package com.github.canteen.rpc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <h2>节点分发器</h2>
 * <em>
 *     维护RPC节点信息,用于将消息路由到指定的RPC节点上
 * </em>
 */
public class Dispatcher {

	// RPC端点映射表
	protected static ConcurrentMap<String,MessageLoop> messageLoopMapping=new ConcurrentHashMap<>();

	protected static ConcurrentMap<String,RPCEndPoint> endPointMap=new ConcurrentHashMap<>();

}
