package com.github.canteen.rpc;

import com.github.canteen.rpc.message.InboxMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息环
 * 用于接收来自发件箱{@link Outbox} 的消息, 经由内部的映射组件,将消息发送给需要接受消息的
 * 收件箱{@link Inbox} 属于收发的接口层.
 */
public abstract class MessageLoop {

	// 处于激活状态的收件箱列表
	private LinkedBlockingQueue<Inbox> active=new LinkedBlockingQueue<>();

	protected Runnable receiveLoopRunnable=new Runnable() {
		@Override
		public void run() {
			receiveLoop();
		}
	};

	private boolean stopped=false;

	protected ExecutorService threadpool;

	public abstract void post(String endpointName, InboxMessage message);

	public abstract void unregister(String name);

	protected final void setActive(Inbox inbox){
		active.offer(inbox);
	}

	public synchronized void stop(){

	}

	private void receiveLoop(){
	}

}
