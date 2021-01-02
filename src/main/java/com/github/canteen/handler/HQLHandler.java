package com.github.canteen.handler;

import com.github.canteen.domain.UserInfo;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;

import java.sql.Connection;

public class HQLHandler extends JDBCHandler {

	public static final Logging logger= LoggingFactory.create();

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
