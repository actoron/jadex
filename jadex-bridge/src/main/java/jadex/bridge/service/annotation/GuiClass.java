package jadex.bridge.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Gui representation for a service.
 *  
 *  Applicable to the type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuiClass
{	
	/**
	 *  Supply a class implementing the gui.
	 */
	public Class<?> value();
}