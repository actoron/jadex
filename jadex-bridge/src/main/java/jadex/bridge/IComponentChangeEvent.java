package jadex.bridge;

public interface IComponentChangeEvent
{
	public static final String EVENT_TYPE_CREATION		= "created";
	public static final String EVENT_TYPE_DISPOSAL		= "disposed";
	public static final String EVENT_TYPE_MODIFICATION 	= "modified";
	public static final String EVENT_TYPE_OCCURRENCE	= "noticed";
	public static final String EVENT_TYPE_BULK = "bulk";
	
	public static final String SOURCE_CATEGORY_EXECUTION = "Execution";
	public static final String SOURCE_CATEGORY_COMPONENT = "Component";
	public static final String SOURCE_CATEGORY_PLAN	   = "Plan";
	public static final String SOURCE_CATEGORY_GOAL	   = "Goal";
	public static final String SOURCE_CATEGORY_FACT	   = "Fact";
	public static final String SOURCE_CATEGORY_MESSAGE   = "Message";
	public static final String SOURCE_CATEGORY_IEVENT	   = "Internal Event";
	public static final String SOURCE_CATEGORY_ACTIVITY	   = "Activity";
	
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
	 *  Returns creation time of the component that generated the event.
	 *  @return Parent ID.
	 */
	public long getComponentCreationTime();
	
	/**
	 *  Returns the parent of the source that generated the event, if any.
	 *  @return Parent ID.
	 */
	public String getParent();
	
	/**
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason();
	
	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public Object getDetails();
	
	/**
	 *  Get the bulk events.
	 *  @return The bulk events.
	 */
	public IComponentChangeEvent[] getBulkEvents();
}
