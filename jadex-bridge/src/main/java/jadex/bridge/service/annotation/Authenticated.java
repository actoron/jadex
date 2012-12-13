package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated
{
	public static String AUTHENTICATED = "authenticated";

	/**
	 *  The platform (prefix) names that are allowed.
	 */
	public String[] names() default {};
	
	/**
	 *  The virtual names. Are mapped to real platform names
	 *  via the security service.
	 */
	public String[] virtuals() default {};
}