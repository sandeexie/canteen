package com.github.canteen.rpc;

import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * <h2>节点分发器</h2>
 * <em>
 *     维护RPC节点信息,用于将消息路由到指定的RPC节点上
 * </em>
 */
public class Dispatcher {

	private static final Logging logging= LoggingFactory.create();

	// RPC端点映射表
	private ConcurrentMap<String,MessageLoop> messageLoopMapping=new ConcurrentHashMap<>();

	private ConcurrentMap<RPCEndPoint,RPCEndPointRef> endPointRefMapping=new ConcurrentHashMap<>();

	private CountDownLatch shutdownLatch= new CountDownLatch(1);

	private boolean stopped=false;

	private Object lock= new Object();

	/**
	 * 注册指定名称指定RPC端点到分发器中
	 * @param name RPC端点名称
	 * @param rpcEndPoint RPC端点
	 */
	public void register(String name,RPCEndPoint rpcEndPoint){
		synchronized (lock){
			if(stopped)
				throw new IllegalStateException("Dispatcher has been stopped.It can not work.");
			if(endPointRefMapping.containsKey(name))
				throw new IllegalArgumentException(name+
						" has already in dispatcher. Do not register twice.");
			RPCEndPointRef rpcEndPointRef = rpcEndPoint.getSelf();
			endPointRefMapping.put(rpcEndPoint,rpcEndPointRef);
			MessageLoop messageLoop=null;

		}
	}

}
