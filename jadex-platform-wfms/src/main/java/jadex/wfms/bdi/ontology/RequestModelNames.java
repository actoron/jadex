package jadex.wfms.bdi.ontology;

import java.util.Set;

import jadex.base.fipa.IComponentAction;

public class RequestModelNames implements IComponentAction
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
