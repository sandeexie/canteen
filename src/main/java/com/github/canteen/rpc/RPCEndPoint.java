package com.github.canteen.rpc;

import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * RPC通信节点
 */
public class RPCEndPoint {

	private static final Logging logging= LoggingFactory.create();
	// 节点名称
	private String endPointName;

	private RPCAddress rpcAddress;

	/**
	 * 使用{@link RPCEndPointRef}进行生命周期设计
	 */
	private RPCEndPointRef self;

	public RPCEndPoint(String endPointName,RPCAddress rpcAddress){
		this.endPointName=endPointName;
		this.rpcAddress=rpcAddress;
	}

	/**
	 * 启动回调函数,一般情况下当<code>self</code>被赋值的时候
	 * 会调用这个回调
	 * @param callback 回调函数
	 */
	public void onStart(BooleanSupplier callback){
		callback.getAsBoolean();
	}

	public void onStart(){
		logging.logInfo("Rpc Endpoint "+rpcAddress.toString()+" has been started.");
	}

	/**
	 * 异常处理回调函数
	 * @param cause 异常发生的原因
	 * @param callback 回调函数
	 * @throws Throwable
	 */
	public void onError(Throwable cause,Consumer callback) throws Throwable {
		callback.accept(cause);
	}

	public void onError(Throwable cause) throws Throwable{
		logging.logError("There occurred error in rpc module "+cause.getMessage());
	}

	/**
	 * 连接到rpc端点时候的回调
	 * @param rpcAddress 需要连接的RPC端点
	 * @param callback   回调函数
	 */
	public void onConnected(RPCAddress rpcAddress,Consumer callback){
		callback.accept(rpcAddress);
	}

	public void onConnected(RPCAddress rpcAddress){
		logging.logInfo("Connected "+rpcAddress.toString()+" successfully.");
	}

	/**
	 * 通信端点无法联系到的时候调用
	 * @param rpcAddress 通信的RPC地址
	 * @param callback 回调函数
	 */
	public void onDisconnected(RPCAddress rpcAddress,Consumer callback){
		callback.accept(rpcAddress);
	}

	public void onDisconnected(RPCAddress rpcAddress){
		logging.logInfo("Disconnected "+rpcAddress.toString()+" successfully.");
	}

	public void onStop(BooleanSupplier callback){
		callback.getAsBoolean();
	}

	/**
	 * 停止RPC端点工作的方法,<code>self</code>会被置空,这个方法不应当去发送消息
	 */
	public void onStop(){
		logging.logInfo("RPC EndPoint"+ rpcAddress.toString() +" has been shut down.");
	}

	public void setEndPointName(String endPointName) {
		this.endPointName = endPointName;
	}

	public String getEndPointName() {
		return endPointName;
	}

	public void setSelf(RPCEndPointRef self) {
		this.self = self;
	}

	public RPCEndPointRef getSelf() {
		return self;
	}
}
