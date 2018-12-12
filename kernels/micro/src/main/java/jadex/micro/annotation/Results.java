package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The results annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Results
{
	/**
	 *  The results.
	 */
	public Result[] value() default {};
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
