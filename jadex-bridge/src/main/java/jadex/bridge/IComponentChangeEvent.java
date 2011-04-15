package jadex.bridge;

public interface IComponentChangeEvent
{
	public static final String EVENT_TYPE_CREATION		= "Created";
	public static final String EVENT_TYPE_DISPOSAL		= "Disposed";
	public static final String EVENT_TYPE_MODIFICATION 	= "Modified";
	public static final String EVENT_TYPE_OCCURRENCE	= "Occured";
	
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
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason();
}
