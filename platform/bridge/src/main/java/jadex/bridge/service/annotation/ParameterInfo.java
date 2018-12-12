package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Parameter info annotation.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterInfo
{
	/**
	 *  The parameter name.
	 */
	public String value();

//	/**
//	 *  The parameter description.
//	 */
//	public String description() default "";
}	
