package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify a method as uncached.
 *  Methods with no parameters that have a non-void and non-future return type
 *  are cached by default, i.e. the return value is pre-calculated and
 *  transferred in the remote reference.
 *  If a method is specified as uncached, it will be invoked on every call.
 *  
 *  Applicable for methods with no parameters
 *  that have a non-void and non-future return type.
 *  These methods are cached by default.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Uncached
{
}
