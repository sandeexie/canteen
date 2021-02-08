package com.github.canteen.exception;

/**
 * 权限异常
 */
public class IllegalKerberosClientException extends RuntimeException {

	private String msg;

	public IllegalKerberosClientException(String s) {
		this.msg=s;
	}
}
