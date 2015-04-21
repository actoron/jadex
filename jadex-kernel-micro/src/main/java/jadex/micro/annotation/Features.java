package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The features annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Features
{
	/**
	 *  The features.
	 */
	public Feature[] value() default {};
	
	/**
	 *  Replace content of the existing classes.
	 */
	public boolean replace() default false;
}