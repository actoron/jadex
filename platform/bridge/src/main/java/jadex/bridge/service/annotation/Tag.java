package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag
{
	/**
	 *  The tags as strings or expression.
	 */
	public String value();

	/**
	 *  Condition to check if the value/tag should be included. 
	 */
	public String include() default "";
}
