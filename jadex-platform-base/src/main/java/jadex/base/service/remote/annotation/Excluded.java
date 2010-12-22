package jadex.base.service.remote.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Excluded methods throw UnsupportedOperationException
 *  when called from remote.
 *  
 *  Applicable to all methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excluded
{
}
