package jadex.bridge;

/**
 *  Type for component events.
 */
public interface IComponentChangeEvent
{
	/** Event denoting creation of an element. */
	public static final String EVENT_TYPE_CREATION		= "created";
	/** Event denoting disposal of an element. */
	public static final String EVENT_TYPE_DISPOSAL		= "disposed";
	/** Event denoting modification of an element. */
	public static final String EVENT_TYPE_MODIFICATION 	= "modified";
	/** Event denoting a single occurrence without temporal extension. */
	public static final String EVENT_TYPE_OCCURRENCE	= "noticed";
	/** Bulk event composed of sub events. */
	public static final String EVENT_TYPE_BULK = "bulk";
	
	/** Events regarding the execution of a step. */
	public static final String SOURCE_CATEGORY_EXECUTION = "Execution";
	/** Events regarding a component. */
	public static final String SOURCE_CATEGORY_COMPONENT = "Component";
	/** Events regarding a BDI plan. */
	public static final String SOURCE_CATEGORY_PLAN	   = "Plan";
	/** Events regarding a BDI goal. */
	public static final String SOURCE_CATEGORY_GOAL	   = "Goal";
	/** Events regarding a BDI fact. */
	public static final String SOURCE_CATEGORY_FACT	   = "Fact";
	/** Events regarding a BDI internal event. */
	public static final String SOURCE_CATEGORY_IEVENT	   = "Internal Event";
	/** Events regarding a message. */
	public static final String SOURCE_CATEGORY_MESSAGE   = "Message";
	/** Events regarding a BPMN activity. */
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
