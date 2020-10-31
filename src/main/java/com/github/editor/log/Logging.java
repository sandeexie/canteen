package com.github.editor.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static Logger logger;

	public InnerLogging innerLogging=InnerLogging.getInnerLogging();

	public Logger getLogger(){
		initLogIfNeccessary(false,this.getClass());
		logger=LoggerFactory.getLogger(this.getClass());
		return logger;
	}

	public Logger getLogger(Class clazz){
		initLogIfNeccessary(false,clazz);
		logger= LoggerFactory.getLogger(clazz);
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
	public void logInfo(String msg, Throwable throwable){
		if(logger.isInfoEnabled()) logger.info(msg,throwable);
	}

	public void logDebug(String msg, Throwable throwable){
		if(logger.isDebugEnabled()) logger.debug(msg,throwable);
	}

	public void logTrace(String msg, Throwable throwable){
		if(logger.isTraceEnabled()) logger.trace(msg,throwable);
	}

	public void logError(String msg, Throwable throwable){
		if(logger.isErrorEnabled()) logger.error(msg,throwable);
	}

	public void logWarning(String msg, Throwable throwable){
		if(logger.isWarnEnabled()) logger.warn(msg,throwable);
	}

	///////////////////////////////////////////////////////////////////////////
	// 初始化工具
	///////////////////////////////////////////////////////////////////////////
	public void initLogIfNeccessary(Boolean isInterpreter,Class clazz){
		initLogIfNeccessary(isInterpreter,false,clazz);
	}

	/**
	 * 日志初始化工作
	 * @param isInterpreter 是否被其他终端所中断
	 * @param silent 是否开启静默模式
	 */
	public boolean initLogIfNeccessary(Boolean isInterpreter, Boolean silent,Class clazz){
		if(!this.innerLogging.initilized){
			synchronized (this.innerLogging.lock){
				if(!this.innerLogging.initilized){
					initializeLogging(isInterpreter,silent,clazz);
					return true;
				}
			}
		}
		return false;
	}

	public void initLogForcefully(Boolean isInterpreter, Boolean silent,Class clazz){
		initializeLogging(isInterpreter,silent,clazz);
	}


	public void initializeLogging(Boolean isInterpreter, Boolean silent,Class clazz){
		org.apache.logging.log4j.Logger rootLogger = LogManager.getRootLogger();

		// TODO 处理log4j 1.2初始化问题

		if(this.innerLogging.defaultRootLevelName==null){
			this.innerLogging.defaultRootLevelName=rootLogger.getLevel();
		}
		/**
		 * 对于使用shell指令的扩展,当使用shell指令对默认日志等级进行覆盖的时候进行适配
		 * TODO 对shell输入的指令进行适配
		 */
		if(isInterpreter){
			org.apache.logging.log4j.Logger replLogger = LogManager.getLogger(clazz);
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
				this.innerLogging.shellThresholdLevel=replLevel;
			}
			this.innerLogging.initilized=true;
		}
	}

}
