package com.github.canteen.rpc;

import com.github.canteen.internal.ConfigReader;
import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.Message;
import com.github.canteen.rpc.message.OutboxMessage;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * RPC通信实际的处理结构
 * @author xyf
 * @date 2020-11-21
 */
public abstract class RPCEndPointExecutor implements Serializable {

	private static final ConfigReader reader=ConfigReader.getReader();

	private static final Logging logging= LoggingFactory.create();

	private int maxRetries;

	private RPCAddress rpcAddress;

	private long retryWaitMs;

	private long rpcAskTimeout;

	public RPCEndPointExecutor(RPCAddress rpcAddress){
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
		this.rpcAddress=rpcAddress;
	}

	public RPCEndPointExecutor(int maxRetries, int retryWaitMs, int rpcAskTimeout, RPCAddress rpcAddress){
		this.maxRetries=maxRetries;
		this.retryWaitMs=retryWaitMs;
		this.rpcAskTimeout=rpcAskTimeout;
		this.rpcAddress=rpcAddress;
	}


	public int getMaxRetries() {
		return maxRetries;
	}

	public long getRetryWaitMs() {
		return retryWaitMs;
	}

	public long getRpcAskTimeout() {
		return rpcAskTimeout;
	}

	// 获取通信对端RPC地址
	public RPCAddress getAddress(){
		return rpcAddress;
	}

	// TODO 发送单向异步消息,从当前RPC端点发送到另一个RPC端点,采用nio对阻塞io优化
	public void send(Message message,RPCEndPoint endPoint){
		assert message instanceof OutboxMessage;

	}

	// 异步远端调用
	public Future ask(Message message,long timeout,RPCEndPoint endPoint){
		assert message instanceof OutboxMessage;
		assert endPoint.getSelf()!=null;

		return null;
	}

	public Future ask(Message message,RPCEndPoint endPoint){
		return ask(message,rpcAskTimeout,endPoint);
	}

	// 同步远端调用
	public Object askSync(Message message,long timeout,RPCEndPoint endPoint){
		Future future = ask(message, timeout,endPoint);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}

	public Object askSync(Message message,RPCEndPoint endPoint){
		Future future = ask(message, rpcAskTimeout,endPoint);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}

	// TODO 回复收件箱中的邮件消息到发送端
	public Future reply(Message message,RPCEndPoint endPoint){
		return null;
	}

	public Future reply(Message message,long timeout,RPCEndPoint endPoint){
		return reply(message,rpcAskTimeout,endPoint);
	}

	public Object replySync(Message message,RPCEndPoint endPoint){
		Future future=reply(message,endPoint);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}

	public Object replySync(Message message,long timeout,RPCEndPoint endPoint){
		Future future=reply(message,timeout,endPoint);
		try {
			return future.get();
		} catch (Exception e) {
			logging.logWarning("Remote request of "+message + "has failed.",e);
			return null;
		}
	}
}
