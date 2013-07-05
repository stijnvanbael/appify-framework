package be.appify.framework.domain;

import java.lang.reflect.*;
import java.util.Map;

import com.google.common.collect.Maps;

public class ReflectionBuilder<E, B extends ReflectionBuilder<E, B>> {
	private final Map<String, Object> properties;
	private final Class<? extends E> entityType;

	protected ReflectionBuilder(Class<? extends E> entityType) {
		this.properties = Maps.newHashMap();
		this.entityType = entityType;
	}

	@SuppressWarnings("unchecked")
	protected B set(String property, Object value) {
		properties.put(property, value);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	protected <T> T get(String property) {
		return (T) properties.get(property);
	}

	public E build() {
		validateProperties();
		E entity = instantiateEntity();
		setProperties(entity);
		return entity;
	}

	private void setProperties(E entity) {
		for (String property : properties.keySet()) {
			Object propertyValue = properties.get(property);
			try {
				Field field = getField(entityType, property);
				field.setAccessible(true);
				field.set(entity, propertyValue);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Unable to access " + entityType.getName() + "." + property + ".", e);
			}
		}
	}

	private Field getField(Class<?> type, String property) {
		try {
			return type.getDeclaredField(property);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = type.getSuperclass();
			if (superClass != null) {
				return getField(superClass, property);
			}
			throw new IllegalStateException("No field " + property + " found in " + entityType + ".", e);
		}
	}

	private E instantiateEntity() {
		E entity;
		try {
			Constructor<? extends E> constructor = entityType.getDeclaredConstructor();
			constructor.setAccessible(true);
			entity = constructor.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Unable to instantiate " + entityType + ". The class should not be abstract.", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to access constructor for " + entityType + ".", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unable to instantiate " + entityType + ". The class should have a no-args constructor.", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("The constructor of " + entityType + " threw an exception.", e);
		}
		return entity;
	}

	private void validateProperties() {
		for (String property : properties.keySet()) {
			Method method = findMethod(property);
			Validator<?> validator = Validators.validatorFor(method.getParameterAnnotations()[0]);
			if (validator != null) {
				validator.validate(entityType, property, properties.get(property));
			}
		}
	}

	private Method findMethod(String property) {
		for (Method method : getClass().getMethods()) {
			if (method.getName().equals(property) && method.getParameterTypes().length == 1) {
				return method;
			}
		}
		throw new IllegalStateException("No method " + property + "(...) found in " + this.getClass());
	}
}
