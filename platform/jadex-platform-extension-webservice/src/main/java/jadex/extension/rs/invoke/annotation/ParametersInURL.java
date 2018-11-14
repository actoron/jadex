package jadex.extension.rs.invoke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation that can be used to state if the
 *  parameters should be passed in the url or not.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersInURL
{
	/**
	 *  The flag if parameters should be passed in url.
	 */
	public boolean value() default true;
}
