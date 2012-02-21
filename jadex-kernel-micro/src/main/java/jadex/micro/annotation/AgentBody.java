package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for agent body method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentBody
{
	/**
	 *  If the agent body has a void return value
	 *  this flag can be used to determine if the
	 *  agent should be kept alive. 
	 */
	public boolean keepalive() default true; 
}

