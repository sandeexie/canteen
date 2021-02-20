package com.github.canteen.network.message;

/**
 * 设置PRC消息的状态用于进行生命周期控制,并进行消息状态的追踪
 */
public enum RPCStatus {
	INITIALIZED,
	STARTED,
	CONNECTED,
	ERROR,
	DISCONNECTED,
	STOPPED,
	DESTROYED
}
