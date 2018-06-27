package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for injecting component parent to a field.
 *  Allowed field types are IExternalAccess, IInternalAccess and its subclasses
 *  as well as the concrete implementation class for pojo components.
 *  Internal and pojo access is only allowed for synchronous components.
 *  Non-synchronous components must use IExternalAccess.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parent
{

}
