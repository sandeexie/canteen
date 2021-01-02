package com.github.canteen.handler;


import com.github.canteen.domain.UserInfo;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;

import java.sql.Connection;

public abstract class JDBCHandler implements ResolveHandler {

	private static final Logging logger= LoggingFactory.create();

	private String sql;

	private Exception reason;

	private boolean isAuthed;

	private boolean isConnected;

	private boolean isSuccessedExecution;

	private boolean isFreeSuccessed;

	// 是否缓存,用户扩展缓存功能
	private boolean isCached;

	/**
	 * 用户权限检查
	 * @param userInfo  用户信息检查
	 * @return
	 */
	public abstract boolean checkAccess(UserInfo userInfo);

	/**
	 * 权限通过时候的回调
	 * @return
	 */
	public void onAuthed(){
		if(this.isAuthed)
			logger.logInfo("User has passed the access verification");
		else
			logger.logError("User has been denied by system");
	}

	/**
	 获取JDBC连接
	 可以是自己创建的连接，也可以是从线程池中获取的连接
	 */
	public abstract Connection getConnection() throws Exception;

	/**
	 * 处理连接成功的回调函数
	 */
	public void onConnected(){
		if(this.isConnected)
			logger.logInfo("JDBC connection has been acquired");
		else
			logger.logError("Can not get JDBC connection, please check your URL");
	}

	/**
	 * 执行前检查,防止网络攻击
	 * @return
	 */
	public abstract boolean preInspect();

	/**
	 * 默认SQL执行体
	 * @param sql   执行SQL串
	 * @return
	 */
	public abstract Object execute(String sql);

	/**
	 * 带有时间限制的SQL执行功能
	 * @param sql   执行SQL串
	 * @param deadline  时限(ms)
	 * @return
	 */
	public abstract Object execute(String sql,long deadline);

	/**
	 * 执行成功的回调函数
	 */
	public void onExecuted(String sql){
		if (this.isSuccessedExecution)
			logger.logInfo(sql+" has been executed successfully");
		else
			logger.logError(sql+" executed on failure");
	}

	/**
	 * 释放链接
	 * @return
	 */
	public abstract boolean releaseConnection();

	/**
	 * 释放成功时的回调函数
	 */
	public void onReleased(){
		if (this.isFreeSuccessed)
			logger.logInfo("JDBC connection has been released successfully");
		else
			logger.logError("JDBC connection was released on failure");
	}

	/**
	 * 执行失败时候的回调函数
	 * @param reason
	 */
	public void onError(Exception reason){
		logger.logError(reason.getMessage());
		this.reason=reason;
	}

	public Exception getReason() {
		return reason;
	}

	public void setReason(Exception reason) {
		this.reason = reason;
	}

	public String getSql() { return sql; }

	public void setSql(String sql) { this.sql = sql; }

	public boolean isAuthed() {
		return isAuthed;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean isFreeSuccessed() {
		return isFreeSuccessed;
	}

	public boolean isSuccessedExecution() {
		return isSuccessedExecution;
	}

	public boolean isCached() {
		return isCached;
	}

	public void setAuthed(boolean authed) {
		isAuthed = authed;
	}

	public void setCached(boolean cached) {
		isCached = cached;
	}

	public void setConnected(boolean connected) {
		isConnected = connected;
	}

	public void setFreeSuccessed(boolean freeSuccessed) {
		isFreeSuccessed = freeSuccessed;
	}

	public void setSuccessedExecution(boolean successedExecution) {
		isSuccessedExecution = successedExecution;
	}

}
