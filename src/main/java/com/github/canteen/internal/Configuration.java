package com.github.canteen.internal;

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

	// 是否使用连接池
	public static final String ENABLE_CONNECTION_POOL="connection.pool.enabled";

	// 连接池大小参数
	public static final String CONNETION_POOL_SIZE="connection.pool.size";

	public static final String ENABLE_DDOS_DEFENSE="enable.ddos.defense";

	public static final String ENABLE_XSS_DEFENSE="enable.xss.defense";

	public static final String RPC_MAX_RETRY_TIMES="rpc.max.retry.times";

	public static final String RPC_WAITING_MICROSECONDS="rpc.waiting.microseconds";

	public static final String RPC_REICEIVE_UPPER_MICROSECONDS="rpc.receive.upper.microseconds";

	public static final String RPC_ASK_TIMEOUT="rpc.ask.timeout";

	public static final String RPC_RECOVER_TRY_TIMES="rpc.recover.try.times";

	public static final String ENCODE_ALGORITHM="encode.algorithm";

	public static final String DECODE_ALGORITHM="decode.algorithm";

	public static final String KERBOEROS_CLIENT_NAME="kerboeros.client.name";

	public static final String KERBOEROS_CLIENT_PASSWORD="kerboeros.client.password";

	public static final String MESSAGELOOP_THREAD_POOL_SIZE="messageloop.thread.pool.size";

	public static final String MESSAGELOOP_THREAD_POOL_KEEPALIVE_TIME="messageloop.thread.pool.keepalive.time";

	public static final String MESSAGELOOP_THREAD_POOL_CORE_SIZE="messageloop.thread.pool.core.size";
	/*================================================================*/

	// RPC默认最大连接次数
	public static int DEFAULT_MAX_RPC_CONNECTION_TIMES=50;

	// 默认执行端口号
	public static String DEFAULT_EXECUTE_PORT="5488";

	// 默认开启连接池
	public static boolean DEFALUT_CONNECT_POOL_ENABLED=true;

	// 默认持久化存储开启
	public static boolean DEFALUT_PERSISTENCE_ENABLED=true;

	// 默认使用SSL
	public static boolean DEFAULT_SSL_ENABLED=true;

	// 默认字符集
	public static String DEFAULT_CHARSET_FORMAT="UTF-8";

	// 默认登录用户(匿名用户)
	public static String DEFAULT_LOGIN_NAME="anonymous";

	// 默认执行限时
	public static long DEFAULT_EXECUTE_DEADLINE=Long.MAX_VALUE-8;

	// 默认重试次数
	public static int DEFAULT_MAX_RETRY_TIMES=6;

	// 默认等待时间
	public static int DEFAULT_WAIT_MILLSECONDS=15000;

	// 默认启动连接池
	public static boolean DEFAULT_CONNECTION_POOL_ENABLED=true;

	// 默认连接池大小
	public static int DEAFAULT_CONNETION_POOL_SIZE=100;

	// 默认DDos防御开启
	public static boolean DEFAULT_DDOS_DEFENSE=true;

	// 默认开启XSS防御
	public static boolean DEFAULT_XSS_DEFENSE=true;

	// 默认RPC连接重试次数
	public static int DEFAULT_RPC_MAX_RETRY_TIMES=3;

	// 默认RPC等待时间
	public static long DEFAULT_RPC_WAITING_MICROSECONDS=1000;

	// 默认RPC接受时间上限
	public static long DEFAULT_RPC_REICEIVE_UPPER_MICROSECONDS=5000;

	// 默认RPC请求时间上限
	public static long DEFAULT_RPC_ASK_TIMEOUT=5000;

	// 默认RPC状态恢复尝试次数
	public static int DEFAULT_RPC_RECOVER_TRY_TIMES=3;

	// 默认加密算法
	public static String DEFAULT_ENCODE_ALGORITHM="AES";

	// 默认解密算法
	public static String DEFAULT_DECODE_ALGORITHM="AES";

	public static int DEFAULT_MESSAGE_THREAD_POOL_SIZE=10;

	public static int DEFAULT_MESSAGELOOP_THREAD_POOL_KEEPALIVE_TIME=1000;

	public static int DEFAULT_MESSAGELOOP_THREAD_POOL_CORE_SIZE=8;
}
