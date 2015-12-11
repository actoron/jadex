package jadex.extension.rs.invoke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.annotation.Value;

/**
 *  The parameter mapper can be used to state how a parameter should
 *  be mapped for a rest service invocation.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterMapper
{
	/**
	 *  The parameter name as used in the rest call.
	 */
	public String value() default "";
	
	/**
	 *  The parameter numbers that should be
	 *  passed to the mapper as input. If not
	 *  given only the annotated parameter will
	 *  be given.
	 *  (from 0: the first till n: the last).
	 */
	public int[] source() default {};
	
	/**
	 *  The class or creation expression of the mapper.
	 */
	public Value mapper() default @Value();
}