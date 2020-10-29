package com.github.editor.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
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

	public Logger getLogger(){
		if(logger==null){
			initLogIfNeccessary(false);
			logger=LoggerFactory.getLogger(getLoggerName());
		}
		return logger;
	}

	///////////////////////////////////////////////////////////////////////////
	// 信息打印
	///////////////////////////////////////////////////////////////////////////
	public void logInfo(String msg){
		if(logger.isInfoEnabled()) logger.info(msg);
	}

	public void logDebug(String msg){
		if(logger.isDebugEnabled()) logger.debug(msg);
	}

	public void logTrace(String msg){
		if(logger.isTraceEnabled()) logger.trace(msg);
	}

	public void logError(String msg){
		if(logger.isErrorEnabled()) logger.error(msg);
	}

	public void logWarning(String msg){
		if(logger.isWarnEnabled()) logger.warn(msg);
	}

	///////////////////////////////////////////////////////////////////////////
	// 信息打印,可接受异常
	///////////////////////////////////////////////////////////////////////////
	public void logInfo(String msg,Throwable throwable){
		if(logger.isInfoEnabled()) logger.info(msg,throwable);
	}

	public void logDebug(String msg,Throwable throwable){
		if(logger.isDebugEnabled()) logger.debug(msg,throwable);
	}

	public void logTrace(String msg,Throwable throwable){
		if(logger.isTraceEnabled()) logger.trace(msg,throwable);
	}

	public void logError(String msg,Throwable throwable){
		if(logger.isErrorEnabled()) logger.error(msg,throwable);
	}

	public void logWarning(String msg,Throwable throwable){
		if(logger.isWarnEnabled()) logger.warn(msg,throwable);
	}

	///////////////////////////////////////////////////////////////////////////
	// 初始化工具
	///////////////////////////////////////////////////////////////////////////
	public void initLogIfNeccessary(Boolean isInterpreter){
		initLogIfNeccessary(isInterpreter,false);
	}

	/**
	 * 日志初始化工作
	 * @param isInterpreter 是否被其他终端所中断
	 * @param silent 是否开启静默模式
	 */
	public boolean initLogIfNeccessary(Boolean isInterpreter,Boolean silent){
		if(!InnerLogging.initilized){
			synchronized (InnerLogging.lock){
				if(!InnerLogging.initilized){
					initializeLogging(isInterpreter,silent);
					return true;
				}
			}
		}
		return false;
	}

	public void initLogForcefully(Boolean isInterpreter, Boolean silent){
		initializeLogging(isInterpreter,silent);
	}

	public void initializeLogging(Boolean isInterpreter,Boolean silent){
		org.apache.logging.log4j.Logger rootLogger = LogManager.getRootLogger();

		// TODO 处理log4j 1.2初始化问题

		if(InnerLogging.defaultRootLevelName==null){
			InnerLogging.defaultRootLevelName=rootLogger.getLevel();
		}
		/**
		 * 对于使用shell指令的扩展,当使用shell指令对默认日志等级进行覆盖的时候进行适配
		 * TODO 对shell输入的指令进行适配
		 */
		if(isInterpreter){
			org.apache.logging.log4j.Logger replLogger = LogManager.getLogger(getLoggerName());
			Level replLevel=replLogger.getLevel()==null?Level.WARN:replLogger.getLevel();
			if(!replLevel.name().equals(replLogger.getLevel().name())){
				// 解决默认日志等级缺省
				if(!silent){
					System.err.println(
							"There exist a difference between default " +
									"log level and level from shell."
					);
					System.err.println("Using Default Log Level to "+replLevel.name());
				}
				InnerLogging.shellThresholdLevel=replLevel;
			}
			InnerLogging.initilized=true;
		}
	}

	static class InnerLogging {

		private static volatile boolean initilized=false;

		private static volatile Level defaultRootLevelName=null;

		private static volatile boolean defaultLog4j=false;

		// TODO: 2020/10/27 控制shell命令日志等级的参数
		private static volatile Level shellThresholdLevel=null;

		private static Object lock=new Object();

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
				initilized=false;
				shellThresholdLevel=null;
			}
		}
	}
}
