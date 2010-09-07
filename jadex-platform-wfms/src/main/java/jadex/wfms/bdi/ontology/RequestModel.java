package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;
import jadex.bridge.IModelInfo;

public class RequestModel implements IComponentAction
{
	private String modelName;
	
	private boolean modelNamePath;
	
	/** The model info. */
	private IModelInfo modelInfo;
	
	/**
	 *  Get the modelInfo.
	 *  @return The modelInfo.
	 */
	public IModelInfo getModelInfo()
	{
		return modelInfo;
	}

	/**
	 *  Set the modelInfo.
	 *  @param modelInfo The modelInfo to set.
	 */
	public void setModelInfo(IModelInfo modelInfo)
	{
		this.modelInfo = modelInfo;
	}

	/**
	 *  Get the modelName.
	 *  @return The modelName.
	 */
	public String getModelName()
	{
		return modelName;
	}

	/**
	 *  Set the modelName.
	 *  @param modelName The modelName to set.
	 */
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}
	
	/**
	 *  Get the modelNamePath.
	 *  @return The modelNamePath.
	 */
	public boolean isModelNamePath()
	{
		return modelNamePath;
	}

	/**
	 *  Set the modelNamePath.
	 *  @param modelNamePath The modelNamePath to set.
	 */
	public void setModelNamePath(boolean modelNamePath)
	{
		this.modelNamePath = modelNamePath;
	}
}
