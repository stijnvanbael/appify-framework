package be.appify.framework.view.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the framework should inject a value for this argument from the CDI context. Optionally a name can be
 * provided using {@code @Context(name = "beanName")} or {@code @Context("beanName")}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Context {
    // Special {@code null} indicator, since simply {@ode null} is not allowed.
    String NULL = "*__null";

    /**
     * The name of the bean to inject. When left empty, beans will be injected by type.
     */
    String name() default NULL;

    /**
     * The name of the bean to inject. When left empty, beans will be injected by type.
     */
    String value() default NULL;

    /**
     * Whether or not the argument may be null. When {@link #notNull()} is {@code true} and no bean is found,
     * an exception is thrown. Optional, default {@code true}
     */
    boolean notNull() default true;
}
