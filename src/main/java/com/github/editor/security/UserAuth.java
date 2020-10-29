package com.github.editor.security;

import com.github.editor.log.Logging;
import com.github.editor.utils.collections.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * 用户权限
 */
public class UserAuth extends Logging {

	public static final Logger logger= LoggerFactory.getLogger(UserAuth.class);

	private String username;

	// 用户所属的用户组
	private UserGroup group;

	// 用户访问控制列表
	private List<Operation> acls;

	// 用户最终权限表
	private Set<Operation> finalAccess;

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

	private void caculate(){

		for (Operation operation:acls) {
			finalAccess.add(operation);
		}

		if(null==this.group){
			logger.warn("check your user group settings and ensure your authority.");
		}else {
			Tuple tuple=this.group.getOperations();
			tuple.forEach(x->finalAccess.add((Operation) x));
		}

	}
}
