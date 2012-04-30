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
public @interface CheckState
{
	/**
	 *  The expression will be parsed.
	 */
	public String value() default "";
	
	/**
	 *  Flag if used as post condition for intermediate results.
	 */
	public boolean intermediate() default false;

	/**
	 *  Flag how many intermediate results should be preserved.
	 *  On necessary if a subscription future is used.
	 */
	public int keep() default 0;

}
