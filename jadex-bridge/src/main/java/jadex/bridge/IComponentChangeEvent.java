package jadex.bridge;

public interface IComponentChangeEvent
{
	public static final String EVENT_TYPE_CREATION		= "created";
	public static final String EVENT_TYPE_DISPOSAL		= "disposed";
	public static final String EVENT_TYPE_MODIFICATION 	= "modified";
	public static final String EVENT_TYPE_OCCURRENCE	= "noticed";
	public static final String EVENT_TYPE_BULK = "bulk";
	
	public static final String SOURCE_CATEGORY_COMPONENT = "Component";
	
	/**
	 *  Returns the type of the event.
	 *  @return The type of the event.
	 */
	public String getEventType();
	
	/**
	 *  Returns the time when the event occured.
	 *  @return Time of event.
	 */
	public long getTime();
	
	/**
	 *  Returns the name of the source type that caused the event.
	 *  @return Name of the source.
	 */
	public String getSourceName();
	
	/**
	 *  Returns the type of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceType();
	
	/**
	 *  Returns the category of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceCategory();
	
	/**
	 *  Returns the component that generated the event.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getComponent();
	
	/**
	 *  Returns the parent component of the component that generated the event, if any.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getParent();
	
	/**
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason();
	
	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public String getDetails();
	
	/**
	 *  Get the bulk events.
	 *  @return The bulk events.
	 */
	public IComponentChangeEvent[] getBulkEvents();
}
