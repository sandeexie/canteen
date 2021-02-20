package com.github.canteen.utils;

import com.github.canteen.internal.ConfigReader;
import com.github.canteen.rpc.RPCAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterUtil {

	private static ConfigReader reader=ConfigReader.getReader();

	private static Logger logger= LoggerFactory.getLogger(ParameterUtil.class);

	public static <T> T getConfiguration(String key, T defaultValue){
		try{
			T value= (T)reader.get(key, (String) defaultValue);
			return value;
		}catch (Exception e){
			logger.warn("Convert parameter on failure. please check your input parameter.");
			return null;
		}
	}

	public static RPCAddress toRpcAddress(String str){
		String protocol=str.split("://")[0];
		String host=str.split("://")[1].split(":")[0];
		int port=Integer.parseInt(str.split("://")[1].split(":")[1]);

		if(!protocol.equals("rpc")){
			logger.warn("protocol is not rpc. please check your address format.");
			return null;
		}
		if(port<0 || port>=65536){
			logger.warn("illegal port number. it is not arrange from 0 to 65535");
			return null;
		}
		if(host.split(".").length==4){
			logger.warn("Input an illegal ip address.");
			return null;
		}
		for (String part:host.split(".")){
			if(Integer.parseInt(part)<0 || Integer.parseInt(part)>255){
				logger.warn("Input an illegal ip address.");
				return null;
			}
		}
		return new RPCAddress(host,port,protocol);
	}
}
