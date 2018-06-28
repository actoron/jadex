package jadex.bdiv3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Marker for a capability class or variable.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Capability
{
	/**
	 *  Belief mappings from outer beliefs to inner abstract beliefs.
	 */
	public Mapping[]	beliefmapping() default {};	
}
