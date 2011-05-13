package jadex.micro.annotation;

import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 * 
 */
public @interface Implementation
{
	//-------- constants --------
	
	/** The raw proxy type (i.e. no proxy). */
	public static final String	PROXYTYPE_RAW	= BasicServiceInvocationHandler.PROXYTYPE_RAW;
	
	/** The direct proxy type (supports custom interceptors, but uses caller thread). */
	public static final String	PROXYTYPE_DIRECT	= BasicServiceInvocationHandler.PROXYTYPE_DIRECT;
	
	/** The (default) decoupled proxy type (decouples from caller thread to component thread). */
	public static final String	PROXYTYPE_DECOUPLED	= BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED;
	
	//-------- properties --------
	
	/**
	 *  The proxy type.
	 */
	public String proxytype() default PROXYTYPE_DECOUPLED;
	
	/**
	 *  The creation class.
	 */
	public Class value() default Object.class;

	/**
	 *  The creation expression.
	 */
	public String expression() default "";

	/**
	 *  The binding for forwarding service calls to another component.
	 */
	public Binding binding() default @Binding();
}
