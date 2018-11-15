package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Required service data.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredService
{	
	/** The raw proxy type (i.e. no proxy). */
	public static final String	PROXYTYPE_RAW	= BasicServiceInvocationHandler.PROXYTYPE_RAW;
	
	/** The direct proxy type (supports custom interceptors, but uses caller thread). */
	public static final String	PROXYTYPE_DIRECT	= BasicServiceInvocationHandler.PROXYTYPE_DIRECT;
	
	/** The (default) decoupled proxy type (decouples from component thread to caller thread). */
	public static final String	PROXYTYPE_DECOUPLED	= BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED;
	
	/** 
	 *  The component internal service name. 
	 */
	public String name();
	
	/** 
	 *  The service interface type. 
	 */
	public Class<?> type();
	
	/**
	 *  The search scope.
	 */
	public ServiceScope scope() default ServiceScope.DEFAULT;
	
	/** 
	 *  The service tags to search for. 
	 */
	public String[] tags() default {};

	/** 
	 *  Flag if multiple services should be returned. 
	 */
	public boolean multiple() default false;

//	/**
//	 *  The binding.
//	 */
//	public Binding binding() default @Binding();
	
//	/**
//	 *  The multiplex type.
//	 */
//	public Class<?> multiplextype() default Object.class;
	// Dropped support for v4

	/**
	 *  The required service non functional properties.
	 */
	public NFRProperty[] nfprops() default {};
	
	// todo: remove the following?!
	
	/**
	 *  The proxy type.
	 */
	public String proxytype() default PROXYTYPE_DECOUPLED;
	
	/**
	 *  The interceptors.
	 */
	public Value[] interceptors() default {};	
}
