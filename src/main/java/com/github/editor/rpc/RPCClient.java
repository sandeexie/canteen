package com.github.editor.rpc;

import com.github.editor.internal.ConfigReader;
import com.github.editor.internal.Configuration;
import com.github.editor.rpc.message.CompressedCodec;
import com.github.editor.rpc.message.RPCMessage;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Future;

public class RPCClient<T> {

	private static final Logger logger= LoggerFactory.getLogger(RPCClient.class);

	private HttpServer server=null;

	private HttpClient client=null;

	private final String prefix="http://";

	private ConfigReader configReader=ConfigReader.getInstance();

	private int rpcConnectionNumber=Integer.parseInt(
			configReader.get(
				Configuration.MAX_RPC_CONNECTION_TIMES,
				String.valueOf(Configuration.DEFAULT_MAX_RPC_CONNECTION_TIMES)
			)
		);

	/**
	 *
	 * @param host 服务端的主机信息
	 * @param port 服务端的端口号
	 * @param task 需要执行的任务
	 * @return
	 */
	public synchronized T remoteExecute(
			String host,
			Integer port,
			Future<T> task){
		String addr=prefix + host + ":" + port;
		try {
			URL url = new URL(addr);
			this.client= HttpClient.New(url);
			RPCMessage<T> message = new RPCMessage<T>(task);
		} catch (MalformedURLException e) {
			logger.error("Can not create url with "+addr);
		} catch (IOException e) {
			logger.error("Can not create http client with "+addr);
		}


		return null;
	}

	public synchronized T remoteExecute(
			String host,
			Integer port,
			Future<T> task,
			CompressedCodec codec){

		return null;
	}
}
