package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Injects a service into a field or a method of a component.
 *  The referenced service must be declared with a {@link RequiredService} annotation.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
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
	
	/**
	 *  If is lazy the service search will happen on first call.
	 *  This can go wrong if first call is a synchronous message.
	 *  If lazy is false, the agent might block when search takes time.
	 */
	public boolean lazy() default true;
}