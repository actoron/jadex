package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The argument annotation.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument
{
	/**
	 *  The argument name.
	 */
	public String name();
	
	/**
	 *  The description.
	 */
	public String description();
	
	/**
	 *  The type name.
	 */
	public String typename();
	
	/**
	 *  The default value as expression string, i.e. will be parsed.
	 */
	public String defaultvalue();
}

