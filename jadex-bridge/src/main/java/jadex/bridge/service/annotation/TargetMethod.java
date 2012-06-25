package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Target method annotation. Can be used to express
 *  to what exact service method a call should be routed to.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetMethod
{	
	/**
	 *  The method name.
	 */
	public String value();
	
	/**
	 *  The method parameters.
	 */
	public Class<?>[] parameters() default {};
}