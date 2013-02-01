package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Belief
{
	/**
	 *  The implementation type (obsolete?)
	 */
	public Class<?> implementation() default Object.class;

	/**
	 *  A dynamic belief is automatically updated when other beliefs change.
	 */
	public String[] dynamic() default {};
	
}
