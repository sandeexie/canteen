package com.github.canteen.rpc;

import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.InboxMessage;
import com.github.canteen.rpc.message.Message;
import com.github.canteen.rpc.message.OutboxMessage;
import com.github.canteen.rpc.message.RPCStatus;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
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

	// ACK信息 默认开启
	private OutboxMessage ack;

	private boolean needAck;

	private int maxRecoveryTimes=-1;

	/**
	 * 初始化收件箱,初始完成的收件箱处于关闭状态,不可以接受消息
	 * @param inboxName 收件箱名称
	 */
	public Inbox(String inboxName,String host,int port,String protocol){
		this.inboxName=inboxName;
		stopped=true;
		RPCAddress rpcAddress = new RPCAddress(host, port,protocol);
		this.rpcEndPoint=new RPCEndPoint(RPC_PREFIX+this.inboxName,rpcAddress);
	}

	public void setMaxRecoveryTimes(int maxRecoveryTimes) {
		this.maxRecoveryTimes = maxRecoveryTimes;
	}

	/**
	 * 启动收件箱
	 */
	public void start(){
		synchronized (lock){
			assert this.stopped;
			this.stopped=false;
			rpcStatus=RPCStatus.INITIALIZED;
		}
		logging.logInfo("Inbox of "+inboxName+" has been started.");
	}

	/**
	 * 停止收件箱
	 */
	public void stop(){
		synchronized (lock){
			assert !this.stopped;
			this.stopped=true;
			rpcStatus=RPCStatus.DESTROYED;
		}
		logging.logInfo("Inbox of "+inboxName+" has been stopped.");
	}

	/**
	 * 将RPC端点设置为启动状态,用于接收RPC消息
	 * @param callback 回调函数
	 * @param useCallBack 使用使用回调函数
	 */
	public void initRpcEndpoint(BooleanSupplier callback,boolean useCallBack){
		assert !this.stopped;
		synchronized (lock){
			assert rpcStatus!=RPCStatus.CONNECTED && rpcStatus!=RPCStatus.STARTED;
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
						if(!needAck)
							throw new UnsupportedOperationException();
						else {
							// TODO 发送ACK给消息环
						}
					}

					// TODO 收件箱需要在需要ack消息中处理与消息环的通信
					@Override
					public Future ask(Message message, long timeout) {
						return null;
					}
				};
				rpcEndPoint.setSelf(ref);
				rpcStatus=RPCStatus.STARTED;
				if(useCallBack || null==callback){
					rpcEndPoint.onStart(callback);
				}else {
					rpcEndPoint.onStart();
				}
			}catch (Exception e){
				logging.logWarning("RPC Endpoint started on failure.",e);
			}
		}
	}

	/**
	 * TODO Dispatcher和MessageLoop完毕之后回来处理验证信息问题
	 * 对处于通信失败的节点进行测试,如果在规定次数内连接上,则修改为{@code RPCStatus.CONNECTED}
	 * 否则将其视作断开连接{@code RPCStatus.DISCONNECTED}
	 * @return 连上的标志位,为<code>true</code>表示连接成功
	 */
	public boolean recoverState(){
		int recoveryTimes=maxRecoveryTimes<0? Configuration.DEFAULT_RPC_RECOVER_TRY_TIMES:maxRecoveryTimes;
		boolean flag=false;
		while (recoveryTimes>0 && !flag){
			// TODO 发送一条连接消息给消息环,并获取响应结果
			Future askTask=rpcEndPoint.getSelf().ask(null);
			try {
				Message message= (Message) askTask.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 停止RPC通信功能,将RPC端点设置为停止状态
	 * @param callback 回调函数
	 * @param useCallBack 是否使用默认回调
	 */
	public void stopRpcEndpoint(BooleanSupplier callback,boolean useCallBack){
		assert !this.stopped;
		synchronized (lock){
			assert rpcStatus!=RPCStatus.STOPPED && rpcStatus!=RPCStatus.DESTROYED;
			try {
				rpcEndPoint.setSelf(null);
				rpcStatus=RPCStatus.STOPPED;
				if(useCallBack || null==callback){
					rpcEndPoint.onStart(callback);
				}else {
					rpcEndPoint.onStart();
				}
			} catch (Exception e){
				logging.logWarning("RPC Endpoint stopped on failure.",e);
			}
		}
	}

	/**
	 * 接受消息
	 * @param message 消息
	 */
	public synchronized boolean receive(InboxMessage message){
		assert rpcStatus==RPCStatus.CONNECTED;
		boolean flag=false;
		int maxRetries = rpcEndPoint.getSelf().getMaxRetries();
		long timeout=rpcEndPoint.getSelf().getRetryWaitMs();
		while (maxRetries>0 && !flag){
			try {
				inboxMessages.offer(message,timeout, TimeUnit.MICROSECONDS);
				flag=true;
			} catch (InterruptedException e) {
				logging.logWarning("Message "+message.toString()+" can" +
						"not be received in "+timeout+" ms. Now try to redo it.");
				maxRetries--;
			}
		}
		if(!flag){
			logging.logWarning("Message has not be received in given "+ maxRetries +" times. Please check network state.");
			rpcStatus=RPCStatus.ERROR;
		}
		return flag;
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
				logging.logWarning("Process element "+x+" on failure. Because "+ e.getMessage());
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
				logging.logWarning("Transform element "+x+" on failure. Because "+e.getMessage());
			}
		});
	}

}
