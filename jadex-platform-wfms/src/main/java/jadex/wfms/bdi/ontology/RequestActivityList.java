package jadex.wfms.bdi.ontology;

import jadex.adapter.base.fipa.IAgentAction;

import java.util.Set;

public class RequestActivityList implements IAgentAction
{
	private Set activities;
	
	public RequestActivityList()
	{
	}
	
	public Set getActivities()
	{
		return activities;
	}
	
	public void setActivities(Set activities)
	{
		this.activities = activities;
	}
}
