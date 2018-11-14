package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NFRProperty;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Required service data.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredService
{
	//-------- constants --------
	
	/** None component scope (nothing will be searched, forces required service creation). */
	public static final String SCOPE_NONE = RequiredServiceInfo.SCOPE_NONE;
	
	/** Parent scope. */
	public static final String SCOPE_PARENT = RequiredServiceInfo.SCOPE_PARENT;
	
	// todo: rename (COMPONENT_LOCAL)
	/** Local component scope. */
	public static final String SCOPE_COMPONENT_ONLY = RequiredServiceInfo.SCOPE_COMPONENT_ONLY;
	
	/** Component scope (component and subcomponents). */
	public static final String SCOPE_COMPONENT = RequiredServiceInfo.SCOPE_COMPONENT;
	
	// todo: rename (APPLICATION_PLATFORM) or remove
	/** Application scope (local application, i.e. second level component plus all subcomponents). */
	public static final String SCOPE_APPLICATION = RequiredServiceInfo.SCOPE_APPLICATION;

	/** Platform scope (all components on the local platform). */
	public static final String SCOPE_PLATFORM = RequiredServiceInfo.SCOPE_PLATFORM;

	
	
	/** Application network scope (any platform with which a secret is shared and application tag must be shared). */
	public static final String SCOPE_APPLICATION_NETWORK = RequiredServiceInfo.SCOPE_APPLICATION_NETWORK;
//		public static final String SCOPE_APPLICATION_CLOUD = "application_cloud";
	
	/** Network scope (any platform with which a secret is shared). */
	public static final String SCOPE_NETWORK = RequiredServiceInfo.SCOPE_NETWORK;
//		public static final String SCOPE_CLOUD = "cloud";
		
	// needed?!
	/** Global application scope. */
	public static final String SCOPE_APPLICATION_GLOBAL = RequiredServiceInfo.SCOPE_APPLICATION_GLOBAL;
		
	/** Global scope (any reachable platform including those with unrestricted services). */
	public static final String SCOPE_GLOBAL = RequiredServiceInfo.SCOPE_GLOBAL;
	
	
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
	public String scope() default "";//RequiredServiceInfo.SCOPE_APPLICATION;
	
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
