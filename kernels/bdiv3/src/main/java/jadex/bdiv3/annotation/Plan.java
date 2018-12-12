package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Plan
{
	/**
	 *  The trigger.
	 */
	public Trigger trigger() default @Trigger();

	/**
	 *  The waitqueue.
	 */
	public Trigger waitqueue() default @Trigger();
	
	/**
	 *  The plan priority. 
	 */
	public int priority() default 0;
	
	/**
	 *  The body (if external plan class).
	 */
	public Body body() default @Body();

}
