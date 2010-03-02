package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;

import java.util.Set;

public class RequestWorkitemList implements IComponentAction
{
	private Set workitems;
	
	public RequestWorkitemList()
	{
	}
	
	public void setWorkitems(Set workitems)
	{
		this.workitems = workitems;
	}
	
	public Set getWorkitems()
	{
		return workitems;
	}
	
	
}
