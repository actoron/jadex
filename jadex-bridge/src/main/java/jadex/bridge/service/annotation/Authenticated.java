package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Policy to use authenticate caller.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated
{
	/** Authenticated setting. */
	public static String AUTHENTICATED = "authenticated";
	
	/** The valid platform names. */
	public String[] value() default {};
}
