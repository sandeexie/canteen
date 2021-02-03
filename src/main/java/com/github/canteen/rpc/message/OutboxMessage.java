package com.github.canteen.rpc.message;

import com.github.canteen.rpc.RPCAddress;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * 发件箱消息
 * @see Message
 */
public abstract class OutboxMessage extends Message {

		private RPCAddress destination;

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure, Consumer onSuccess, boolean isSingleWay) {
		super(requestId, source, destination, body, onFailure, onSuccess, isSingleWay);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure) {
		super(requestId, source, destination, body, onFailure);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure, boolean isSingleWay) {
		super(requestId, source, destination, body, onFailure, isSingleWay);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body) {
		super(requestId, source, destination, body);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, boolean isSingleWay) {
		super(requestId, source, destination, body, isSingleWay);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination) {
		super(requestId, source, destination);
	}

	public OutboxMessage(long requestId, RPCMessage source, RPCMessage destination, boolean isSingleWay) {
		super(requestId, source, destination, isSingleWay);
	}

	public OutboxMessage(long requestId, RPCMessage destination) {
		super(requestId, destination);
	}

	public OutboxMessage(long requestId, RPCMessage destination, boolean isSingleWay) {
		super(requestId, destination, isSingleWay);
	}
}
