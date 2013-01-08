package jadex.bdiv3.annotation;

import jadex.bdiv3.model.MProcessableElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Goal
{
	/**
	 * 
	 */
	public boolean posttoall() default false;
	
	/**
	 * 
	 */
	public boolean randomselection() default false;
	
	/**
	 * 
	 */
	public boolean rebuild() default false;
	
	/**
	 * 
	 */
	public String excludemode() default MProcessableElement.EXCLUDE_WHEN_TRIED;
	
	/** 
	 * The retry flag. 
	 */
	public boolean retry() default true;
	
	/** 
	 * The recur flag. 
	 */
	public boolean recur() default true;
	
	/** 
	 * The retry delay. 
	 */
	public long retrydelay() default -1;
	
	/** 
	 * The recur delay. 
	 */
	public long recurdelay() default -1;

	/**
	 *  Should the goal procedural succeed when first plan executed successfully. 
	 */
	public boolean succeedonpassed() default true;

}
