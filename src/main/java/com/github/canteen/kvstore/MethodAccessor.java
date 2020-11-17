package com.github.canteen.kvstore;

import java.lang.reflect.Method;

public class MethodAccessor implements Accessor {

	private final Method method;

	MethodAccessor(Method method){
		this.method=method;
	}

	@Override
	public Object get(Object instance) throws Exception {
		return method.invoke(instance);
	}

	@Override
	public Class<?> getType() {
		return method.getReturnType();
	}
}
