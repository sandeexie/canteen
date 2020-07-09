package com.github.editor.handler;

import com.github.editor.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class HQLHandler extends JDBCHandler {

	public static final Logger logger= LoggerFactory.getLogger(HQLHandler.class);

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
