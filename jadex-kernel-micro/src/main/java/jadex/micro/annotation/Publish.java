package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The name (for referencing/overriding).
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Publish
{
	/**
	 *  The publishing url.
	 */
	public String url();
	
	/**
	 *  The web service interface.
	 */
	public Class type();
}
