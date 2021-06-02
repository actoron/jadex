package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  A goal creation condition can be annotated in the following ways,
 *  which allow more or less fine-grained control over the condition
 *  and the goal creation process.
 *  
 *  1) The simplest way is to annotate the constructor of a goal class.
 *  You need to add names of beliefs, parameters or raw events that should trigger
 *  the creation of a new goal instance. For example, if a specified belief is
 *  not null / or a belief set does contain elements, the value will be injected
 *  into the constructor, if applicable, and the goal will be instantiated.
 *  
 *  2) Annotating a static method that returns a boolean value.
 *  Here you can use arbitrarily complex java code to decide,
 *  if a goal should be created. In addition, the interpreter will
 *  try to auto-detect beliefs, used as part of the condition code,
 *  so you normally do not need to specify the condition events manually.   
 * 
 *  3) Instead of returning a boolean value, a static method may also return
 *  an object of the goal type. This provides the most flexibility as it allows
 *  creating the goal object using arbitrary Java code. In addition, you can
 *  include a condition by returning null, when no goal should be created.
 *  As in case (2), auto-detection of events is supported by this option.  
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalCreationCondition
{
	/**
	 *  The fact added belief names, i.e. the goal will be created whenever a fact is added to the given belief set(s).
	 */
	public String[] factadded() default {};
	
	/**
	 *  The fact removed belief names, i.e. the goal will be created whenever a fact is removed from the given belief set(s).
	 */
	public String[] factremoved() default {};
	
	/**
	 *  The fact changed belief names, i.e. the goal will be created whenever a fact of a given belief (set) changes.
	 */
	public String[] factchanged() default {};
	
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
