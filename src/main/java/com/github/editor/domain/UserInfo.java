package com.github.editor.domain;

/**
 * 登录执行操作的用户名称
 */
public class UserInfo {

	private String name;

	private String password;

	// 登录状态
	private boolean isLogin;

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public boolean isLogin() { return isLogin; }

	public void setLogin(boolean login) { isLogin = login; }

	public void setPassword(String password) { this.password = password; }
}
