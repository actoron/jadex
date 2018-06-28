package jadex.extension.rs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method responds to HTTP OPTIONS requests
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
// @HttpMethod(HttpMethod.OPTIONS)
public @interface OPTIONS
{
}
