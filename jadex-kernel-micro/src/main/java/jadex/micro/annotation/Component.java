package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	public boolean suspend() default false;
	
	/**
	 *  The master flag.
	 */
	public boolean master() default false;
	
	/**
	 *  The daemon flag.
	 */
	public boolean daemon() default false;
	
	/**
	 *  The autoshutdown flag.
	 */
	public boolean autoshutdown() default false;

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
