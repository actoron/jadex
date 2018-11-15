package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bdiv3.model.MProcessableElement.ExcludeMode;

/**
 *  Let a class be used to represent goals of a BDI agent.
 *  Can be annotated to inner classes of an agent or in a {@link Goals}
 *  annotation to refer to external classes.
 *  
 *  Settings of this annotation give detailed control over the means-end reasoning
 *  and goal deliberation processes (see {@link Deliberation}) with regard to the annotated goal.
 *  
 *  The means-end reasoning is the process of plan selection and execution and is based
 *  on the notion of an "applicable plans list" (APL). When the goal needs processing,
 *  the agent determines the applicable plans and stores them in the APL.
 *  By default the plans are tried sequentially in the given order.
 *  Several goal flags like {@link #randomselection() randomselection} and {@link #posttoall() posttoall} allow changing this behavior.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Goal
{
	/**
	 *  The goal class, when used inside a  {@link Goals} annotation.
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
	public ExcludeMode excludemode() default ExcludeMode.WhenTried;
	
	/** 
	 *  If true (default) means-end reasoning is allowed to select another plan after a plan has already been executed.
	 */
	public boolean retry() default true;
	
	/** 
	 *  If true (defaults to false), a new round of means-end reasoning is started after each plan execution.
	 *  As a result, the APL is rebuild and the exclude set is cleared.
	 */
	public boolean recur() default false;
	
	/** 
	 *  The delay between two plan executions (in milliseconds). 
	 */
	public long retrydelay() default -1;
	
	/** 
	 *  The delay (default 0 for no delay) before restarting goal processing if recur is set to true (in milliseconds).
	 */
	public long recurdelay() default 0;

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
	 *  The publication settings can be used to export goal
	 *  as a component service.
	 */
	public Publish publish() default @Publish(type=Object.class);

	/**
	 *  The goal trigger is used in case the goal should be considered as plan for another goal.
	 */
	public Class<?>[] triggergoals() default {};
}
