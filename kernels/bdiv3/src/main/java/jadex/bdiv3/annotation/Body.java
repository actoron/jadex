package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Body
{
	/**
	 *  The body as seperate class.
	 */
	public Class<?> value() default Object.class;

	/**
	 *  The body as component type.
	 */
	public String component() default "";

	/**
	 *  The body as required service.
	 */
	public ServicePlan service() default @ServicePlan(name="");
	
}