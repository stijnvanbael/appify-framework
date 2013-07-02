package be.appify.framework.persistence.appengine;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.Sets;

public class KeyBuilder {
	public static Key createKey(Object object) {
		Field idField = null;
		String keyValue = null;
		Class<? extends Object> type = object.getClass();
		for (Field field : fieldsOf(type)) {
			if ((field.getModifiers() & Modifier.STATIC) == 0) {
				if (field.isAnnotationPresent(Id.class)) {
					if (idField != null) {
						throw new IllegalArgumentException("Only one @Id field is allowed");
					}
					idField = field;
				}
			}
		}
		if (idField == null) {
			throw new IllegalArgumentException("No @Id field found");
		}
		try {
			PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(object, idField.getName());
			Method readMethod = property.getReadMethod();
			Object value = readMethod.invoke(object);
			keyValue = value.toString();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to get property " + idField.getName() + " on " + type
					+ ". Does a getter exist for the property and is it accessible?", e);
		}
		String kind = getKind(type);
		String ancestorFieldName = AppEngineUtil.getAncestorFieldName(type);
		Key ancestorKey = null;
		if (ancestorFieldName != null) {
			try {
				Object ancestor = PropertyUtils.getProperty(object, ancestorFieldName);
				if (ancestor != null) {
					ancestorKey = createKey(ancestor);
				}
			} catch (Exception e) {
				throw new IllegalStateException("Failed to get property " + ancestorFieldName + " on " + type
						+ ". Does a getter exist for the property and is it accessible?", e);
			}
		}
		return ancestorKey != null ? KeyFactory.createKey(ancestorKey, kind, keyValue) : KeyFactory.createKey(kind, keyValue);
	}

	private static Collection<Field> fieldsOf(Class<?> type) {
		Collection<Field> fields = Sets.newHashSet(type.getDeclaredFields());
		if (type.getSuperclass() != null) {
			fields.addAll(fieldsOf(type.getSuperclass()));
		}
		return fields;
	}

	public static String getKind(Class<?> type) {
		return type.getSimpleName();
	}
}
