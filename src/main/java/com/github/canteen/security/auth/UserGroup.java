package com.github.canteen.security.auth;

import com.github.canteen.utils.collections.*;

/**
 * 用户组
 * 主要用户对用户角色权限的控制
 */
public class UserGroup {

	// 用户组名称
	private String userGroupName;

	// 用户组操作权限
	private Tuple operations;

	private volatile Object lock=new Object();

	// 用户组角色
	private Role role;

	public UserGroup(){
		this.role=Role.NONE;
	}

	public UserGroup(Role role){
		this.role=role;
	}

	public UserGroup(String userGroupName){
		this.userGroupName=userGroupName;
		this.role=Role.NONE;
	}

	public UserGroup(String userGroupName,Role role){
		this.userGroupName=userGroupName;
		this.role=role;
	}

	public Role getRole() {
		return this.role;
	}

	public String getUserGroupName() {
		return this.userGroupName;
	}

	public void setRole(Role role){
		this.role=role;
	}

	public void setUserGroupName(String userGroupName){
		this.userGroupName=userGroupName;
	}

	public Tuple getOperations(){
		return this.operations;
	}

	public void setOperations(){
		synchronized (lock){
			switch (role){
				case ADMIN:
					this.operations=
							new Tuple6(
									Operation.ADD,
									Operation.DELETE,
									Operation.RESTART,
									Operation.QUERY,
									Operation.REGISTE,
									Operation.SHUTDOWN
							);
					break;
				case DEVELOP:
					this.operations=
							new Tuple4(
								Operation.ADD,
								Operation.DELETE,
								Operation.QUERY,
								Operation.RESTART
							);
					break;
				case ANALYSIS:
					this.operations=
							new Tuple3(
									Operation.ADD,
									Operation.DELETE,
									Operation.QUERY
							);
					break;
				case VIEWER:
					this.operations=new Tuple1(Operation.QUERY);
					break;
				case NONE:
					this.operations=new Tuple0();
					break;
			}
		}
	}


}
