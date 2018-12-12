package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Mapping, e.g. for beliefs in subcapabilities.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping
{
	/**
	 * 	The name of the outer element.
	 */
	public String value();
	
	/**
	 *  The name of the inner element, if different from outer.
	 */
	public String target() default "";
}
