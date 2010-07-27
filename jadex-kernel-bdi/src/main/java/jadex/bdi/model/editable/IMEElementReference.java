package jadex.bdi.model.editable;

import jadex.bdi.model.IMElementReference;

/**
 * 
 */
public interface IMEElementReference extends IMElementReference, IMEReferenceableElement
{
	/**
	 *  Set concrete element name.
	 *  @param concrete The concrete element name. 
	 */
	public void setConcrete(String concrete);
}
