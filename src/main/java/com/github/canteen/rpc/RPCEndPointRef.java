package com.github.canteen.rpc;

import java.io.Serializable;
import java.util.concurrent.Future;

public abstract class RPCEndPointRef implements Serializable {

	private int maxRetries;

	private long retryWaitMs;

	private long defaultAskTimeout;

	public abstract RPCAddress getAddress();

	public abstract String getEndPointName();

	// 发送单向异步消息
	public abstract void send(Object message);

	public abstract Future ask(Object message,long timeout);

	public Future ask(Object message){
		return ask(message,defaultAskTimeout);
	}

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
