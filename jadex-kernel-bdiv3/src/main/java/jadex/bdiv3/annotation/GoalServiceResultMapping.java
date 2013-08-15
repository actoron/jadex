package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Mapping annotation for goals that helps feeding
 *  back a result of a service invocation.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalServiceResultMapping
{
	/**
	 *  The name of the service this mapping
	 *  is used for. (Only necessary is multiple mappings are available).
	 */
	public String name() default "";
}
