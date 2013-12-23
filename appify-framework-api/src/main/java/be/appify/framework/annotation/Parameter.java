package be.appify.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the framework should inject a request parameter value for this argument. The name of the parameter to set
 * is determined as follows:
 * <ul>
 *     <li>If the property {@link #name()} or {@link #value()} is set, that is the property name.</li>
 *     <li>If the argument is the single argument of a setter, the property name of the setter is chosen.</li>
 *     <li>If the argument is the single argument of another method, the name of the method is chosen.</li>
 *     <li>In any other case, {@link #name()} or {@link #value()} is required, and an exception will be thrown if it is not set.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {
    // Special {@code null} indicator, since simply {@ode null} is not allowed.
    String NULL = "*__null";

    /**
     * The default value to set when this parameter is not provided. Optional, default null.
     */
    String defaultValue() default NULL;

    /**
     * The name of the request parameter containing the parameter value. Optional for single argument methods.
     */
    String name() default NULL;

    /**
     * The name of the request parameter containing the parameter value. Optional for single argument methods.
     */
    String value() default NULL;

    /**
     * Whether or not this parameter may be {@code null}. When set to {@code true} and the request parameter is not
     * provided, an exception will be thrown. Alternatively, {@link javax.validation.constraints.NotNull} can be used.
     * Optional, default {@code false}
     */
    boolean notNull() default false;
}
