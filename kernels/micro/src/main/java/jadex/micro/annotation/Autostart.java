package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jadex.commons.Boolean3;

/**
 *  Autostart options for platform startup.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autostart
{
	/**
	 *  Auto-start this agent on platform startup.
	 */
	public Boolean3 value() default Boolean3.NULL;
	
	/**
	 *  Name for the started component.
	 */
	public String name() default "";
	
}
