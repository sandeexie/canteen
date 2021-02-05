package com.github.canteen.rpc.message;

import com.github.canteen.rpc.RPCAddress;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * <em>报文设计</em>
 * <p>
 *     请求编号 requestId
 *     缺省源RPC地址 localRPCAddress
 *     源地址 source
 *     目标地址 destination
 *     消息主体 body
 *     差错处理 onFailure
 *     响应回调 onSuccess
 * </p>
 */
public abstract class Message implements Serializable {

	private long requestId;

	private RPCAddress localRPCAddress;

	private RPCAddress source;

	private RPCAddress destination;

	private ByteBuffer body;

	private Consumer onFailure;

	private Consumer onSuccess;

	private boolean isSingleWay;

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               ByteBuffer body,
	               Consumer onFailure,
	               Consumer onSuccess,
	               boolean isSingleWay) {
		this.requestId=requestId;
		this.source=source;
		this.destination=destination;
		this.body=body;
		this.onFailure=onFailure;
		this.onSuccess=onSuccess;
		this.isSingleWay=isSingleWay;
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               ByteBuffer body,
	               Consumer onFailure) {
		this(requestId,source,destination,body,onFailure,null,false);
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               ByteBuffer body,
	               Consumer onFailure,
	               boolean isSingleWay) {
		this(requestId,source,destination,body,onFailure,null,isSingleWay);
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               ByteBuffer body){
		this(requestId,source,destination,body,null,null,false);
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               ByteBuffer body,
	               boolean isSingleWay){
		this(requestId,source,destination,body,null,null,isSingleWay);
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination){
		this(requestId,source,destination,null,null,null,false);
	}

	public Message(long requestId,
	               RPCAddress source,
	               RPCAddress destination,
	               boolean isSingleWay){
		this(requestId,source,destination,null,null,null,isSingleWay);
	}

	public Message(long requestId,
	               RPCAddress destination){
		this(requestId,null,destination,null,null,null,false);
		this.source=localRPCAddress;
	}

	public Message(long requestId,
	               RPCAddress destination,
	               boolean isSingleWay){
		this(requestId,null,destination,null,null,null,isSingleWay);
		this.source=localRPCAddress;
	}
}
