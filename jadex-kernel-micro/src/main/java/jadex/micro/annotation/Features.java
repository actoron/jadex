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
	 *  Replace content of the inherited classes.
	 */
	public boolean replace() default false;
	
	/**
	 *  Add the defined features of this classes (and inherited when replace==false)
	 *  to the standard features of the component.
	 */
	public boolean additional() default false;
}