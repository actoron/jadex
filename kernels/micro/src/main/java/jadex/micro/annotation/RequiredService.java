package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Required service data.
 *  
 *  todo: support hard constraints
 *  todo: support ranking
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
	
	/** Constant for multiplicity many. */
	public static final int MANY = RequiredServiceInfo.MANY;
	
	/** Constant for multiplicity undefined. */
	public static final int UNDEFINED = RequiredServiceInfo.UNDEFINED;
	
	/** 
	 *  The component internal service name. 
	 */
	public String name() default "";
	
	/** 
	 *  The service interface type. 
	 */
	public Class<?> type() default Object.class;
	
	/**
	 *  The search scope.
	 */
	public ServiceScope scope() default ServiceScope.DEFAULT;
	
	/** 
	 *  The service tags to search for. 
	 */
	public String[] tags() default {};

//	/** 
//	 *  Flag if multiple services should be returned. 
//	 */
//	public boolean multiple() default false;
	
	/**
	 *  The minimum number of services.
	 */
	public int min() default UNDEFINED;
	
	/**
	 *  The maximum number of services
	 */
	public int max() default UNDEFINED;

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
	 *  NOT used for search, but for adding required service properties.
	 */
	public NFRProperty[] nfprops() default {};
	
	// todo: remove the following?!
	
	/**
	 *  The proxy type.
	 */
	@Deprecated
	public String proxytype() default PROXYTYPE_DECOUPLED;
	
	/**
	 *  The interceptors.
	 */
	@Deprecated
	public Value[] interceptors() default {};	
}
