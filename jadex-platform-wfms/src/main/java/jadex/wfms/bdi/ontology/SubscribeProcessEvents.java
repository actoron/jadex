package jadex.wfms.bdi.ontology;

public class SubscribeProcessEvents
{
	private boolean finishedEvents;
	
	public SubscribeProcessEvents()
	{
		finishedEvents = true;
	}

	/**
	 *  Get the finishedEvents.
	 *  @return The finishedEvents.
	 */
	public boolean isFinishedEvents()
	{
		return finishedEvents;
	}

	/**
	 *  Set the finishedEvents.
	 *  @param finishedEvents The finishedEvents to set.
	 */
	public void setFinishedEvents(boolean finishedEvents)
	{
		this.finishedEvents = finishedEvents;
	}
	
	
}
