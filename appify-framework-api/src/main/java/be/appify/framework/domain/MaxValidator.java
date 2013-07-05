package be.appify.framework.domain;

import javax.validation.constraints.Max;

public class MaxValidator implements Validator<Max> {

	private final Max annotation;

	public MaxValidator(Max annotation) {
		this.annotation = annotation;
	}

	public MaxValidator() {
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
		if (longValue > annotation.value()) {
			throw new IllegalArgumentException("Expected the value of " + entityType.getName() + "." + property + " to be at most " + annotation.value()
					+ ", actual: " + longValue);
		}
	}

	@Override
	public Validator<Max> newInstance(Max annotation) {
		return new MaxValidator(annotation);
	}

}
