package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  Description of the 
 *  
 *  Applicable the type.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration
{	
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
}




