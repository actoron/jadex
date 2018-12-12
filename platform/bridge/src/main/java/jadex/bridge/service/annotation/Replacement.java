package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Define a replacement object to be called
 *  instead of the remote method.
 *  
 *  Applicable to all methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Replacement
{
	/**
	 *  Supply a class name of a class implementing IMethodReplacement.
	 */
	public String value();
}
