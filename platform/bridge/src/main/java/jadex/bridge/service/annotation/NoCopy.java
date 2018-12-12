package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Parameters that should be copied / not copied.
 *  
 *  Applicable to all paramters.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoCopy
{
	/**
	 *  Set the copy state.
	 */
	public boolean value() default true;
}
