package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the media types that the methods of a resource class can accept. If
 * not specified, a container will assume that any media type is acceptable.
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumes
{
	/**
	 * List of media types. Multiple types can be comma separated, e.g.
	 * {"image/jpeg,image/gif", "image/png"}.
	 */
	String[] value() default "*/*";
}
