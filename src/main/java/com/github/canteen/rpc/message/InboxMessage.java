package com.github.canteen.rpc.message;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * 收件箱消息
 * @see Message
 */
public class InboxMessage extends Message{

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure, Consumer onSuccess, boolean isSingleWay) {
		super(requestId, source, destination, body, onFailure, onSuccess, isSingleWay);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure) {
		super(requestId, source, destination, body, onFailure);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, Consumer onFailure, boolean isSingleWay) {
		super(requestId, source, destination, body, onFailure, isSingleWay);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body) {
		super(requestId, source, destination, body);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, ByteBuffer body, boolean isSingleWay) {
		super(requestId, source, destination, body, isSingleWay);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination) {
		super(requestId, source, destination);
	}

	public InboxMessage(long requestId, RPCMessage source, RPCMessage destination, boolean isSingleWay) {
		super(requestId, source, destination, isSingleWay);
	}

	public InboxMessage(long requestId, RPCMessage destination) {
		super(requestId, destination);
	}

	public InboxMessage(long requestId, RPCMessage destination, boolean isSingleWay) {
		super(requestId, destination, isSingleWay);
	}
}
