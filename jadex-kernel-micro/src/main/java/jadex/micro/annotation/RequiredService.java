package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.service.RequiredServiceInfo;

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
	public Class type();
	
	/** 
	 *  Flag if binding is dynamic. 
	 */
	public boolean dynamic() default false;

	/** 
	 *  Flag if multiple services should be returned. 
	 */
	public boolean multiple() default false;

	/** 
	 * The search scope. 
	 */
	public String scope() default RequiredServiceInfo.SCOPE_APPLICATION;
}
