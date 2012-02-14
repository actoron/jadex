package jadex.extension.rs.publish.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodMapper
{	
	/**
	 *  The method name.
	 */
	public String value();
	
	/**
	 *  The parameters.
	 */
	public Class<?>[] parameters() default {};
}