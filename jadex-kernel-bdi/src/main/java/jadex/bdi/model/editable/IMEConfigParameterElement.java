package jadex.bdi.model.editable;

import jadex.bdi.model.IMConfigParameterElement;

/**
 * 
 */
public interface IMEConfigParameterElement extends IMConfigParameterElement, IMEElement
{
	/**
	 *  Create a parameter.
	 *  @param ref The name.
	 *  @return The parameter.
	 */
	public IMEParameter createParameter(String ref);
	
	/**
	 *  Create a parameter set.
	 *  @param name The name.
	 *  @return The parameter sets.
	 */
	public IMEParameterSet createParameterSet(String ref);
}
