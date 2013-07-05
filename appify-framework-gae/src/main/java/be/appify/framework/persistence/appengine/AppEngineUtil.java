package be.appify.framework.persistence.appengine;

import java.lang.reflect.Field;
import java.util.Set;


import be.appify.framework.persistence.annotation.Ancestor;

import com.google.common.collect.Sets;

final class AppEngineUtil {
	private AppEngineUtil() {
	}

	static String getAncestorFieldName(Class<? extends Object> type) {
		String name = null;
		for (Field field : allFields(type)) {
			if (field.isAnnotationPresent(Ancestor.class)) {
				if (name != null) {
					throw new IllegalStateException("Only one field can be marked @Ancestor on " + type + ". Found @Ancestor on fields '"
							+ name + "' and '" + field.getName() + "'.");
				}
				name = field.getName();
			}
		}
		return name;
	}

	static Set<Field> allFields(Class<? extends Object> type) {
		Set<Field> fields = Sets.newHashSet(type.getDeclaredFields());
		if (type.getSuperclass() != null) {
			fields.addAll(allFields(type.getSuperclass()));
		}
		return fields;
	}

	static Field getField(Class<?> type, String name) {
		Field field = null;
		try {
			field = type.getDeclaredField(name);
		} catch (Exception e) {
			if (type.getSuperclass() != null) {
				field = getField(type.getSuperclass(), name);
			}
		}
		return field;
	}
}
