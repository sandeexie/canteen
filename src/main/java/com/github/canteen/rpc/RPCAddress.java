package com.github.canteen.rpc;

public class RPCAddress {

	private String host;

	private int port;

	private String protocol;

	public RPCAddress(){
		this.host="0.0.0.0";
		this.port=0;
		this.protocol="http";
	}

	public RPCAddress(int port){
		this.host="127.0.0.1";
		this.port=port;
		this.protocol="http";
	}

	public RPCAddress(String host,int port){
		this.host=host;
		this.port=port;
		this.protocol="http";
	}

	public RPCAddress(String host,int port,String protocol){
		this.host=host;
		this.port=port;
		this.protocol=protocol;
	}


	@Override
	public String toString() {
		return this.protocol+"://"+this.host+":"+this.port;
	}
}
