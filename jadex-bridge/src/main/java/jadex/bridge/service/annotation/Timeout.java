package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify a timeout period after which the remote invocation
 *  is aborted when no result is received.
 *  
 *  Applicable to all methods or an interface as a whole.
 *  Interface specific settings apply to all methods without
 *  explicit timeout specifications.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout
{
	/** Constant for no timeout. */
	public static final long NONE = -1;
	
	/** Constant for unset. */
	public static final long UNSET = -2;

	/** Constant for local default timeout. */
	public static final long DEFAULT_LOCAL = 10000;

	/** Constant for remote default timeout. */
	public static final long DEFAULT_REMOTE = 30000;
	
	/**
	 *  The timeout period after which the remote invocation
	 *  is aborted when no result is received.
	 */
	public long value() default UNSET;
	
	/**
	 *  The timeout period after which the remote invocation
	 *  is aborted when no result is received.
	 */
	public long local() default UNSET;
	
	/**
	 *  The timeout period after which the remote invocation
	 *  is aborted when no result is received.
	 */
	public long remote() default UNSET;
}
