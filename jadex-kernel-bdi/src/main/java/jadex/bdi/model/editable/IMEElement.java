package jadex.bdi.model.editable;

import jadex.bdi.model.IMElement;

/**
 * 
 */
public interface IMEElement extends IMElement
{
	/**
	 *  Set the name.
	 *  @param name The name. 
	 */
	public void setName(String name);
	
	/**
	 *  Set the description.
	 *  @param desc The description. 
	 */
	public void setDescription(String desc);
}
