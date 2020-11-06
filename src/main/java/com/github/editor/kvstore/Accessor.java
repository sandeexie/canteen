package com.github.editor.kvstore;

/**
 * 存取器
 * 用于抽象属性使用和调用方法
 */
public interface Accessor {

	Object get(Object instance) throws Exception;

	Class<?> getType();
}
