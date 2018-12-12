package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Requires services annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredServices
{
	/**
	 *  The required services.
	 */
	public RequiredService[] value();
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
