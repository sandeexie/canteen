package com.github.canteen.log;

public class LoggingFactory {

	private static LoggingFactory factory = new LoggingFactory();

	public static LoggingFactory getInstance() {
		return factory;
	}

	private static Logging logging=new Logging();

	public static Logging create(){
		logging.getLogger();
		return logging;
	}

	/**
	 * 指定类名称创建日志
	 * @param clazz  指定类
	 * @return
	 */
	public static Logging create(Class clazz){
		logging.getLogger(clazz);
		return logging;
	}

	private LoggingFactory() {

	}
}
