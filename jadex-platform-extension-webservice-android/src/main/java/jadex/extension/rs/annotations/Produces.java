package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the media type(s) that the methods of a resource class or
 * {@link javax.ws.rs.ext.MessageBodyWriter} can produce.
 * If not specified then a container will assume that any type can be produced.
 * Method level annotations override a class level annotation. A container
 * is responsible for ensuring that the method invoked is capable of producing
 * one of the media types requested in the HTTP request. If no such method is
 * available the container must respond with a HTTP "406 Not Acceptable" as
 * specified by RFC 2616.
 *
 * <p>A method for which there is a single-valued <code>Produces</code>
 * is not required to set the media type of representations that it produces:
 * the container will use the value of the <code>Produces</code> when
 * sending a response.</p>
 *
 * @see javax.ws.rs.ext.MessageBodyWriter
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    /**
     * A list of media types. Each entry may specify a single type or consist
     * of a comma separated list of types. E.g. {"image/jpeg,image/gif",
     * "image/png"}. Use of the comma-separated form allows definition of a
     * common string constant for use on multiple targets.
     */
    String[] value() default "*/*";    
}
