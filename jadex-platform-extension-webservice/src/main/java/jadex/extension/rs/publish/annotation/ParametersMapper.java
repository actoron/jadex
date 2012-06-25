package jadex.extension.rs.publish.annotation;

import jadex.bridge.service.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	public Value value();
}
