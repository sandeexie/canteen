package com.github.editor.log;

import org.apache.logging.log4j.Level;
import org.slf4j.impl.StaticLoggerBinder;

public class InnerLogging {

	public volatile boolean initilized=false;

	public volatile Level defaultRootLevelName=null;

	public volatile boolean defaultLog4j=false;

	// TODO: 2020/10/27 控制shell命令日志等级的参数
	public volatile Level shellThresholdLevel=null;

	public Object lock=new Object();

	private static InnerLogging innerLogging = new InnerLogging();

	public static InnerLogging getInnerLogging() {
		return innerLogging;
	}

	/**
	 * 解决用户移除log4j-jul依赖时无法定位到jul的问题
	 */
	private InnerLogging() {
		try {
			Class bridgeClass = Class.forName("org.slf4j.bridge.SLF4JBridgeHandler");
			bridgeClass.getMethod("removeHandlersForRootLogger").invoke(null);
			Boolean installed=(Boolean) bridgeClass.getMethod("isInstalled").invoke(null);
			if(!installed){
				bridgeClass.getMethod("install").invoke(null);
			}
		} catch (Exception e) {
			// no-op
		}
	}

	/**
	 * <em>
	 *     对于Log4jLoggerFactory来说
	 *     {@link org.slf4j.impl} 是Log4j 1.2的版本
	 *     而{@link org.apache.logging.slf4j} 是Log4j 2.0的版本
	 *     所以这里需要一个判断
	 * </em>
	 * @return 判断是否是Log4j 1.2版本
	 */
	private boolean isLog4j12(){
		String binderClass= StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
		return "org.slf4j.impl.Log4jLoggerFactory".equals(binderClass);
	}

	/**
	 * 重置日志
	 */
	private void uninitilize(){
		synchronized (lock){
			if (isLog4j12()){
				if(defaultLog4j){
					defaultLog4j=false;
				}else {
					defaultRootLevelName=null;
				}
			}
			initilized=false;
			shellThresholdLevel=null;
		}
	}
}
