package com.github.editor.rpc.message;

/**
 * 设置PRC消息的状态用于进行生命周期控制,并进行消息状态的追踪
 */
public enum RPCStatus {
	INITIALIZED,
	SENT,
	ACCEPTED,
	FINISHED,
	SEND_BACK,
	LOCAL_RECEIVED
}
