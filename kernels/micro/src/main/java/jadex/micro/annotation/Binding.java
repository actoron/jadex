package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  The argument annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding
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
//	public static final String SCOPE_APPLICATION_CLOUD = "application_cloud";
	
	/** Network scope (any platform with which a secret is shared). */
	public static final String SCOPE_NETWORK = RequiredServiceInfo.SCOPE_NETWORK;
//	public static final String SCOPE_CLOUD = "cloud";
		
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
	
	//-------- properties --------
	
	/**
	 *  The proxy type.
	 */
	public String proxytype() default PROXYTYPE_DECOUPLED;
	
	/**
	 *  The binding name.
	 */
	public String name() default "";
	
	/**
	 *  The component name.
	 */
	public String componentname() default "";
	
	/**
	 *  The component type.
	 */
	public String componenttype() default "";
	
//	/**
//	 *  The component filename.
//	 */
//	public String componentfilename() default "";

//	/**
//	 *  The creation name.
//	 */
//	public String creationname() default "";
//	
//	/**
//	 *  The creation type.
//	 */
//	public String creationtype() default "";
	
//	/**
//	 *  The creation info (cannot use @Component as no cycles are allowed in annotations). 
//	 */
//	public CreationInfo creationinfo() default @CreationInfo;
	// Dropped support for v4
	
	/**
	 *  The search scope.
	 */
	public String scope() default "";//RequiredServiceInfo.SCOPE_APPLICATION;

//	/**
//	 *  The dynamic binding flag.
//	 */
//	public boolean dynamic() default false;
	// Dropped support for v4

//	/**
//	 *  The create component flag.
//	 */
//	public boolean create() default false;
	// Dropped support for v4
	
//	/**
//	 *  The error recover flag.
//	 */
//	public boolean recover() default false;
	// Dropped support for v4
	
	/**
	 *  The interceptors.
	 */
	public Value[] interceptors() default {};	
}
