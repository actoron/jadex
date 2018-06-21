package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for a field or method that should act as
 *  goal result.
 *  
 *  Is used for two directions. To write a plan
 *  result in the goal and to fetch the result for
 *  the call result of (dispatchGoal).
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalResult
{
}
