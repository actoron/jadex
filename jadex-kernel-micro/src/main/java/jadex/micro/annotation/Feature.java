package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The features annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature
{
	/** 
	 * Get the interface type. 
	 */
	public Class<?> type();
	
	/** 
	 *  Get the implementation type.
	 */ 
	public Class<?> clazz();
	
	/** 
	 *  The predecessors. 
	 */
	public Class<?>[] predecessors() default {};
	
	/** 
	 *  The successors. 
	 */
	public Class<?>[] successors() default {};
	
	/**
	 *  Flag if default last feature dependency should be added.
	 */
	public boolean addlast() default true;
	
	/**
	 *  Replace content of the existing classes.
	 */
	public boolean replace() default true;
}

