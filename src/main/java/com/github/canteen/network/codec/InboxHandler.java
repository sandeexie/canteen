package com.github.canteen.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class InboxHandler<T> extends ChannelInboundHandlerAdapter {

	private static final Logger logger= LoggerFactory.getLogger(InboxHandler.class);

	private T message;

	private Exception cause;

	private boolean isServer;

	private boolean isSilent=true;

	public InboxHandler(boolean isServer){
		this.cause=null;
		this.isServer=isServer;
	}

	public InboxHandler(boolean isServer,boolean isSilent){
		this.cause=null;
		this.isServer=isServer;
		this.isSilent=isSilent;
	}


	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		logger.info("There is handler added.");

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channel {} -> {}  has been active.",
				ctx.channel().remoteAddress(),
				ctx.channel().localAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channel {} -> {} has been inactive",
				ctx.channel().remoteAddress(),
				ctx.channel().localAddress());
	}



	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.message= (T) msg;
		System.out.println("**************************");
		if(!isSilent)
			logger.info("receive a message \n:{}"+msg.toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(!isServer)
			return;
		boolean flag=false;
		if(evt instanceof IdleStateEvent
				&& IdleState.READER_IDLE.equals(((IdleStateEvent) evt).state())){
			long startTime=System.currentTimeMillis();
			while (IdleState.READER_IDLE.equals(((IdleStateEvent) evt).state())){
				// TODO 自设定参数
				if(System.currentTimeMillis()-startTime>5000){
					flag=true;
					break;
				}
			}
		}
		if(flag)
			ctx.channel().close();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		assert ctx.channel()!=null;
		logger.info("{} has been registered to channel handler context.",ctx.channel().localAddress());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		assert ctx.channel()!=null;
		logger.info("{} has been unregistered to channel handler context.",ctx.channel().localAddress());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

		logger.info("{} has been read completely.",ctx.channel().localAddress());

	}

	public void action(Consumer func){
		func.accept(message);
	}

	public T getMessage() {
		return message;
	}

	public boolean isServer() {
		return isServer;
	}

	public Exception getCause() {
		return cause;
	}
}

