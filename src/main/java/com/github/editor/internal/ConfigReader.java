package com.github.editor.internal;

public class ConfigReader {
	private static ConfigReader ourInstance = new ConfigReader();

	public static ConfigReader getInstance() {
		return ourInstance;
	}

	private ConfigReader() { }

	public String get(String key,String defaultValue){
		String value=System.getProperty(key);
		return null==value?defaultValue:value;
	}
}
