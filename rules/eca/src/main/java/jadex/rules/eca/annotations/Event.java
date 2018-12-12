package jadex.rules.eca.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Representation of an event that causes the evaluation of a condition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Event
{
	/**
	 *  The event type.
	 */
	public String type() default "";
	
	/**
	 *  The event content.
	 */
	public String value() default "";
}
