package jadex.extension.rs.publish.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Method mapper annotation. Can be used to express
 *  to what exact Jadex service method a call should be routed to.
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
	 *  The method parameters.
	 */
	public Class<?>[] parameters() default {};
}