package com.github.canteen.network.Listener;

import com.github.canteen.network.RpcCallBack;
import io.netty.channel.Channel;

import java.io.IOException;

public class RpcChannelListener extends StandardChannelListener {


	private final RpcCallBack callBack;

	public RpcChannelListener(Channel channel, Object requestId, RpcCallBack callBack) {
		super(channel, requestId);
		this.callBack = callBack;
	}

	@Override
	public void handleFailure(String msg, Throwable cause) throws Exception {
		new IOException(msg,cause);
	}
}
