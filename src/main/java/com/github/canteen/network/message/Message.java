package com.github.canteen.network.message;

import com.github.canteen.rpc.RPCAddress;

import java.io.Serializable;
import java.util.UUID;
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
public class Message implements Serializable {

	private long requestId;

	private RPCAddress localRPCAddress;

	private RPCAddress source;

	private RPCAddress destination;

	private byte[] body;

	private Consumer onFailure;

	private Consumer onSuccess;

	private boolean isSingleWay;

	public Message(RPCAddress source,
	               RPCAddress destination,
	               byte[] body,
	               Consumer onFailure,
	               Consumer onSuccess,
	               boolean isSingleWay) {
		this.requestId= UUID.randomUUID().getLeastSignificantBits();
		this.source=source;
		this.destination=destination;
		this.body=body;
		this.onFailure=onFailure;
		this.onSuccess=onSuccess;
		this.isSingleWay=isSingleWay;
	}

	public Message(RPCAddress source,
	               RPCAddress destination,
	               byte[] body,
	               Consumer onFailure) {
		this(source,destination,body,onFailure,null,false);
	}

	public Message(RPCAddress source,
	               RPCAddress destination,
	               byte[] body,
	               Consumer onFailure,
	               boolean isSingleWay) {
		this(source,destination,body,onFailure,null,isSingleWay);
	}

	public Message(RPCAddress source,
	               RPCAddress destination,
	               byte[] body){
		this(source,destination,body,null,null,false);
	}

	public Message(RPCAddress source,
	               RPCAddress destination,
	               byte[] body,
	               boolean isSingleWay){
		this(source,destination,body,null,null,isSingleWay);
	}

	public Message(RPCAddress source,
	               RPCAddress destination){
		this(source,destination,null,null,null,false);
	}

	public Message(RPCAddress source,
	               RPCAddress destination,
	               boolean isSingleWay){
		this(source,destination,null,null,null,isSingleWay);
	}

	public Message(RPCAddress destination){
		this(null,destination,null,null,null,false);
		this.source=localRPCAddress;
	}

	public Message(RPCAddress destination,
	               boolean isSingleWay){
		this(null,destination,null,null,null,isSingleWay);
		this.source=localRPCAddress;
	}

	@Override
	public String toString() {
		return new StringBuffer()
				.append("message Info\n")
				.append("=========================\n")
				.append("source:  "+source.toString()+"\n")
				.append("destination:  "+destination.toString()+"\n")
				.append("is singleway:  "+isSingleWay+"\n")
				.append("text: "+body.toString())
				.toString();
	}
}
