package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.Boolean3;


/**
 *  Configuration annotation.
 */
//@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Target(ElementType.ANNOTATION_TYPE)
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
	 *  The scope flag.
	 */
	public String scope() default RequiredServiceInfo.SCOPE_GLOBAL;
	
//	/**
//	 *  The master flag.
//	 */
//	public Boolean3 master() default Boolean3.NULL;
//	
//	/**
//	 *  The master flag.
//	 */
//	public Boolean3 daemon() default Boolean3.NULL;
//	
//	/**
//	 *  The autoshutdown flag.
//	 */
//	public Boolean3 autoshutdown() default Boolean3.NULL;
	
	/**
	 *  The synchronous flag.
	 */
	public Boolean3 synchronous() default Boolean3.NULL;
	
	/**
	 *  The persistable flag.
	 */
	public Boolean3 persistable() default Boolean3.NULL;
	
	/**
	 *  The suspend flag.
	 */
	public Boolean3 suspend() default Boolean3.NULL;
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
