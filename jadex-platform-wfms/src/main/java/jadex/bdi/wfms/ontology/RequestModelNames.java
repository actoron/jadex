package jadex.bdi.wfms.ontology;

import java.util.Set;

import jadex.adapter.base.fipa.IAgentAction;

public class RequestModelNames implements IAgentAction
{
	/** The model names */
	private Set modelNames;
	
	public Set getModelNames()
	{
		return modelNames;
	}
	
	public void setModelNames(Set modelNames)
	{
		this.modelNames = modelNames;
	}
}
