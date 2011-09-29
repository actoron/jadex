package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Component properties as name value pairs.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Properties
{
	/**
	 *  The name value pairs.
	 */
	public NameValue[] value();
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
