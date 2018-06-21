package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Service trigger annotation.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceTrigger
{
	/**
	 *  The required service name.
	 */
	public String name() default "";
	
	/**
	 *  The service type.
	 */
	public Class<?> type() default Object.class;
	
	/**
	 *  The method name that identifies the method.
	 */
	public String method() default "";
}
