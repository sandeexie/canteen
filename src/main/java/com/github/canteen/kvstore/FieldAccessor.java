package com.github.canteen.kvstore;

import java.lang.reflect.Field;

public class FieldAccessor implements Accessor {

	private final Field field;

	public FieldAccessor(Field field) {
		this.field = field;
	}

	@Override
	public Object get(Object instance) throws Exception {
		return field.get(instance);
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}
}
