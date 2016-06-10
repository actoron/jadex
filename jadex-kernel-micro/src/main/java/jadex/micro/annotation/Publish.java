package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.bridge.nonfunctional.annotation.NameValue;

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
	 * The mapping information (e.g. annotated interface). 
	 */
	public Class<?> mapping() default Object.class;
	
	/**
	 *  Additional mapping properties. 
	 */
	public NameValue[] properties() default {};
}
