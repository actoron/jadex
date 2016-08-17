package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method annotated with this Annotation should return a boolean indicating whether the condition is met.
 *
 * Used on a constructor, this annotation contains the condition:
 * if the specified belief is not null / does contain elements, the value will be injected into the constructor
 * and the goal will be instantiated.
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalCreationCondition
{
	/**
	 *  The events this condition should react to.
	 */
	public String[] beliefs() default {};
	
	/**
	 *  The parameters this condition should react to.
	 */
	public String[] parameters() default {};
	
	/**
	 *  The events this condition should react to.
	 */
	public RawEvent[] rawevents() default {};
}
