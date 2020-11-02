package security;

import com.github.editor.security.auth.Operation;
import com.github.editor.security.auth.Role;
import com.github.editor.security.auth.UserAuth;
import com.github.editor.security.auth.UserGroup;
import com.github.editor.utils.collections.Tuple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AuthTest {

	@Test
	public void roleCheck(){
		UserGroup userGroup=new UserGroup("China", Role.ANALYSIS);
		userGroup.setOperations();
		Tuple tuple = userGroup.getOperations();
		tuple.forEach(x->System.out.println(x));
	}

	/**
	 * 通用权限测试
	 * 1. 持有用户组角色
	 * 2. 持有特定的成员权限
	 */
	@Test
	public void commonAuthTest(){
		// 设置组权限
		UserGroup userGroup = new UserGroup("China", Role.ANALYSIS);
		userGroup.setOperations();
		List<Operation> acls=new ArrayList<>();
		acls.add(Operation.REGISTE);
		acls.add(Operation.RESTART);
		// 设置ACL权限
		UserAuth userAuth=new UserAuth("sandee",userGroup, acls);
		userAuth.caculate();
		userAuth.finalAccess.forEach(x->System.out.println(x));
	}
}
