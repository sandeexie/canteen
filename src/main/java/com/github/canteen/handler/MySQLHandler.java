package com.github.canteen.handler;

import com.github.canteen.domain.UserInfo;

import java.sql.Connection;

public class MySQLHandler extends JDBCHandler {

	public boolean checkAccess(UserInfo userInfo) {
		return false;
	}


	public Connection getConnection() throws Exception {
		return null;
	}

	public boolean preInspect() {
		return false;
	}

	public Object execute(String sql) {
		return null;
	}

	public Object execute(String sql, long deadline) {
		return null;
	}

	public boolean releaseConnection() {
		return false;
	}

}
