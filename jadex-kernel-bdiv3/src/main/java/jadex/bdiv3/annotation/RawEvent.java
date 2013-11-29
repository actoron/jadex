package jadex.bdiv3.annotation;

import jadex.rules.eca.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
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
	 * 
	 */
	public String value();
	
	/**
	 * 
	 */
	public String second() default EventType.MATCHALL;
	
	/**
	 * 
	 */
	public Class<?> secondc() default Object.class;
}
