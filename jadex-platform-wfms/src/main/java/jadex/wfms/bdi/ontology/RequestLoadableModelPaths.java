package jadex.wfms.bdi.ontology;

import java.util.Set;

import jadex.adapter.base.fipa.IAgentAction;

/**
 * Request for a potential set of loadable process models
 *
 */
public class RequestLoadableModelPaths implements IAgentAction
{
	/** The model paths */
	private Set modelPaths;
	
	/**
	 * Gets the model paths.
	 * @return the model paths
	 */
	public Set getModelPaths()
	{
		return modelPaths;
	}
	
	/**
	 * Sets the model paths.
	 * @param modelPaths the model paths
	 */
	public void setModelPaths(Set modelPaths)
	{
		this.modelPaths = modelPaths;
	}
}
