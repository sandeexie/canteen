package com.github.canteen.utils;

import com.github.canteen.internal.ConfigReader;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;

public class ParameterUtil {

	private static ConfigReader reader=ConfigReader.getReader();

	private static Logging logging= LoggingFactory.create();

	public static <T> T getConfiguration(String key, int defaultValue){
		try{
			T value= (T)reader.get(key,defaultValue);
			return value;
		}catch (Exception e){
			logging.logWarning("Convert parameter on failure. please check your input parameter.");
			return null;
		}
	}
}
