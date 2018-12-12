package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Gui representations for a service.
 *  
 *  Applicable the type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuiClassNames
{
	/**
	 *  Supply a class name of a class implementing the gui.
	 */
	public GuiClassName[] value();
}
