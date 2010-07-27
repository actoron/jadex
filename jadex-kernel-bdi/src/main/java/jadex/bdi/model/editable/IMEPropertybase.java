package jadex.bdi.model.editable;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.IMPropertybase;

/**
 * 
 */
public interface IMEPropertybase extends IMPropertybase, IMElement
{
	/**
	 *  Create a property.
	 *  @param name The name.
	 *  @param content The content.
	 *  @param lang The language.
	 *  @return The property.
	 */
	public IMEExpression createProperty(String name, String content, String lang);
}
