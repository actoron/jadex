package jadex.bridge.nonfunctional.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.INFProperty;

/**
 *  Property for required services and methods.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NFRProperty
{
	/**
	 *  The name of the property.
	 */
	public String name() default "";
	
	/**
	 *  The type of the property.
	 */
	public Class<? extends INFProperty> value();
	
	/**
	 *  The method name (if method property).
	 */
	public String methodname() default "";
	
	/**
	 *  The method name (if method property).
	 */
	public Class<?>[] methodparametertypes() default {};
}

