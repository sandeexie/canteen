package com.github.canteen.rpc;

import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * RPC通信节点
 * 包括:
 * 1. 收件箱 {@link Inbox}
 * 2. 发件箱 {@link Outbox}
 * 3. RPC处理器 {@link RPCEndPointExecutor}
 * 4. 唯一RPC定位表示 {@link RPCAddress}
 * 包含对RPC处理器的生命周期控制
 */
public class RPCEndPoint {

	private static final Logging logging= LoggingFactory.create();
	// 节点名称
	private String endPointName;

	private RPCAddress rpcAddress;

	// 绑定的收件箱
	private Inbox inbox;

	private BlockingQueue<Message> cache;

	// 绑定的发件箱
	private Outbox outbox;

	// 是否可用
	private boolean isAvailable;

	private Object lock=new Object();

	/**
	 * 使用{@link RPCEndPointExecutor}进行生命周期设计
	 */
	private RPCEndPointExecutor self;

	public RPCEndPoint(String endPointName,RPCAddress rpcAddress,Inbox inbox,Outbox outbox){
		this.endPointName=endPointName;
		this.rpcAddress=rpcAddress;
		this.inbox=inbox;
		this.outbox=outbox;
		this.isAvailable=false;
	}

	public void store(Message message){
		this.cache.offer(message);
	}

	public Message fetch(Message message){
		return this.cache.poll();
	}

	public void startRPCEndPoint() throws Throwable {
		assert this.isAvailable==false;
		synchronized (lock){
			try {
				this.isAvailable=true;
				onStart();
				Dispatcher.endPointMap.put(endPointName,this);
				Dispatcher.messageLoopMapping.put(endPointName,new MessageLoop());
			}catch (Throwable cause){
				logging.logWarning("start rpc endpoint" +endPointName+" on failure.");
				onError(cause);
			}
		}
	}

	public void startRPCEndPoint(BooleanSupplier startCallback,Consumer errorCallback) throws Throwable {
		assert this.isAvailable==false;
		synchronized (lock){
			try {
				this.isAvailable=true;
				onStart(startCallback);
				Dispatcher.endPointMap.put(endPointName,this);
				Dispatcher.messageLoopMapping.put(endPointName,new MessageLoop());
			}catch (Throwable cause){
				onError(cause,errorCallback);
			}
		}
	}

	public void stopRPCEndPoint() throws Throwable {
		assert this.isAvailable==true;
		synchronized (lock){
			try {
				this.isAvailable=false;
				onStop();
				Dispatcher.endPointMap.remove(endPointName);
				Dispatcher.messageLoopMapping.remove(endPointName);
			}catch (Throwable cause){
				onError(cause);
			}
		}
	}

	public boolean isStarted(){
		return this.isAvailable;
	}

	public void stopRPCEndPoint(BooleanSupplier stopCallback,Consumer errorCallback) throws Throwable {
		assert this.isAvailable==true;
		synchronized (lock){
			try {
				this.isAvailable=false;
				onStop(stopCallback);
				Dispatcher.endPointMap.remove(endPointName);
				Dispatcher.messageLoopMapping.remove(endPointName);
			}catch (Throwable cause){
				onError(cause,errorCallback);
			}

		}
	}

	/**
	 * 启动回调函数,一般情况下当<code>self</code>被赋值的时候
	 * 会调用这个回调
	 * @param callback 回调函数
	 */
	private void onStart(BooleanSupplier callback){
		callback.getAsBoolean();
	}

	private void onStart(){
		logging.logInfo("Rpc Endpoint "+rpcAddress.toString()+" has been started.");
	}

	/**
	 * 异常处理回调函数
	 * @param cause 异常发生的原因
	 * @param callback 回调函数
	 * @throws Throwable
	 */
	private void onError(Throwable cause,Consumer callback) throws Throwable {
		callback.accept(cause);
	}

	private void onError(Throwable cause) throws Throwable{
		logging.logError("There occurred error in rpc module "+cause.getMessage());
	}

	/**
	 * 通信端点无法联系到的时候调用
	 * @param rpcAddress 通信的RPC地址
	 * @param callback 回调函数
	 */
	private void onDisconnected(RPCAddress rpcAddress,Consumer callback){
		callback.accept(rpcAddress);
	}

	private void onDisconnected(RPCAddress rpcAddress){
		logging.logInfo("Disconnected "+rpcAddress.toString()+" successfully.");
	}

	private void onStop(BooleanSupplier callback){
		callback.getAsBoolean();
	}

	/**
	 * 停止RPC端点工作的方法,<code>self</code>会被置空,这个方法不应当去发送消息
	 */
	private void onStop(){
		logging.logInfo("RPC EndPoint"+ rpcAddress.toString() +" has been shut down.");
	}

	public void setEndPointName(String endPointName) {
		this.endPointName = endPointName;
	}

	public String getEndPointName() {
		return endPointName;
	}

	public void setSelf(RPCEndPointExecutor self) {
		this.self = self;
	}

	public RPCEndPointExecutor getSelf() {
		return self;
	}

	public RPCAddress getRpcAddress() {
		return rpcAddress;
	}

	public void setRpcAddress(RPCAddress rpcAddress) {
		this.rpcAddress = rpcAddress;
	}
}
