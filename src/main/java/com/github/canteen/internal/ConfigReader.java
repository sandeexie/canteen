package com.github.canteen.internal;

public class ConfigReader {
	private static ConfigReader reader = new ConfigReader();

	public static ConfigReader getReader() {
		return reader;
	}

	private ConfigReader() { }

	public String get(String key,String defaultValue){
		String value=System.getProperty(key);
		return null==value?defaultValue:value;
	}
}
