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
	
	/**
	 *  The timeout period after which the remote invocation
	 *  is aborted when no result is received.
	 */
	public long value();
}
