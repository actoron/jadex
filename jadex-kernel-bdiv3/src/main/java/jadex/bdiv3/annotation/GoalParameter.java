package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Annotation for goal parameters.
 */
@Target({ElementType.FIELD})//, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalParameter
{
}
