package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the media type(s) that the methods of a resource class can produce.
 * If not specified then a container will assume that any type can be produced.
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces
{
	/**
	 * List of media types. Entries can be comma separated, e.g.
	 * {"image/jpeg,image/gif", "image/png"}.
	 */
	String[] value() default "*/*";
}
