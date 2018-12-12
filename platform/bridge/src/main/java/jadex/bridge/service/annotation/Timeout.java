package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.commons.future.Future;

/**
 *  Specify a timeout period after which the remote invocation
 *  is aborted when no result is received.
 *  
 *  Applicable to all methods of a service interface or a service interface as a whole.
 *  Interface specific settings apply to all methods without
 *  explicit timeout specifications.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout
{	
	/** Constant for no timeout. */
	public static final long NONE = Future.NONE;
	
	/** Constant for unset (i.e. use current default timeout). */
	public static final long UNSET = Future.UNSET;

	/** Constant for timeout name in non-functional properties. */
	public static final String TIMEOUT = "timeout";
	
	/**
	 *  The timeout period after which local or remote invocations
	 *  are aborted when no result is received.
	 */
	public long value() default UNSET;
}
