package com.github.editor.handler;

import com.github.editor.domain.UserInfo;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class FlinkSQLHandler extends JDBCHandler {

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
