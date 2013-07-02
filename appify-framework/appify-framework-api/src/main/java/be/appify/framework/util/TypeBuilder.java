package be.appify.framework.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeBuilder<T> {
	public Class<T> build() {

		Type superclass = getClass().getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return convert(parameterized.getActualTypeArguments()[0]);
	}

	@SuppressWarnings("unchecked")
	private Class<T> convert(Type type) {
		if (type instanceof Class) {
			return (Class<T>) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			return (Class<T>) rawType;
		} else {
			return null;
		}
	}
}
