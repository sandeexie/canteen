package com.github.editor.internal;

public class Configuration {

	// 执行限时
	public static final String EXECUTE_DEADLINE="execute.deadline";

	// 登录用户名称
	private static final String LOGIN_NAME="login.name";

	// 重试次数
	public static final String MAX_RETRY_TIMES="max.retry.times";

	// 等待时间
	public static final String WAIT_MILLSECONDS="wait.millseconds";

	// 是否持久化查询信息
	public static final String PERSISTENCE_ENABLED="persistence.enabled";

	// MySQL JDBC链接地址 用于数据持久化
	public static final String JDBC_URL="jdbc.url";

	// MySQL JDBC链接密码 用于数据持久化
	public static final String JDBC_PASSWORD="jdbc.password";

	// 字符集
	public static final String CHARSET_FORMAT="encode.format";

	// 是否使用SSL
	public static final String SSL_ENABLED="ssl.enabled";

	// 是否使用连接池
	public static final String CONNECT_POOL_ENABLED="connect.pool.enabled";

	// 执行端端口
	public static final String EXECUTE_PORT="execute.port";

	// RPC最大连接次数
	public static final String MAX_RPC_CONNECTION_TIMES="max.rpc.connection.times";

	/*================================================================*/

	// RPC默认最大连接次数
	public static final int DEFAULT_MAX_RPC_CONNECTION_TIMES=50;

	// 默认执行端口号
	public static final String DEFAULT_EXECUTE_PORT="5488";

	// 默认开启连接池
	public static final boolean DEFALUT_CONNECT_POOL_ENABLED=true;

	// 默认持久化存储开启
	public static final boolean DEFALUT_PERSISTENCE_ENABLED=true;

	// 默认使用SSL
	public static final boolean DEFAULT_SSL_ENABLED=true;

	// 默认字符集
	public static final String DEFAULT_CHARSET_FORMAT="UTF-8";

	// 默认登录用户(匿名用户)
	public static final String DEFAULT_LOGIN_NAME="anonymous";

	// 默认执行限时
	public static final long DEFAULT_EXECUTE_DEADLINE=Long.MAX_VALUE-8;

	// 默认重试次数
	public static final int DEFAULT_MAX_RETRY_TIMES=3;

	// 默认等待时间
	public static final int DEFAULT_WAIT_MILLSECONDS=15000;
}
