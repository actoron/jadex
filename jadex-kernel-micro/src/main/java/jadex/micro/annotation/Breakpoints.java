package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The breakpoints annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Breakpoints
{
	/**
	 *  The breakpoint names.
	 */
	public String[] value();
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
