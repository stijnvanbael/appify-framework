package be.appify.framework.domain;

import java.util.*;

import javax.validation.constraints.Size;

public class SizeValidator implements Validator<Size> {

	private final Size annotation;

	public SizeValidator(Size annotation) {
		this.annotation = annotation;
	}

	public SizeValidator() {
		this(null);
	}

	@Override
	public void validate(Class<?> entityType, String property, Object value) {
		if (value == null) {
			return;
		}
		int size = 0;
		if (value instanceof String) {
			size = ((String) value).length();
		} else if (value instanceof Map) {
			size = ((Map<?, ?>) value).size();
		} else if (value instanceof Collection) {
			size = ((Collection<?>) value).size();
		} else if (value instanceof Object[]) {
			size = ((Object[]) value).length;
		} else {
			throw new IllegalArgumentException("Unable to validate the size of an object of " + value.getClass() + " for " + entityType + ".");
		}
		if (size < annotation.min()) {
			throw new IllegalArgumentException("Expected the size of " + entityType.getName() + "." + property + " to be at least " + annotation.min()
					+ ", actual: " + size);
		}
		if (size > annotation.max()) {
			throw new IllegalArgumentException("Expected the size of " + entityType.getName() + "." + property + " to be at most " + annotation.max()
					+ ", actual: " + size);
		}
	}

	@Override
	public Validator<Size> newInstance(Size annotation) {
		return new SizeValidator(annotation);
	}

}
