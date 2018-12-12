package jadex.commons.transformation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Include all values of (public) fields when
 *  serializing the object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IncludeFields
{
    /**
     * If set, all private fields are included, too.
     **/
    boolean includePrivate() default false;


}
