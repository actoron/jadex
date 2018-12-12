package jadex.micro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define component types that can be instantiated as subcomponents of the declaring component.
 * This components can be instantiated either by referring to in a {@link Binding} annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentTypes
{
	/**
	 *  The component types.
	 */
	public ComponentType[] value() default {};
	
	/**
	 *  Replace content of the base classes.
	 */
	public boolean replace() default false;
}
