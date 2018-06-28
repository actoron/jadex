package jadex.rules.eca.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Representation of an action.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Action
{
	/**
	 *  The action name.
	 */
	public String value();
}