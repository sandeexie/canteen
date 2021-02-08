package com.github.canteen.rpc;

import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.InboxMessage;
import com.github.canteen.rpc.message.RPCStatus;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 收件箱
 * 用于存储接收到的消息,并且可以进行批处理
 */
public class Inbox implements Serializable {

	private static final Logging logging= LoggingFactory.create();

	private static final String RPC_PREFIX="rpc://";

	private Object lock=new Object();

	// 收件箱名称
	private String inboxName;

	// RPC通信状态(当前收件箱)
	private RPCStatus rpcStatus;

	// 收件箱运行状态
	private boolean stopped;

	// 收件箱
	private BlockingQueue<InboxMessage> inboxMessages;

	/**
	 * 初始化收件箱,初始完成的收件箱处于关闭状态,不可以接受消息
	 * @param inboxName 收件箱名称
	 */
	public Inbox(String inboxName,String host,int port,String protocol){
		this.inboxName=inboxName;
		stopped=true;
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
	 * 接受消息
	 * @param message 消息
	 */
	public synchronized boolean receive(InboxMessage message,int maxRetries,int timeout){
		assert rpcStatus==RPCStatus.CONNECTED;
		boolean flag=false;
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
	public void process(BiConsumer consumer){
		assert !this.stopped;
		inboxMessages.forEach(message-> {
			try{
				// TODO 暂时不作存储
				consumer.accept(message,null);
			}catch (Exception e){
				logging.logWarning("Process element "+message+" on failure. Because "+ e.getMessage());
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
