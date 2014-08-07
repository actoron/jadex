package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Plan trigger elements.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger
{
	/**
	 *  Goal types to react on.
	 */
	public Class<?>[] goals() default {};
	
	/**
	 *  Goal type finished events to react on.
	 */
	public Class<?>[] goalfinisheds() default {};
	
	/**
	 *  The fact added belief names.
	 */
	public String[] factaddeds() default {};
	
	/**
	 *  The fact removed belief names.
	 */
	public String[] factremoveds() default {};
	
	/**
	 *  The fact changed belief names.
	 */
	public String[] factchangeds() default {};
	
	/**
	 *  Activate from service invocation.
	 */
	public ServiceTrigger service() default @ServiceTrigger();
}
