package jadex.rules.eca.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Representation of a condition.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition
{
	/**
	 *  The condition name.
	 */
	public String value();
}
