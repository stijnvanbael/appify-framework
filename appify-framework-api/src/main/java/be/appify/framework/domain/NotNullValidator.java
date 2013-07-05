package be.appify.framework.domain;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;

public class NotNullValidator implements Validator<NotNull> {

	@Override
	public void validate(Class<?> entityType, String property, Object value) {
		Validate.notNull(value, entityType.getName() + "." + property + " cannot be null.");
	}

	@Override
	public Validator<NotNull> newInstance(NotNull annotation) {
		return this;
	}

}
