package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Publish information for a goal.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish
{
	/**
	 *  The service type as which the goal should be published.
	 */
	public Class<?> type();
	
	/**
	 *  The service method the goal should be mapped to.
	 */
	public String method() default "";
}
