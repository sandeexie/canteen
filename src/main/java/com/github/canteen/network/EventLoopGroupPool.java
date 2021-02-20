package com.github.canteen.network;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoopGroupPool {

	private static final Logger logger= LoggerFactory.getLogger(EventLoopGroupPool.class);

	private static EventLoopGroup[] groups;

	private static int available;
	// 状态表,false表示可用
	private static boolean[] status;

	private static int pos;

	private static int size;

	public static void allocate(int length){
		size=length;
		groups=new EventLoopGroup[size];
		for (int i = 0; i <size ; i++)
			groups[i]=new NioEventLoopGroup();
		status=new boolean[size];
		available=size;
		pos=0;
	}

	/**
	 * 获取池子中下一个可以的处理机,选取之后,指针指向下一个处理机
	 * @return EventLoopGroup
	 */
	public static EventLoopGroup get(){
		assert size>0 && available>0;
		int start=pos;
		boolean stop=false;
		while (status[start%size]){
			if(stop && start%size==pos%size){
				logger.warn("get event loop group on failure. because there are no available event loop now.");
				return null;
			}
			if(start%size==pos%size)
				stop=true;
			start++;
		}
		status[start%size]=true;
		pos=(start+1)%size;
		available--;
		return groups[start%size];
	}

	public static boolean sendBack(){
		assert size>0 && available<size;
		int start=pos;
		boolean stop=false;
		while (!status[start%size]){
			if(stop && start%size==pos%size){
				logger.warn("can not return event loop to pool. because there are no available event loop now.");
				return false;
			}
			if(start%size==pos%size)
				stop=true;
			start++;
		}
		status[start%size]=false;
		available++;
		return true;
	}

}
