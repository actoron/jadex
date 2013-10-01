package jadex.micro.annotation;

import jadex.bridge.nonfunctional.annotation.NFRProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Required service data.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredService
{
	/** 
	 *  The component internal service name. 
	 */
	public String name();
	
	/** 
	 *  The service interface type. 
	 */
	public Class<?> type();

	/** 
	 *  Flag if multiple services should be returned. 
	 */
	public boolean multiple() default false;

	/**
	 *  The binding.
	 */
	public Binding binding() default @Binding();
	
	/**
	 *  The multiplex type.
	 */
	public Class<?> multiplextype() default Object.class;

	/**
	 *  The required service non functional properties.
	 */
	public NFRProperty[] nfprops() default {};
}
