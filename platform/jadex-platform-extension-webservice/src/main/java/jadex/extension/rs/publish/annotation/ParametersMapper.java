package jadex.extension.rs.publish.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.annotation.Value;

/**
 *  Parameter mapper to map the parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersMapper
{
	/**
	 *  The method name.
	 */
	public Value value() default @Value;
	
	/**
	 *  Flag if automapping should be provided.
	 */
	public boolean automapping() default false;
}
