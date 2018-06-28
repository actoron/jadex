package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.Boolean3;

/**
 *  Component annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component
{
	/**
	 *  The suspend state.
	 */
	public Boolean3 suspend() default Boolean3.NULL;
	
	/**
	 *  The master flag.
	 */
	public Boolean3 master() default Boolean3.NULL;
	
	/**
	 *  The daemon flag.
	 */
	public Boolean3 daemon() default Boolean3.NULL;
	
	/**
	 *  The autoshutdown flag.
	 */
	public Boolean3 autoshutdown() default Boolean3.NULL;

	/**
	 *  The synchronous flag.
	 */
	public Boolean3 synchronous() default Boolean3.NULL;

	/**
	 *  The persistable flag.
	 */
	public Boolean3 persistable() default Boolean3.NULL;

	/**
	 *  The component name.
	 */
	public String name() default "";
	
	/**
	 *  The local component type.
	 */
	public String type();
	
	/**
	 *  The configuration name.
	 */
	public String configuration() default "";
	
	/**
	 *  The number of components to start (parsed).
	 */
	public String number() default "";
	
	/**
	 *  The argument values.
	 */
	public NameValue[] arguments() default {};
	
	/**
	 *  The argument values.
	 */
	public Binding[] bindings() default {};
}
