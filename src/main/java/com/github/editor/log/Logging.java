package com.github.editor.log;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * @date 2020-10-24
 * @author xyf
 *
 * <em>
 *     日志门面,框架中用于封装{@link org.slf4j.Logger}和
 *     {@link org.apache.logging.log4j.Logger}的工具
 * </em>
 */
public class Logging {

	private Logger logger=null;

	/**
	 * @return 当前类的类名称
	 */
	private String getLoggerName(){
		return this.getClass().getName();
	}

	private Logger getLogger(){
		if(logger==null){
			initLogIfNeccessary(false);
			logger=LoggerFactory.getLogger(getLoggerName());
		}
		return logger;
	}

	///////////////////////////////////////////////////////////////////////////
	// 信息打印
	///////////////////////////////////////////////////////////////////////////
	private void logInfo(String msg){
		if(logger.isInfoEnabled()) logger.info(msg);
	}

	private void logDebug(String msg){
		if(logger.isDebugEnabled()) logger.debug(msg);
	}

	private void logTrace(String msg){
		if(logger.isTraceEnabled()) logger.trace(msg);
	}

	private void logError(String msg){
		if(logger.isErrorEnabled()) logger.error(msg);
	}

	private void logWarning(String msg){
		if(logger.isWarnEnabled()) logger.warn(msg);
	}

	///////////////////////////////////////////////////////////////////////////
	// 信息打印,可接受异常
	///////////////////////////////////////////////////////////////////////////
	private void logInfo(String msg,Throwable throwable){
		if(logger.isInfoEnabled()) logger.info(msg,throwable);
	}

	private void logDebug(String msg,Throwable throwable){
		if(logger.isDebugEnabled()) logger.debug(msg,throwable);
	}

	private void logTrace(String msg,Throwable throwable){
		if(logger.isTraceEnabled()) logger.trace(msg,throwable);
	}

	private void logError(String msg,Throwable throwable){
		if(logger.isErrorEnabled()) logger.error(msg,throwable);
	}

	private void logWarning(String msg,Throwable throwable){
		if(logger.isWarnEnabled()) logger.warn(msg,throwable);
	}

	///////////////////////////////////////////////////////////////////////////
	// 初始化工具
	///////////////////////////////////////////////////////////////////////////
	private void initLogIfNeccessary(Boolean isInterpreter){
		initLogIfNeccessary(isInterpreter,false);
	}

	private void initLogIfNeccessary(Boolean isInterpreter,Boolean silent){

	}

	private void initLogForcefully(Boolean isInterpreter, Boolean silent){

	}

	private void initializeLogging(Boolean isInterpreter,Boolean silent){

	}

	static class InnerLogging {

		private volatile boolean initilized=false;

		private volatile Level defaultRootLevelName=null;

		private volatile boolean defaultLog4j=false;

		private Object lock=new Object();

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
			String binderClass=StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
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
				this.initilized=false;
			}
		}
	}
}
