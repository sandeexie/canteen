package com.github.canteen.network;

import com.github.canteen.rpc.message.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.concurrent.*;

public class TransportClient implements Closeable {

	private static final Logger logger= LoggerFactory.getLogger(TransportClient.class);

	private final Channel channel;

	private String clientId;

	private boolean running;

	private Object lock=new Object();

	private BlockingQueue<Message> cachedMessage;

	public TransportClient(Channel channel,String clientId) {
		this.channel = channel;
		this.clientId=clientId;
		this.running=false;
		this.cachedMessage=new LinkedBlockingQueue<>();
	}

	public void start(){
		synchronized (lock){
			this.running=true;
			if(null!=clientId)
				logger.warn("client "+clientId+" is running. ");
			else
				logger.warn("anonymous client has started.");
		}
	}

	public SocketAddress getSocketAddress(){
		return channel.remoteAddress();
	}

	public Channel getChannel() {
		return channel;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void send(Message message, RpcCallBack callback){
		assert channel!=null;
		if(logger.isTraceEnabled()){
			logger.trace("sending rpc to {}",
					channel.remoteAddress().toString());
		}
		long requestId=getRequestId();
//		RpcChannelListener listener=new RpcChannelListener(this.channel,requestId,callback);
		channel.writeAndFlush(message);
	}

	private static long getRequestId(){
		return Math.abs(UUID.randomUUID().getLeastSignificantBits());
	}

	public void sendSync(Message message,long timeout){

		CompletableFuture<Boolean> future=CompletableFuture.supplyAsync(
				()->{
					send(message, new RpcCallBack() {
						@Override
						public void onSuccess(Message response) {
							try {
								cachedMessage.offer(response);
							}catch (Exception cause){
								onFailure(cause);
							}
						}

						@Override
						public void onFailure(Exception cause) {
							logger.error("send rpc on failure because "+cause.getMessage());
						}
					});
					return true;
				}
		);
		try {
			future.get(timeout, TimeUnit.MICROSECONDS);
		} catch (InterruptedException e) {
			logger.error("sync task run on failure  because {}"+e.getMessage());
		} catch (TimeoutException e) {
			logger.error("sync task run on failure because task run beyond given limit "+timeout+" ms. "+e.getMessage());
		}catch (Exception e){
			logger.error("sync task run on failure because "+e.getMessage());
		}
	}

	@Override
	public void close() throws IOException {
		channel.close().awaitUninterruptibly(10, TimeUnit.SECONDS);
		synchronized (lock){
			assert this.running;
			this.running=false;
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("remoteAddress "+channel.remoteAddress())
				.append("clientId "+(clientId==null?"anonymous":clientId))
				.append("Status"+(running?"Running":"Stopped"))
				.toString();
	}
}
