package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  If void methods are declared synchronous they will block
 *  the caller until the method has been executed on the
 *  remote side (exceptions thus can arrive).
 *  Synchronous methods are discouraged as they may lead to deadlocks.
 *  
 *  Applicable for methods with void return value
 *  that are asynchronous by default.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Synchronous
{
}
