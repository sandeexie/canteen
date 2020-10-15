package com.github.editor.rpc.message;

import java.util.concurrent.Future;

public class RPCMessage<T> implements Message{

	// RPC发送的消息
	private Future<T> command;

	// 传输所使用的压缩算法
	private CompressedCodec codec;

	private RPCStatus status;

	private Object message;

	private RPCStatus rpcStatus;

	public RPCMessage(){
		this.command=null;
		this.codec=CompressedCodec.NONE;
		this.message=null;
		this.rpcStatus=RPCStatus.INITIALIZED;
	}

	public RPCMessage(Future<T> command){
		this.command=command;
		this.codec=CompressedCodec.NONE;
		this.message=null;
		this.rpcStatus=RPCStatus.INITIALIZED;
	}

	public RPCMessage(Future<T> command,CompressedCodec codec){
		this.command=command;
		this.codec=codec;
		this.message=null;
		this.rpcStatus=RPCStatus.INITIALIZED;
	}

	public RPCMessage(Future<T> command,Object message){
		this.command=command;
		this.message=message;
		this.codec=CompressedCodec.NONE;
		this.rpcStatus=RPCStatus.INITIALIZED;
	}

	public RPCMessage(Future<T> command,CompressedCodec codec,Object message){
		this.command=command;
		this.codec=codec;
		this.message=message;
		this.rpcStatus=RPCStatus.INITIALIZED;
	}
}
