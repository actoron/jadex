package jadex.extension.rs.invoke.annotation;

import jadex.micro.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParamMapper
{
	/**
	 *  The method name.
	 */
	public String value() default "";
	
	/**
	 *  The source parameter numbers.
	 */
	public int[] source() default {};
	
	/**
	 *  The mapper.
	 */
	public Value mapper() default @Value();
}