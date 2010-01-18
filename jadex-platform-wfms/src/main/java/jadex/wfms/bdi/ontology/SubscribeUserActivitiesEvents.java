package jadex.wfms.bdi.ontology;

public class SubscribeUserActivitiesEvents
{
	private boolean addEvents;
	
	private boolean removeEvents;
	
	public SubscribeUserActivitiesEvents()
	{
		addEvents = true;
		removeEvents = true;
	}
	
	public boolean isAddEvents()
	{
		return addEvents;
	}
	
	public boolean isRemoveEvents()
	{
		return removeEvents;
	}
	
	public void setAddEvents(boolean addEvents)
	{
		this.addEvents = addEvents;
	}
	
	public void setRemoveEvents(boolean removeEvents)
	{
		this.removeEvents = removeEvents;
	}
}
