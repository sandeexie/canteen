package com.github.canteen.rpc;

/**
 * RPC通信节点
 */
public class RPCEndPoint {

	// 节点名称
	public String endPointName;

	/**
	 * 使用{@link RPCEndPointRef}进行生命周期设计
	 */
	public RPCEndPointRef self;

	/**
	 * 启动回调函数,一般情况下当<code>self</code>被赋值的时候
	 * 会调用这个回调
	 */
	public void onStart(){

	}

	/**
	 * 异常处理回调函数
	 * @param cause 异常发生的原因
	 * @throws Throwable
	 */
	public void onError(Throwable cause) throws Throwable {
		throw cause;
	}

	/**
	 * 连接到rpc端点时候的回调
	 * @param rpcAddress 需要连接的RPC端点
	 */
	public void onConnected(RPCAddress rpcAddress){

	}

	/**
	 * 通信端点无法联系到的时候调用
	 * @param rpcAddress 通信的RPC地址
	 */
	public void onDisconnected(RPCAddress rpcAddress){

	}

	/**
	 * 停止RPC端点工作的方法,<code>self</code>会被置空,这个方法不应当去发送消息
	 */
	public void onStop(){

	}
}
