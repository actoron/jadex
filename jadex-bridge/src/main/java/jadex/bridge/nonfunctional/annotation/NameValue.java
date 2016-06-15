package jadex.bridge.nonfunctional.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Name, value pair.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NameValue
{
	/**
	 *  The name.
	 */
	public String name();
	
	/**
	 *  The value as string, will be parsed.
	 */
	public String value() default "";
	
	/**
	 *  The values as strings, will be individually parsed.
	 */
	public String[] values() default {};
	
	/**
	 *  The value as a class.
	 */
	public Class<?> clazz() default Object.class;
	
}
