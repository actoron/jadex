package jadex.bdi.wfms.ontology;

public class SubscribeWorkitemEvents
{
	private boolean addEvents;
	
	private boolean removeEvents;
	
	public SubscribeWorkitemEvents()
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
