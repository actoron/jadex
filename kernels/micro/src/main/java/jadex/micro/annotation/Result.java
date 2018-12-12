package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The result annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Result
{
	/**
	 *  The argument name.
	 */
	public String name();
	
	/**
	 *  The description.
	 */
	public String description() default "";
	
//	/**
//	 *  The class name.
//	 */
//	public String classname();
	
	/**
	 *  The class.
	 */
	public Class<?> clazz();
	
	/**
	 *  The default value as expression string, i.e. will be parsed.
	 */
	public String defaultvalue() default "";
}
