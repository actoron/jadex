package jadex.bridge.service.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  The breakpoint annotation.
 *  Used to annotate component steps as breakpoint via execute method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Breakpoint
{
	/**
	 *  The breakpoint name.
	 */
	public String value();
}
