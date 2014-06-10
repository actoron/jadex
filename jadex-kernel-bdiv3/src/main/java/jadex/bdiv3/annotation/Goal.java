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
	/** The exclude mode. */
	public enum ExcludeMode
	{
		Never(MProcessableElement.EXCLUDE_NEVER), 
		WhenTried(MProcessableElement.EXCLUDE_WHEN_TRIED), 
		WhenSucceeded(MProcessableElement.EXCLUDE_WHEN_SUCCEEDED), 
		WhenFailed(MProcessableElement.EXCLUDE_WHEN_FAILED);
		
		protected String str;
		ExcludeMode(String str)
		{
			this.str = str;
		}
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
	}
	
	/**
	 * 
	 */
	public Class<?> clazz() default Object.class;
	
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
	public ExcludeMode excludemode() default ExcludeMode.WhenTried;//MProcessableElement.EXCLUDE_WHEN_TRIED;
	
	/** 
	 * The retry flag. 
	 */
	public boolean retry() default true;
	
	/** 
	 * The recur flag. 
	 */
	public boolean recur() default false;
	
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
