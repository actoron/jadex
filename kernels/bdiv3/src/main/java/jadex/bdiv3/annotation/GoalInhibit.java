package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The inhibit annotation can be placed on a method of a goal class
 *  to form a so called instance-level inhibition arc, i.e. an inhibition
 *  relationship defined between specific instances of goals instead
 *  of all goals of a class.
 *  
 *  The method should have a parameter for the "other" goal and return
 *  a boolean to denote that this goal instance should inhibit the
 *  goal instance provided in the parameter (true) or not (false).
 *  
 *  
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalInhibit
{
	/**
	 *  The inhibited goal type specifies for which other goals this method is called.
	 */
	public Class<?> value();// default Object.class;
}
