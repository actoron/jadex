package jadex.commons.transformation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Specify an alias for a class
 *  (e.g. the old name for compatibility after the class was renamed or moved to a different package).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Alias
{
	/**
	 *  The alias name.
	 */
	public String value();
}
