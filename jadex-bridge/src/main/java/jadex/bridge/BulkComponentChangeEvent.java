package jadex.bridge;

/**
 * 
 */
public class BulkComponentChangeEvent implements IComponentChangeEvent
{
	/** The bulk events. */
	protected IComponentChangeEvent[] events;
	
	/**
	 * 
	 */
	public BulkComponentChangeEvent()
	{
	}
	
	/**
	 * 
	 */
	public BulkComponentChangeEvent(IComponentChangeEvent[] events)
	{
		this.events = events;
	}
	
	/**
	 *  Returns the type of the event.
	 *  @return The type of the event.
	 */
	public String getEventType()
	{
		return null;
	}
	
	/**
	 *  Returns the time when the event occured.
	 *  @return Time of event.
	 */
	public long getTime()
	{
		return 0;
	}
	
	/**
	 *  Returns the name of the source type that caused the event.
	 *  @return Name of the source.
	 */
	public String getSourceName()
	{
		return null;
	}
	
	/**
	 *  Returns the type of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceType()
	{
		return null;
	}
	
	/**
	 *  Returns the category of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceCategory()
	{
		return null;
	}
	
	/**
	 *  Returns the component that generated the event.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getComponent()
	{
		return null;
	}
	
	/**
	 *  Returns creation time of the component that generated the event.
	 *  @return Parent ID.
	 */
	public long getComponentCreationTime()
	{
		return 0;
	}
	
	/**
	 *  Returns the parent of the source that generated the event, if any.
	 *  @return Parent ID.
	 */
	public String getParent()
	{
		return null;
	}
	
	/**
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason()
	{
		return null;
	}
	
	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public String getDetails()
	{
		return null;
	}
	
	/**
	 *  Get the bulk events.
	 *  @return The bulk events.
	 */
	public IComponentChangeEvent[] getBulkEvents()
	{
		return events!=null? events: new IComponentChangeEvent[0];
	}

	/**
	 *  Set the events.
	 *  @param events The events to set.
	 */
	public void setBulkEvents(IComponentChangeEvent[] events)
	{
		this.events = events;
	}
}
