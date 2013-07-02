package be.appify.framework.domain;

import java.lang.annotation.Annotation;
import java.util.*;

import com.google.common.collect.Sets;

public class CompositeValidator implements Validator<Annotation> {

	private final HashSet<Validator<?>> validators;

	public CompositeValidator(Set<Validator<?>> validators) {
		this.validators = Sets.newHashSet(validators);
	}

	@Override
	public void validate(Class<?> entityType, String property, Object value) {
		for (Validator<?> validator : validators) {
			validator.validate(entityType, property, value);
		}
	}

	@Override
	public Validator<Annotation> newInstance(Annotation annotation) {
		throw new UnsupportedOperationException();
	}

}
