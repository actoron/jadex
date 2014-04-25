package jadex.bridge.modelinfo;

import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Interface for a persistable component state
 *  to be used from the outside (e.g. persistence service).
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
