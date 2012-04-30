package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface CheckNotNull
{
	/**
	 *  Flag if used as post condition for intermediate results.
	 */
	public boolean intermediate() default false;
}
