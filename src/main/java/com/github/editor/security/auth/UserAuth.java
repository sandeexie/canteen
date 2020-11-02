package com.github.editor.security.auth;

import com.github.editor.utils.collections.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户权限
 */
public class UserAuth {

	public static final Logger logger= LoggerFactory.getLogger(UserAuth.class);

	private String username;

	// 用户所属的用户组
	private UserGroup group;

	// 用户访问控制列表
	private List<Operation> acls;

	// 用户最终权限表
	public Set<Operation> finalAccess=new HashSet<>();

	public UserAuth(String username){
		this.username=username;
		this.group=new UserGroup();
	}

	public UserAuth(String username,UserGroup group){
		this.username=username;
		this.group=group;
	}

	public UserAuth(String username,UserGroup group,List<Operation> acls){
		this.username=username;
		this.group=group;
		this.acls=acls;
	}

	public void caculate(){

		if(null!=acls){
			for (Operation operation:acls) {
				this.finalAccess.add(operation);
			}
		}

		if(null==this.group){
			logger.warn("check your user group settings and ensure your authority.");
		}else {
			Tuple tuple=this.group.getOperations();
			if(null==tuple){
				logger.error("this user group do not have any operation. please check your privilege.");
			}else {
				tuple.forEach(x-> this.finalAccess.add((Operation) x));
			}
		}

	}
}
