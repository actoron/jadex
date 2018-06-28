package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Injects a service into a field or a method of a component.
 *  The referenced service must be declared with a {@link RequiredService} annotation.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentServiceQuery
{
//	/**
//	 *  The required service name that is used for searching the service.
//	 */
//	public String name() default "";
//	
//	/**
//	 *  Fail at startup if no service is found?
//	 */
//	public boolean required() default true;
//	
//	/**
//	 *  If is lazy the service search will happen on first call.
//	 *  This can go wrong if first call is a synchronous message.
//	 *  If lazy is false, the agent might block when search takes time.
//	 */
//	public boolean lazy() default true;
	
	/**
	 *  Define a required service inline. If given no name it will receive
	 *  the field name as name. 
	 */
	public RequiredService requiredservice() default @RequiredService(name="", type=Object.class);
	
//	/**
//	 *  Should be used as service query.
//	 */
//	public boolean isquery() default false;
	
	
	// copied from RequiredService :-(
	
//	/** 
//	 *  The service tags to search for. 
//	 */
//	public String[] tags() default {};

	/** 
	 *  The service interface type. 
	 */
	public Class<?> type() default Object.class;
	
	/** 
	 *  Flag if multiple services should be returned. 
	 */
	public boolean multiple() default true; 
	
	/**
	 *  The search scope.
	 */
	public String scope() default "";
}