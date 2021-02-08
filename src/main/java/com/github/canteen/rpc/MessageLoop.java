package com.github.canteen.rpc;

import com.github.canteen.internal.Configuration;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.Message;
import com.github.canteen.rpc.message.OutboxMessage;
import com.github.canteen.utils.ParameterUtil;

import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * 消息环
 * 用于接收来自发件箱{@link Outbox} 的消息, 经由内部的映射组件,将消息发送给需要接受消息的
 * 收件箱{@link Inbox} 属于收发的接口层.
 */
public class MessageLoop {

	// 处于激活状态的收件箱列表
	private LinkedBlockingQueue<Inbox> active=new LinkedBlockingQueue<>();

	private static final Logging logging= LoggingFactory.create();

	private BiConsumer<Message,RPCEndPoint> action;

	private Runnable receiveLoopRunnable=new Runnable() {
		@Override
		public void run() {
			receiveLoop(action);
		}
	};

	private boolean stopped=false;

	private ExecutorService threadpool;

	private Object lock=new Object();


	private BlockingQueue<Runnable> workQueue=new LinkedBlockingQueue<>();
	/**
	 * 初始化工作线程
	 * @param corePoolSize 处于工作状态的线程数量
	 */
	public MessageLoop(int corePoolSize, BiConsumer action){


		int maxPoolSize= (int) ParameterUtil.getConfiguration(
				Configuration.MESSAGELOOP_THREAD_POOL_SIZE,
				Configuration.DEFAULT_MESSAGE_THREAD_POOL_SIZE);

		int keepAliveTime=ParameterUtil.getConfiguration(
				Configuration.MESSAGELOOP_THREAD_POOL_KEEPALIVE_TIME,
				Configuration.DEFAULT_MESSAGELOOP_THREAD_POOL_KEEPALIVE_TIME);

		for(int i=0;i<corePoolSize;i++)
			workQueue.offer(receiveLoopRunnable);

		this.threadpool=new ThreadPoolExecutor(
				corePoolSize,
				maxPoolSize,
				keepAliveTime,
				TimeUnit.MICROSECONDS,
				workQueue
		);
		this.action=action;
	}

	public MessageLoop(){
		int coreSize=ParameterUtil.getConfiguration(
				Configuration.MESSAGELOOP_THREAD_POOL_CORE_SIZE,
				Configuration.DEFAULT_MESSAGELOOP_THREAD_POOL_CORE_SIZE
		);
		action=((message, rpcEndPoint) -> {
			assert message instanceof Message;
			assert rpcEndPoint instanceof RPCEndPoint;
			rpcEndPoint.store(message);
		});
 	}

	public void start(){
		assert this.stopped;
		synchronized (lock){
			try {
				this.stopped=false;
			}catch (Exception cause){
				logging.logWarning("Start message loop on failure. because "+
						cause.getMessage());
			}
		}
	}

	public void terminal(){
		assert !this.stopped;
		synchronized (lock){
			try {
				this.stopped=true;
			}catch (Exception cause){
				logging.logWarning("Shut down message loop on failure. beacuse "
						+cause.getMessage());
			}
		}
	}

	/**
	 *
	 * 发送的时候需要做好差错控制,选择路由
	 * @param endpointName RPC端点名称
	 * @param message 发送的消息
	 */
	public void post(String endpointName, OutboxMessage message){
		RPCEndPoint endPoint=Dispatcher.endPointMap.getOrDefault(endpointName,null);
		if(endPoint==null){
			logging.logWarning("Endpoint "+endpointName+" is not in the list of dispatch.");
		}else {
			assert endPoint.isStarted();
			RPCAddress address=endPoint.getRpcAddress();
			// TODO 与address进行通信


		}
	}

	public void unregister(String endPointName){
		RPCEndPoint rpcEndPoint = Dispatcher.endPointMap.remove(endPointName);
		MessageLoop loop = Dispatcher.messageLoopMapping.remove(endPointName);
		if(null!=rpcEndPoint)
			logging.logInfo("unregister rpc endpoint "+ endPointName+" successfully.");
		if(null!=loop)
			logging.logInfo("unregister message loop of endpoint "+endPointName+" successfully.");
	}

	public void register(String endPointName,RPCEndPoint rpcEndPoint,MessageLoop messageLoop){
		RPCEndPoint point = Dispatcher.endPointMap.put(endPointName, rpcEndPoint);
		MessageLoop loop = Dispatcher.messageLoopMapping.put(endPointName, messageLoop);
		if(null!=rpcEndPoint)
			logging.logInfo("register rpc endpoint "+ endPointName+" successfully.");
		if(null!=loop)
			logging.logInfo("register message loop of endpoint "+endPointName+" successfully.");
	}

	/**
	 * 从收件箱中拿到消息并进行处理
	 * @param action 处理函数
	 */
	private void receiveLoop(BiConsumer action){
		try {
			while (true){
				Inbox inbox=active.poll();
				if(null==inbox)
					return;
				inbox.process(action);
			}
		}catch (Throwable cause){
			logging.logWarning("task run on failure. because "+cause.getMessage());
			threadpool.execute(receiveLoopRunnable);
		}
	}

}
