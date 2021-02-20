package com.github.canteen.network.message;

import com.github.canteen.rpc.RPCAddress;

import java.util.function.Consumer;

/**
 * 发件箱消息
 * @see Message
 */
public abstract class OutboxMessage extends Message {

	public OutboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure, Consumer onSuccess, boolean isSingleWay) {
		super(source, destination, body, onFailure, onSuccess, isSingleWay);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure) {
		super(source, destination, body, onFailure);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination, byte[] body, Consumer onFailure, boolean isSingleWay) {
		super(source, destination, body, onFailure, isSingleWay);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination, byte[] body) {
		super(source, destination, body);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination, byte[] body, boolean isSingleWay) {
		super(source, destination, body, isSingleWay);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination) {
		super(source, destination);
	}

	public OutboxMessage(RPCAddress source, RPCAddress destination, boolean isSingleWay) {
		super(source, destination, isSingleWay);
	}

	public OutboxMessage(RPCAddress destination) {
		super(destination);
	}

	public OutboxMessage(RPCAddress destination, boolean isSingleWay) {
		super(destination, isSingleWay);
	}
}
