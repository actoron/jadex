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
}
