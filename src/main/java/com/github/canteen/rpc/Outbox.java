package com.github.canteen.rpc;

import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.OutboxMessage;
import com.github.canteen.rpc.message.RPCStatus;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 发件箱
 * 用于发送的发件箱
 */
public class Outbox implements Serializable {

	private static final Logging logging= LoggingFactory.create();

	private static final String RPC_PREFIX="rpc://";

	private Object lock=new Object();

	// 发件箱名称
	private String outboxName;

	// RPC通信状态
	private RPCStatus rpcStatus;

	// 收件箱运行状态
	private boolean stopped;


	private BlockingQueue<OutboxMessage> outboxMessages;

	public Outbox(String inboxName,String host,int port,String protocol){
		this.outboxName=inboxName;
		stopped=true;
	}

	public void start(){
		assert this.stopped;
		synchronized (lock){
			this.stopped=false;
			rpcStatus=RPCStatus.INITIALIZED;
		}
		logging.logInfo("Outbox of "+outboxName+" has been started.");
	}

	public void stop(){
		assert !this.stopped;
		synchronized (lock){
			this.stopped=true;
			rpcStatus=RPCStatus.DESTROYED;
		}
		logging.logInfo("Outbox of "+outboxName+" has been stopped.");
	}

	public void send(OutboxMessage message, long timeout){
		assert rpcStatus==RPCStatus.CONNECTED;
		try {
			outboxMessages.offer(message,timeout, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			rpcStatus=RPCStatus.ERROR;
			logging.logWarning("Message "+message.toString()+" can" +
					"not be sent in "+timeout+" ms.");
		}
	}

	public void send(OutboxMessage message){
		send(message, Configuration.DEFAULT_RPC_REICEIVE_UPPER_MICROSECONDS);
	}

	public void process(Consumer consumer){
		assert !this.stopped;
		outboxMessages.forEach(x-> {
			try{
				consumer.accept(x);
			}catch (Exception e){
				logging.logWarning("Process element "+x+" on failure.");
			}
		});
	}

	public void transform(Function function){
		assert !this.stopped;
		outboxMessages.forEach(x->{
			try{
				x= (OutboxMessage) function.apply(x);
			}catch (Exception e){
				logging.logWarning("Transform element "+x+" on failure.");
			}
		});
	}
}
