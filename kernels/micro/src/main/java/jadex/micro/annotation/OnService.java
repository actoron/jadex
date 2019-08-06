package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.commons.Boolean3;

/**
 * 
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnService
{
	/**
	 *  Should be used as service query.
	 */
	public Boolean3 query() default Boolean3.NULL;
	
	//-------- search details --------
	
	/**
	 *  Fail at startup if no service is found?
	 */
	public boolean required() default true;
	
	/**
	 *  todo: remove and replace by query.
	 * 
	 *  If is lazy the service search will happen on first call.
	 *  This can go wrong if first call is a synchronous message.
	 *  If lazy is false, the agent might block when search takes time.
	 */
	public boolean lazy() default true;
	
	//-------- query details ---------
	
//	/** 
//	 *  The service interface type. 
//	 */
//	public Class<?> type() default Object.class;
//	
//	/** 
//	 *  Flag if multiple services should be returned. 
//	 */
//	public boolean multiple() default true; 
//	
//	/**
//	 *  The search scope.
//	 */
//	public ServiceScope scope() default ServiceScope.DEFAULT;
	
	/**
	 *  How long shall the query be active?
	 *  -1 for ever
	 */
	public long active() default -1;
	
	//-------- required service spec --------
	
	/**
	 *  The required service name that is used for searching the service.
	 */
	public String name() default "";
	
	/**
	 *  Define a required service inline. If given no name it will receive
	 *  the field name as name. 
	 */
	public RequiredService requiredservice() default @RequiredService(name="", type=Object.class);
}
