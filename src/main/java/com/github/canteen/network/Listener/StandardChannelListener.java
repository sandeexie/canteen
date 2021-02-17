package com.github.canteen.network.Listener;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StandardChannelListener implements GenericFutureListener<Future<?>> {

	private static final Logger logger= LoggerFactory.getLogger(StandardChannelListener.class);

	private final long startTime;

	private final Channel channel;

	private final Object requestId;

	public StandardChannelListener(Channel channel, Object requestId) {
		this.channel = channel;
		this.startTime = System.currentTimeMillis();
		this.requestId = requestId;
	}

	@Override
	public void operationComplete(Future future) throws Exception {
		assert channel!=null;
		if(future.isSuccess()){
			if(logger.isTraceEnabled()){
				logger.trace("send request {} to {} took {} ms",
						requestId,
						channel.remoteAddress().toString(),
						System.currentTimeMillis()-startTime);
			}
		}else{
			String msg="Failed to send rpc message "+requestId+" to "+channel.remoteAddress().toString();
			logger.warn(msg,future.cause());
			channel.close();
			try {
				handleFailure(msg,future.cause());
			}catch (Exception cause){
				logger.error("Unhandled problem in rpc connection with "+channel.remoteAddress().toString());
			}
		}
	}

	public void handleFailure(String msg,Throwable cause) throws Exception{

	}
}
