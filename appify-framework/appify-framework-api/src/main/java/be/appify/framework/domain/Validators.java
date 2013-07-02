package be.appify.framework.domain;

import java.lang.annotation.Annotation;
import java.util.*;

import javax.validation.constraints.*;

import com.google.common.collect.*;

public class Validators {

	private static final Map<Class<? extends Annotation>, Validator<?>> VALIDATORS = Maps.newHashMap();
	static {
		VALIDATORS.put(NotNull.class, new NotNullValidator());
		VALIDATORS.put(Size.class, new SizeValidator());
		VALIDATORS.put(Min.class, new MinValidator());
		VALIDATORS.put(Max.class, new MaxValidator());
	}

	public static Validator<?> validatorFor(Annotation... annotations) {
		Set<Validator<?>> validators = Sets.newHashSet();
		for (Annotation annotation : annotations) {
			Validator<?> validator = getValidator(annotation);
			if (validator != null) {
				validators.add(validator);
			}
		}
		return new CompositeValidator(validators);
	}

	@SuppressWarnings("unchecked")
	private static <A extends Annotation> Validator<A> getValidator(A annotation) {
		Validator<A> validator = (Validator<A>) VALIDATORS.get(annotation.getClass());
		return validator != null ? validator.newInstance(annotation) : null;
	}

}
