package jadex.base.service.remote.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify a timeout period after which the remote invocation
 *  is aborted when no result is received.
 *  
 *  Applicable to all methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout
{
	/**
	 *  The timeout period after which the remote invocation
	 *  is aborted when no result is received.
	 */
	public long value();
}
