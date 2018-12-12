package jadex.extension.rs.invoke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Container annotation for more than one parameter
 *  mapper that should be annotated at the method itself.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterMappers
{
	/**
	 *  The parameter mappers.
	 */
	public ParameterMapper[] value() default {};
}
