package com.github.editor.domain;

import java.util.ArrayList;
import java.util.List;

// 用户权限信息
public class AuthInfo {

	// 功能名称
	private String funName;

	// 该项功能的拥有者
	private List<String> users;

	public AuthInfo(String funName){
		this.funName=funName;
		users=new ArrayList<String>();
		users.add("root");
	}

	/**
	 * 添加用户到当前功能的使用权限中
	 * @param host  当前功能持有的用户
	 * @param userInfo  当前未持有此权限的用户
	 */
	public void append(UserInfo host,UserInfo userInfo){
		assert users.contains(host.getName());
		assert !users.contains(userInfo.getName());
		users.add(userInfo.getName());
	}

	public List<String> getUsers() { return users; }

	public String getFunName() { return funName; }

	public void setFunName(String funName) { this.funName = funName; }

	public void setUsers(List<String> users) { this.users = users; }
}
