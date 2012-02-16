package jadex.extension.rs.publish.annotation;

import jadex.micro.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMapper
{
	/**
	 *  The method name.
	 */
	public Value value();
}

