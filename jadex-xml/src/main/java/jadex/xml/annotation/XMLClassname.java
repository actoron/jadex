package jadex.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify an identifying name for the class
 *  used to distinguish anonymous inner classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XMLClassname
{
	/**
	 *  The identifying name of the class.
	 */
	public String value();
}
