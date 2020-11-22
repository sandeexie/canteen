package com.github.canteen.rpc;

import com.github.canteen.internal.ConfigReader;
import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.Message;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * RPC通信实际的处理结构
 * @author xyf
 * @date 2020-11-21
 */
public abstract class RPCEndPointRef implements Serializable {

	private static final ConfigReader reader=ConfigReader.getReader();

	private static final Logging logging= LoggingFactory.create();

	private int maxRetries;

	private long retryWaitMs;

	private long rpcAskTimeout;

	public RPCEndPointRef(){
		this.maxRetries= Integer.parseInt(
				reader.get(
						Configuration.RPC_MAX_RETRY_TIMES,
						String.valueOf(
								Configuration.DEFAULT_RPC_MAX_RETRY_TIMES)
				)
		);
		this.retryWaitMs=Integer.parseInt(
				reader.get(
						Configuration.RPC_WAITING_MICROSECONDS,
						String.valueOf(
								Configuration.DEFAULT_RPC_WAITING_MICROSECONDS)
				)
		);
		this.rpcAskTimeout=Integer.parseInt(
				reader.get(
						Configuration.RPC_ASK_TIMEOUT,
						String.valueOf(
								Configuration.DEFAULT_RPC_ASK_TIMEOUT)
				)
		);
	}

	public RPCEndPointRef(int maxRetries,int retryWaitMs,int rpcAskTimeout){
		this.maxRetries=maxRetries;
		this.retryWaitMs=retryWaitMs;
		this.rpcAskTimeout=rpcAskTimeout;
	}

	// 获取通信对端RPC地址
	public abstract RPCAddress getAddress();

	// 发送单向异步消息
	public abstract void send(Message message);

	// 异步远端调用
	public abstract Future ask(Message message,long timeout);

	public Future ask(Message message){
		return ask(message,rpcAskTimeout);
	}

	// 同步远端调用
	public Object askSync(Message message,long timeout){
		Future future = ask(message, timeout);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}

	public Object askSync(Message message){
		Future future = ask(message, rpcAskTimeout);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}
}
