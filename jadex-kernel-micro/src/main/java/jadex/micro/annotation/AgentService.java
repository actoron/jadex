package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The service annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentService
{
	/**
	 *  The argument name.
	 */
	public String name() default "";
	
	/**
	 *  Fail at startup if no service is found?
	 */
	public boolean required() default true;
}