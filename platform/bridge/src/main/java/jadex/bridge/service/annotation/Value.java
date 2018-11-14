package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Value as class or unparsed expression string.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value
{
	/**
	 *  The value as string, will be parsed.
	 */
	public String value() default "";
	
	/**
	 *  The value as a class.
	 */
	public Class<?> clazz() default Object.class;

}
