package com.github.canteen.rpc;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * RPC通信实际的处理结构
 * @author xyf
 * @date 2020-11-21
 */
public abstract class RPCEndPointRef implements Serializable {

	private int maxRetries;

	private long retryWaitMs;

	private long defaultAskTimeout;

	public abstract RPCAddress getAddress();

	public abstract String getEndPointName();

	// 发送单向异步消息
	public abstract void send(Object message);

	// 异步远端调用
	public abstract Future ask(Object message,long timeout);

	public Future ask(Object message){
		return ask(message,defaultAskTimeout);
	}

	// 同步远端调用
	public Object askSync(Object message,long timeout){
		Future future = ask(message, timeout);
		try {
			return future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object askSync(Object message){
		Future future = ask(message, defaultAskTimeout);
		try {
			return future.get();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
