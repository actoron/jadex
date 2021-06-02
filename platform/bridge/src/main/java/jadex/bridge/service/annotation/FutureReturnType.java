package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Allows for defining a concrete Future return type of a method.
 *  Helpful fpr reflective or generic methods with return type IFuture.
 *  In each call an argument can be passed how the return type of this
 *  call looks like. Used e.g. for the reflective IService.invokeMethod().
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureReturnType
{
}
