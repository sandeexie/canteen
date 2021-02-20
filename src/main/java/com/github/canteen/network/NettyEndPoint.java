package com.github.canteen.network;

import com.github.canteen.network.codec.Codec;
import com.github.canteen.network.codec.InboxFilter;
import com.github.canteen.rpc.RPCAddress;
import com.github.canteen.network.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Netty点对点通信
 *
 * 同时持有Netty服务端和客户端的功能
 */
public class NettyEndPoint {

	private static final Logger logger= LoggerFactory.getLogger(NettyEndPoint.class);

	// 唯一RPC地址
	private RPCAddress rpcAddress;

	// 事件环,仅仅一个
	private EventLoopGroup group;

	// 提供客户端功能,只有一个处理机
	private Bootstrap bootstrap;

	// 提供服务端功能
	private ServerBootstrap serverBootstrap;

	// 缓存消息
	private BlockingQueue<Message> messages;

	private Channel inboxChannel;

	private Channel outboxChannel;

	// 入站处理逻辑
	private Function inboxHandlers;

	// 出站处理逻辑
	private Function outboxHandlers;

	// 链接映射表
	private Map<RPCAddress,ChannelFuture> dispatch;

	// 运行状态
	private boolean running;

	// 链接状态
	private boolean connected;

	private Object lock=new Object();

	public NettyEndPoint(String host,
	                     int port,
	                     Function inboxHandlers,
	                     Function outboxHandlers){
		this.rpcAddress=new RPCAddress(host,port);
		this.bootstrap=new Bootstrap();
		this.running=false;
		this.connected=false;
		this.dispatch=new ConcurrentHashMap<>();
		this.serverBootstrap=new ServerBootstrap();
		this.messages=new LinkedBlockingQueue<>();
		this.inboxHandlers=inboxHandlers;
		this.outboxHandlers=outboxHandlers;
		this.inboxChannel=null;
		this.outboxChannel=null;
	}


	public NettyEndPoint(String host,
	                      int port){
		this(host,port,null,null);
	}

	public synchronized void setEventLoopGroup(EventLoopGroup group){
		assert this.group==null;
		this.group=group;
	}

	public synchronized void releaseEventLoopGroup(EventLoopGroup group){
		this.group=null;
	}

	public void start(){
		synchronized (lock){
			this.bootstrap.group(group);
			this.serverBootstrap.group(group);
			this.bootstrap.channel(NioSocketChannel.class);
			this.serverBootstrap.channel(NioServerSocketChannel.class);
			this.running=true;
			logger.info("EndPoint {} has been started.",rpcAddress.toString());
		}
	}

	/**
	 * 停止之后只提供消息存储功能
	 */
	public void stop(){
		synchronized (lock){
			assert running && !connected && group==null;
			this.running=false;
			this.bootstrap=null;
			this.serverBootstrap=null;
		}
	}

	public ChannelFuture getConnection(RPCAddress address){
		return dispatch.get(address);
	}

	/**
	 * 连接服务器
	 * @param endPoint RPC端点
	 * @return
	 */
	public ChannelFuture connect(NettyEndPoint endPoint,
	                             Codec codec,
	                             int buffSize,
	                             boolean isSilent) throws InterruptedException {
		if(dispatch.containsKey(endPoint.getRpcAddress()))
			return dispatch.get(endPoint.getRpcAddress()).addListener(new StardardListener());

		logger.info("can not find connection in cache. try to new a connection.");

		ChannelFuture connection=null;
		String host= endPoint.getRpcAddress().getHost();
		int port=endPoint.getRpcAddress().getPort();
		if(!running){
			logger.warn("can not connect server {}:{}. because your client is not started.",host,port);
		}else {
			assert endPoint.running;

			// 服务端链接
			if(endPoint.inboxChannel!=null){
				logger.warn("Rpc Address {} is busy. Can not afford a connection.",endPoint.rpcAddress.toString());
				return null;
			}
			endPoint.serverBootstrap.childHandler(new InboxFilter(endPoint.rpcAddress,codec,buffSize,true,isSilent));

			synchronized (lock){
				connected=true;
				bootstrap.handler(new InboxFilter(rpcAddress,codec,buffSize));
				connection = bootstrap.connect(host, port);
			}

			dispatch.put(endPoint.getRpcAddress(),connection);
			outboxChannel=connection.channel();

			endPoint.inboxChannel=outboxChannel;
			logger.info("connected server {}:{} successfully.",host,port);
		}
		return connection.addListener(new StardardListener());
	}

	public void disconnect(NettyEndPoint endPoint) {
		if(dispatch.containsKey(endPoint.getRpcAddress())){
			dispatch.remove(endPoint.getRpcAddress());
			this.outboxChannel.close().addListener(new StardardListener());
			this.outboxChannel=null;
			logger.info("Remove connection of {} from dispatcher.",
					endPoint.getRpcAddress().toString());
		}else{
			logger.info("Remove connection of {} on failure. " +
					"Because this connection is not signed up in dispatch.",
					endPoint.getRpcAddress().toString());
		}

		if(endPoint.inboxChannel.isActive()){
			endPoint.inboxChannel.close().addListener(new StardardListener());

			endPoint.inboxChannel=null;
			logger.info("Remove connetion of server {} successfully",
					endPoint.rpcAddress.toString());
		}
		// TODO 初始化bootstrap 和对端serverBootstrap
	}

	public ChannelFuture startServerSocket(){
		if(!running){
			logger.warn("you have not start your server.please check again.");
			return null;
		}
		ChannelFuture future = this.serverBootstrap.bind(this.rpcAddress.getPort());
		logger.info("Server socket has been started in port {}",rpcAddress.getPort());
		return future;
	}

	public ChannelFuture closeServerSocket(ChannelFuture future){
		return future.channel().closeFuture();
	}

	public synchronized EventLoopGroup disableEventLoopGroup(){
		EventLoopGroup back=this.group;
		this.group=null;
		return back;
	}

	public RPCAddress getRpcAddress() {
		return rpcAddress;
	}

	public ChannelFuture send(Message message,NettyEndPoint endPoint){
		if(!running){
			logger.warn("can not send message to {}",endPoint.rpcAddress.toString());
			return null;
		}
		if(!connected || dispatch.get(endPoint.rpcAddress)==null){
			logger.warn("can not send message to {}. because tcp is not established.",rpcAddress.toString());
			return null;
		}
		if(outboxHandlers!=null){
			message = (Message)outboxHandlers.apply(message);
		}
		return outboxChannel.writeAndFlush(message).addListener(new StardardListener<Message>());
	}

	@SuppressWarnings("Just for test")
	public ChannelFuture sendForTest(Message message,NettyEndPoint endPoint){
		logger.info("{} send message\n {}",rpcAddress.toString(),message.toString());
		return send(message,endPoint);
	}

	public void sendSync(Message message,NettyEndPoint endPoint) throws InterruptedException {
		send(message,endPoint).sync();
	}

	/**
	 * 接受来自{@link NettyEndPoint}的消息
	 * @param endPoint 消息发出点
	 * @throws InterruptedException
	 */
	public void receive(NettyEndPoint endPoint) throws InterruptedException {
		ChannelFuture future=endPoint.dispatch.get(rpcAddress);
		if(future==null){
			logger.warn("There is no connection between "+endPoint.getRpcAddress().toString());
			return;
		}
		future.addListener(new StardardListener());
	}
}
