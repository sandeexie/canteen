package com.github.canteen.rpc;

import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.InboxMessage;
import com.github.canteen.rpc.message.Message;
import com.github.canteen.rpc.message.RPCStatus;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 收件箱
 * 接收来自消息环{@link MessageLoop}的消息,并进行处理
 */
public class Inbox implements Serializable {

	private static final Logging logging= LoggingFactory.create();

	private static final String RPC_PREFIX="rpc://";

	private Object lock=new Object();

	// 收件箱名称
	private String inboxName;

	// RPC通信状态
	private RPCStatus rpcStatus;

	// 收件箱运行状态
	private boolean stopped;

	private RPCEndPoint rpcEndPoint;

	// 收件箱
	private BlockingQueue<InboxMessage> inboxMessages;

	/**
	 * 初始化收件箱,初始完成的收件箱处于关闭状态
	 * @param inboxName 收件箱名称
	 */
	public Inbox(String inboxName,String host,int port,String protocol){
		this.inboxName=inboxName;
		stopped=true;
		RPCAddress rpcAddress = new RPCAddress(host, port,protocol);
		this.rpcEndPoint=new RPCEndPoint(RPC_PREFIX+this.inboxName,rpcAddress);
	}

	/**
	 * 启动收件箱
	 */
	public void start(){
		assert this.stopped;
		synchronized (lock){
			this.stopped=false;
			rpcStatus=RPCStatus.INITIALIZED;
		}
		logging.logInfo("Inbox of "+inboxName+" has been started.");
	}

	/**
	 * 停止收件箱
	 */
	public void stop(){
		assert !this.stopped;
		synchronized (lock){
			this.stopped=true;
			rpcStatus=RPCStatus.DESTROYED;
		}
		logging.logInfo("Inbox of "+inboxName+" has been stopped.");
	}

	/**
	 * 将RPC端点设置为启动状态,用于接收RPC消息
	 * @param callback
	 */
	public void initRpcEndpoint(Consumer callback){
		assert !this.stopped;
		assert rpcStatus!=RPCStatus.CONNECTED && rpcStatus!=RPCStatus.STARTED;
		synchronized (lock){
			try{
				RPCEndPointRef ref=new RPCEndPointRef() {
					// TODO 收件箱需要完成与消息环的通信 RPC地址填写消息环中映射的地址
					@Override
					public RPCAddress getAddress() {
						return null;
					}

					// 收件箱不支持消息发送
					@Override
					public void send(Message message) {
						throw new UnsupportedOperationException();
					}

					// TODO 收件箱需要在需要ack消息中处理与消息环的通信
					@Override
					public Future ask(Message message, long timeout) {
						return null;
					}
				};
				rpcEndPoint.setSelf(ref);
				rpcStatus=RPCStatus.STARTED;
			}catch (Exception e){
				logging.logWarning("RPC Endpoint started on failure.",e);
			}
		}

	}

	/**
	 * 从{@link MessageLoop}中接收消息
	 */
	public void receive(InboxMessage message){
		receive(message, Configuration.DEFAULT_RPC_REICEIVE_UPPER_MICROSECONDS);
	}


	/**
	 * 接受消息
	 * TODO 支持ARQ重传
	 * @param message 消息
	 * @param timeout 时限
	 */
	public void receive(InboxMessage message,long timeout){
		assert rpcStatus==RPCStatus.CONNECTED;
		try {
			inboxMessages.offer(message,timeout, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			rpcStatus=RPCStatus.ERROR;
			logging.logWarning("Message "+message.toString()+" can" +
					"not be received in "+timeout+" ms.");
		}
	}

	/**
	 * 处理当前收件箱中的消息
	 * 可以在RPC通信无法使用时处理
	 * @param consumer 处理函数
	 * @exception Exception 处理时遇到的异常
	 */
	public void process(Consumer consumer){
		assert !this.stopped;
		inboxMessages.forEach(x-> {
			try{
				consumer.accept(x);
			}catch (Exception e){
				logging.logWarning("Process element "+x+" on failure.");
			}
		});
	}

	/**
	 * 处理当前邮箱中的消息
	 * 可以在RPC通信无法正常使用时进行
	 * @param function 映射函数
	 * @exception Exception 处理时遇到的异常
	 */
	public void transform(Function function){
		assert !this.stopped;
		inboxMessages.forEach(x->{
			try{
				x= (InboxMessage) function.apply(x);
			}catch (Exception e){
				logging.logWarning("Transform element "+x+" on failure.");
			}
		});
	}

}
