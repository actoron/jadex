package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the media types that the methods of a resource class or 
 * {@link javax.ws.rs.ext.MessageBodyReader} can accept. If
 * not specified, a container will assume that any media type is acceptable.
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumes {
    /**
     * A list of media types. Each entry may specify a single type or consist
     * of a comma separated list of types. E.g. {"image/jpeg,image/gif",
     * "image/png"}. Use of the comma-separated form allows definition of a
     * common string constant for use on multiple targets.
     */
    String[] value() default "*/*";
}
