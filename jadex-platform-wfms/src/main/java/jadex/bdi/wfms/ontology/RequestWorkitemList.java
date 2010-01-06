package jadex.bdi.wfms.ontology;

import jadex.adapter.base.fipa.IAgentAction;

import java.util.Set;

public class RequestWorkitemList implements IAgentAction
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
