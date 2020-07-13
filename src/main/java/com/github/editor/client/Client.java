package com.github.editor.client;

import com.github.editor.internal.ConfigReader;
import com.github.editor.internal.Configuration;

public class Client {

	private String hostname;

	private Integer port;

	public Client(){
		ConfigReader reader = ConfigReader.getInstance();
		this.hostname="localhost";
		this.port= Integer.parseInt(
				reader.get(
					Configuration.EXECUTE_PORT,
					Configuration.DEFAULT_EXECUTE_PORT
				)
		);
	}

	public Client(String hostname,Integer port){
		this.hostname=hostname;
		this.port=port;
	}
}
