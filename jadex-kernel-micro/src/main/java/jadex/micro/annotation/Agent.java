package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  Marker for agent class and variable.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Agent
{
	/**
	 *  If the agent body has a void return value
	 *  or no body at all this flag can be used to 
	 *  determine if the agent should be kept alive.
	 */
	public boolean keepalive() default true; 
}
