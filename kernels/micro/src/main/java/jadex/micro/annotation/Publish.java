package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;

/**
 *  The name (for referencing/overriding).
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish
{
	/**
	 *  The publishing id, e.g. url or name.
	 */
	public String publishid();
	
	/**
	 *  The publishing type, e.g. web service.
	 */
	public String publishtype();
	
	/**
	 *  The scope user to find the publish service.
	 */
	public ServiceScope publishscope() default ServiceScope.PLATFORM;
	
	/**
	 *  Flag if the service should be published to multiple locations.
	 */
	public boolean multi() default false;
	
	/**
	 * The mapping information (e.g. annotated interface). 
	 */
	public Class<?> mapping() default Object.class;
	
	/**
	 *  Additional mapping properties. 
	 */
	public NameValue[] properties() default {};
}
