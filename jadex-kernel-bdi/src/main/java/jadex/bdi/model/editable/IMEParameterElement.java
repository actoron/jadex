package jadex.bdi.model.editable;

import jadex.bdi.model.IMParameterElement;

/**
 * 
 */
public interface IMEParameterElement extends IMEReferenceableElement, IMParameterElement
{
	/**
	 *  Get a parameter.
	 *  @return The parameter.
	 */
	public IMEParameter createParameter(String name);
	
	/**
	 *  Get a parameter set.
	 *  @return The parameter set.
	 */
	public IMEParameterSet createParameterSet(String name);
}
