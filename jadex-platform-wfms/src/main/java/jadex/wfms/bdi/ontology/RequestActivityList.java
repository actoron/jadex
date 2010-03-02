package jadex.wfms.bdi.ontology;

import jadex.base.fipa.IComponentAction;

import java.util.Set;

public class RequestActivityList implements IComponentAction
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
