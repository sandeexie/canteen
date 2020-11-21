package com.github.canteen.rpc;

import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.github.canteen.rpc.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * 收件箱
 * 接收来自消息环{@link MessageLoop}的消息,并进行处理
 */
public class Inbox {

	private static final Logging logging= LoggingFactory.create();

	private String name;

	private boolean stopped;

	private BlockingQueue<Message> inboxMessages;


	/**
	 * 从MessageLoop中接收消息
	 */
	public void receive(){

	}
	/**
	 * 处理当前收件箱中的消息
	 */
	public void process(){
		if(stopped){

			return;
		}
	}

}
