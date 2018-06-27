package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  Precondition for checking if the argument is a valid index.
 *
 *  The value of this precondition is used to determine
 *  the argument number with the collection or array to check
 *  the index against. The check tests index>=0 && collection.size()>index
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface CheckIndex
{
	/**
	 *  The argument number with the array or collection or array.
	 */
	public int value();
}
