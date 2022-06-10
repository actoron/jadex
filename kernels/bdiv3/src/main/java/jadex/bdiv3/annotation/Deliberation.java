package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Deliberation settings.
 *  Instance level inhibitions are defined as method using the @GoalInhibit annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deliberation
{
	/**
	 *  The cardinality.
	 *  True if only one goal can be active at the same time.
	 */
	public boolean cardinalityone() default false;
	
	/**
	 *  The inhibited goal.
	 */
	public Class<?>[] inhibits() default {};
	
	/**
	 *  Suspend or drop on inhibition.
	 *  @return True, if goal should be dropped.
	 */
	public boolean droponinhibit() default false;
}
