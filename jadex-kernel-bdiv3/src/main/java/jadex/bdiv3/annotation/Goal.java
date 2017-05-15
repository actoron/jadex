package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bdiv3.model.MProcessableElement.ExcludeMode;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Goal
{
	/** The exclude mode. */
//	public enum ExcludeMode
//	{
//		Never(MProcessableElement.EXCLUDE_NEVER), 
//		WhenTried(MProcessableElement.EXCLUDE_WHEN_TRIED), 
//		WhenSucceeded(MProcessableElement.EXCLUDE_WHEN_SUCCEEDED), 
//		WhenFailed(MProcessableElement.EXCLUDE_WHEN_FAILED);
//		
//		protected String str;
//		ExcludeMode(String str)
//		{
//			this.str = str;
//		}
//		
//		/**
//		 *  Get the string representation.
//		 *  @return The string representation.
//		 */
//		public String getString()
//		{
//			return str;
//		}
//	}
	
	/**
	 *  The goal class.
	 */
	public Class<?> clazz() default Object.class;
	
	/**
	 *  Post the goal to all plans of the APL in parallel.
	 */
	public boolean posttoall() default false;
	
	/**
	 *  Select a plan randomly.
	 */
	public boolean randomselection() default false;
	
	/**
	 *  Rebuild the APL on each retry;
	 */
	public boolean rebuild() default false;
	
	/**
	 *  When to exclude a plan from the APL after it has been executed.
	 *  Default is to execute it after it has been tried without consideration
	 *  of its success state.
	 */
	public ExcludeMode excludemode() default ExcludeMode.WhenTried;//MProcessableElement.EXCLUDE_WHEN_TRIED;
	
	/** 
	 *  The retry flag. Is means-end reasoning allowed to select a new plan and execute it.
	 */
	public boolean retry() default true;
	
	/** 
	 *  The recur flag. Starts over a new round of means-end reasoning. The APL
	 *  is rebuild and the exclude set is cleared.
	 */
	public boolean recur() default false;
	
	/** 
	 *  The retry delay. 
	 */
	public long retrydelay() default -1;
	
	/** 
	 *  The recur delay. 
	 */
	public long recurdelay() default -1;

	/**
	 *  Should a procedural goal succeed when first plan executed successfully
	 *  or after all plans have been executed (with at least one passed plan). 
	 *  Plan success flag. Determines when a goal is succeeded
	 *  depending on plan success. Default a procedural goal 
	 *  has succeeded when the first plan has passed.
	 *  If not 'or' but 'and' mode is used the goal will
	 *  only succeed after execution of the last plan from the APL.
	 */
	public boolean orsuccess() default true;
	
	/**
	 *  Should the goal be unique (no other goal is allowed that is the same).
	 */
	public boolean unique() default false;
	
	/**
	 *  The deliberation settings.
	 */
	public Deliberation deliberation() default @Deliberation();
	
	/**
	 *  The publication settings. Can be used to export goal
	 *  as a component service.
	 */
	public Publish publish() default @Publish(type=Object.class);

	/**
	 *  The goal trigger. Used in case the goal should be used as plan for another goal.
	 */
	public Class<?>[] triggergoals() default {};
}
