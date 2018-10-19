package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  A plan trigger states for which events or goals a plan should be selected.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger
{
	/**
	 *  Goal types to react on, i.e. choose this plan to handle goals of the given type(s).
	 */
	public Class<?>[] goals() default {};
	
	/**
	 *  Goal type finished events to react on, i.e. the plan will be executed after the goal has been processed (e.g. by a different plan).
	 */
	public Class<?>[] goalfinisheds() default {};
	
	/**
	 *  The fact added belief names, i.e. the plan will be executed whenever a fact is added to the given belief set(s).
	 */
	public String[] factaddeds() default {};
	
	/**
	 *  The fact removed belief names, i.e. the plan will be executed whenever a fact is removed from the given belief set(s).
	 */
	public String[] factremoveds() default {};
	
	/**
	 *  The fact changed belief names, i.e. the plan will be executed whenever a fact of a given belief (set) changes.
	 */
	public String[] factchangeds() default {};
	
	/**
	 *  Activate from service invocation, i.e. choose this plan to handle service invocation requests of the given type.
	 */
	// TODO: allow multiples?
	public ServiceTrigger service() default @ServiceTrigger();
}
