package be.appify.framework.domain;

import java.lang.annotation.Annotation;

public interface Validator<A extends Annotation> {
	void validate(Class<?> entityType, String property, Object value);

	Validator<A> newInstance(A annotation);
}
