package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.commons.Boolean3;

/**
 *  Annotate fields and methods that will be called when the corresponding services are available.
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
	public Boolean3 required() default Boolean3.NULL;
	
	/**
	 *  todo: remove and replace by query.
	 * 
	 *  If is lazy the service search will happen on first call.
	 *  This can go wrong if first call is a synchronous message.
	 *  If lazy is false, the agent might block when search takes time on agent init.
	 */
	public Boolean3 lazy() default Boolean3.NULL;
	
	//-------- query details ---------
	
	/**
	 *  How long shall the query be active?
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
