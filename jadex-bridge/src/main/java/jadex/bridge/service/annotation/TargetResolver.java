package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  
 * 
 *  Applicable to the class as a whole.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetResolver
{
	/**
	 *  The target resolver class.
	 */
	public Class value();  // Hack! should be ITargetResolver
}
