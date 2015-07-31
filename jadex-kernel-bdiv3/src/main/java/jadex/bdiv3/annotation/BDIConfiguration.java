package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.micro.annotation.Component;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.RequiredService;


/**
 *  Redefines jadex.micro.annotation.Configuration
 *  as annotations do not allow inheritance.
 *  
 *  Configuration annotation.
 */
//@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BDIConfiguration
{	
	//-------- methods of Configuration --------
	
	/**
	 *  The configuration name.
	 */
	public String name();
	
	/**
	 *  The arguments.
	 */
	public NameValue[] arguments() default {};
	
	/**
	 *  The results.
	 */
	public NameValue[] results() default {};

	/**
	 *  The components.
	 */
	public Component[] components() default {};
	
	/**
	 *  The provided service implementations.
	 */
	public ProvidedService[] providedservices() default {};
	
	/**
	 *  The provided service implementations.
	 */
	public RequiredService[] requiredservices() default {};
	
	/**
	 *  The master flag.
	 */
	public boolean master() default false;
	
	/**
	 *  The master flag.
	 */
	public boolean daemon() default false;
	
	/**
	 *  The autoshutdown flag.
	 */
	public boolean autoshutdown() default false;
	
	/**
	 *  The synchronous flag.
	 */
	public boolean synchronous() default false;
	
	/**
	 *  The persistable flag.
	 */
	public boolean persistable() default false;
	
	/**
	 *  The suspend flag.
	 */
	public boolean suspend() default false;
	
	//-------- new bdi methods --------
	
	/**
	 *  The initial beliefs.
	 */
	public NameValue[] initialbeliefs() default {};

	/**
	 *  The initial goals.
	 */
	public NameValue[] initialgoals() default {};

	/**
	 *  The initial plans.
	 */
	public NameValue[] initialplans() default {};

	/**
	 *  The end beliefs.
	 */
	public NameValue[] endbeliefs() default {};

	/**
	 *  The end goals.
	 */
	public NameValue[] endgoals() default {};

	/**
	 *  The end plans.
	 */
	public NameValue[] endplans() default {};
}




