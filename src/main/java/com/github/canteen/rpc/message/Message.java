package com.github.canteen.rpc.message;

import java.io.Serializable;

/**
 * RPC报文信息
 *
 */
public abstract class Message implements Serializable {

	public String header;

	public String body;
}
