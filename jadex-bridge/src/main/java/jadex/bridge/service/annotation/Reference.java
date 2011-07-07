package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Can be used to give objects reference semantics in local
 *  and remote calls:
 *  - in local calls references are not copied.
 *  - in remote calls references are made to proxy objects.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference
{
	/**
	 *  Set the local reference state.
	 */
	public boolean local() default true;
	
	/**
	 *  Set the remote reference state.
	 */
	public boolean remote() default true;
}
