package jadex.bridge.modelinfo;

import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Interface for a persistable component state.
 *
 */
public interface IPersistInfo
{
	/**
	 *  Gets the model file name.
	 *
	 *  @return The model file name.
	 */
	public String getModelFileName();
	
	/**
	 *  Get the component description.
	 *
	 *  @return The component description
	 */
	public IComponentDescription getComponentDescription();
}
