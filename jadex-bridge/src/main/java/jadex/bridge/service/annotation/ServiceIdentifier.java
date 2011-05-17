package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation for field of pojo service in which
 *  the service identifier should be injected.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceIdentifier
{
	/**
	 *  Supply the interface.
	 */
	public Class value() default Object.class;
}
