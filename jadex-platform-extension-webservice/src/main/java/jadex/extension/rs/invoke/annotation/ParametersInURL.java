package jadex.extension.rs.invoke.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersInURL
{
	/**
	 *  The method name.
	 */
	public boolean value() default true;
}
