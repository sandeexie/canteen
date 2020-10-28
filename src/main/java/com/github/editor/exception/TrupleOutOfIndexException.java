package com.github.editor.exception;

public class TrupleOutOfIndexException extends RuntimeException {

	private String msg;

	public TrupleOutOfIndexException(String msg) {
		this.msg=msg;
	}
}
