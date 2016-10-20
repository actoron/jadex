package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.rules.eca.EventType;

/**
 *  Raw event allows to specify exactly on what condition to wait.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RawEvent
{
//	/**
//	 *  The event type parts.
//	 */
//	public String[] value();

	/**
	 *  First is the event type such as ChangeEvent.GOALOPTION.
	 */
	public String value();
	
	/**
	 *  Second is the element type such as GoalX
	 */
	public String second() default EventType.MATCHALL;
	
	/**
	 *  Second is the element type such as GoalX as class.
	 */
	public Class<?> secondc() default Object.class;
}
