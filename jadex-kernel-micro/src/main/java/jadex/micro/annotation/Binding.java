package jadex.micro.annotation;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.BasicServiceInvocationHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The argument annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding
{
	//-------- constants --------
	
	/** The raw proxy type (i.e. no proxy). */
	public static final String	PROXYTYPE_RAW	= BasicServiceInvocationHandler.PROXYTYPE_RAW;
	
//	/** The direct proxy type (supports custom interceptors, but uses caller thread). */
//	public static final String	PROXYTYPE_DIRECT	= BasicServiceInvocationHandler.PROXYTYPE_DIRECT;
	
	/** The (default) decoupled proxy type (decouples from component thread to caller thread). */
	public static final String	PROXYTYPE_DECOUPLED	= BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED;
	
	//-------- properties --------
	
	/**
	 *  The proxy type.
	 */
	public String proxytype() default PROXYTYPE_DECOUPLED;
	
	/**
	 *  The argument name.
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
	
	/**
	 *  The component filename.
	 */
	public String componentfilename() default "";
	
	/**
	 *  The search scope.
	 */
	public String scope() default RequiredServiceInfo.SCOPE_APPLICATION;

	/**
	 *  The dynamic binding flag.
	 */
	public boolean dynamic() default false;

	/**
	 *  The create component flag.
	 */
	public boolean create() default false;
	
	/**
	 *  The error recover flag.
	 */
	public boolean recover() default false;
	
	/**
	 *  The interceptors.
	 */
	public Value[] interceptors() default {};
}
