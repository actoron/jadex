package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the URI path that a resource class or class method will serve
 * requests for.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Path
{
	/**
	 * The Path.
	 * 
	 * @return
	 */
	String value();
}
