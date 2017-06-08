package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Mark a class as implementing a service.
 *  Applicable to the type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service
{
	/**
	 *  Supply the interface.
	 */
	public Class<?> value() default Object.class;
	
	/**
	 *  Flag if it is a system service.
	 */
	public boolean system() default false;
}
