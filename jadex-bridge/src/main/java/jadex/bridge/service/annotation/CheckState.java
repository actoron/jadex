package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Pre or postcondition that a state is valid.
 *  May refer to all arguments/results.
 *  
 *  Reserved variables are $arg for the current argument
 *  and $arg0 - $argn for the arguments.
 *  In case of a post condition the result is available
 *  via $res and intermediate results via $res[0], $res[-1].
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
	 *  Only necessary if a subscription future is used.
	 */
	public int keep() default 0;

}
