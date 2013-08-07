package jadex.bridge.nonfunctional.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The provided services annotation.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NFProperties
{
	/**
	 *  The non-functional properties.
	 */
	public NFProperty[] value();
}
