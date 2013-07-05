package be.appify.framework.domain;

import javax.validation.constraints.Min;

public class MinValidator implements Validator<Min> {

	private final Min annotation;

	public MinValidator(Min annotation) {
		this.annotation = annotation;
	}

	public MinValidator() {
		this(null);
	}

	@Override
	public void validate(Class<?> entityType, String property, Object value) {
		if (value == null) {
			return;
		}
		long longValue = 0;
		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
		} else {
			throw new IllegalArgumentException("Unable to validate the value of an object of " + value.getClass() + " for " + entityType
					+ ". Only classes implementing " + Number.class.getName() + " are supported.");
		}
		if (longValue < annotation.value()) {
			throw new IllegalArgumentException("Expected the value of " + entityType.getName() + "." + property + " to be at least " + annotation.value()
					+ ", actual: " + longValue);
		}
	}

	@Override
	public Validator<Min> newInstance(Min annotation) {
		return new MinValidator(annotation);
	}

}
