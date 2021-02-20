package com.github.canteen.network.message;

import com.github.canteen.rpc.RPCAddress;

import java.util.function.Consumer;

/**
 * 收件箱消息
 * @see Message
 */
public class InboxMessage extends Message{

	public InboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure, Consumer onSuccess, boolean isSingleWay) {
		super(source, destination, body, onFailure, onSuccess, isSingleWay);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure) {
		super(source, destination, body, onFailure);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure, boolean isSingleWay) {
		super(source, destination, body, onFailure, isSingleWay);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination, byte[] body) {
		super(source, destination, body);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination, byte[] body, boolean isSingleWay) {
		super(source, destination, body, isSingleWay);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination) {
		super(source, destination);
	}

	public InboxMessage(RPCAddress source, RPCAddress destination, boolean isSingleWay) {
		super(source, destination, isSingleWay);
	}

	public InboxMessage(RPCAddress destination) {
		super(destination);
	}

	public InboxMessage(RPCAddress destination, boolean isSingleWay) {
		super(destination, isSingleWay);
	}
}
