package jadex.bridge;


public class ComponentChangeEvent implements IComponentChangeEvent
{
	/** The time of the event. */
	protected long time;

	/** Type of event (e.g. creation, disposal). */
	protected String eventtype;

	/** The category of the source (e.g. goal, step, component) */
	protected String sourcecategory;

	/** The type of the source, i.e. model element name (moveto goal) */
	protected String sourcetype;
	
	/** The name of the source, i.e. instance name (e.g. goal13). */
	protected String sourcename;
	
	/** Component which generated the event. */
	protected IComponentIdentifier component;
	
	/** Parent of the component which generated the event. */
	protected IComponentIdentifier parent;
	
	/** Reason for the event, if any (e.g. goal succeeded). */
	protected String reason;
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent(String eventtype, String sourcecategory, String sourcetype, 
		String sourcename, IComponentIdentifier cid)
	{
		this(eventtype, sourcecategory, sourcetype, sourcename, cid, null, 0);
	}
	
	/**
	 *  Create a new event.
	 */
	public ComponentChangeEvent(String eventtype, String sourcecategory, String sourcetype, 
		String sourcename, IComponentIdentifier cid, String reason, long time)
	{
		this.eventtype = eventtype;
		this.time = time;
		this.sourcename = sourcename;
		this.sourcetype = sourcetype;
		this.sourcecategory = sourcecategory;
		this.component = cid;
		this.reason = reason;
	}

	/**
	 *  Returns the type of the event.
	 *  @return The type of the event.
	 */
	public String getEventType()
	{
		return eventtype;
	}
	
	/**
	 *  Returns the time when the event occured.
	 *  @return Time of event.
	 */
	public long getTime()
	{
		return time;
	}
	
	/**
	 *  Returns the name of the source that caused the event.
	 *  @return Name of the source.
	 */
	public String getSourceName()
	{
		return sourcename;
	}
	
	/**
	 *  Returns the type of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceType()
	{
		return sourcetype;
	}
	
	/**
	 *  Returns the category of the source that caused the event.
	 *  @return Type of the source.
	 */
	public String getSourceCategory()
	{
		return sourcecategory;
	}
	
	/**
	 *  Returns the component that generated the event.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getComponent()
	{
		return component;
	}
	
	/**
	 *  Returns the parent component of the component that generated the event, if any.
	 *  @return Component ID.
	 */
	public IComponentIdentifier getParent()
	{
		return parent;
	}
	
	/**
	 *  Returns a reason why the event occured.
	 *  @return Reason why the event occured, may be null.
	 */
	public String getReason()
	{
		return reason;
	}
	
	//================== Setters ===================

	/**
	 *  Sets the type of the event.
	 *  @param type The type of the event.
	 */
	public void setEventType(String type)
	{
		eventtype = type;
	}
	
	/**
	 *  Sets the time when the event occured.
	 *  @param time Time of event.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}
	
	/**
	 *  Sets the name of the source that caused the event.
	 *  @param name Name of the source.
	 */
	public void setSourceName(String name)
	{
		sourcename = name;
	}
	
	/**
	 *  Sets the type of the source that caused the event.
	 *  @param type Type of the source.
	 */
	public void setSourceType(String type)
	{
		sourcetype = type;
	}
	
	/**
	 *  Sets the category of the source that caused the event.
	 *  @param category Category of the source.
	 */
	public void setSourceCategory(String category)
	{
		sourcecategory = category;
	}
	
	/**
	 *  Sets the component that generated the event.
	 *  @param id Component ID.
	 */
	public void setComponent(IComponentIdentifier id)
	{
		component = id;
	}
	
	/**
	 *  Sets the parent of the component that generated the event.
	 *  @param id Component ID.
	 */
	public void setParent(IComponentIdentifier id)
	{
		parent = id;
	}
	
	/**
	 *  Sets a reason why the event occured.
	 *  @param reason Reason why the event occured, may be null.
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder(getComponent()!=null? getComponent().getName(): "unknown");
		sb.append(" ");
		if(getEventType() != null)
		{
			sb.append(getEventType());
			sb.append(" ");
		}
		if(getSourceCategory() != null)
		{
			sb.append(getSourceCategory());
			sb.append(" ");
		}
		sb.append("(");
		sb.append(getTime());
		sb.append("): ");
		if(getSourceName() != null)
		{
			sb.append("Instance: ");
			sb.append(getSourceName());
			sb.append(", ");
		}
		
		sb.append("Type: ");
		sb.append(getSourceType());
		
		if(getReason() != null)
		{
			sb.append(", Reason: ");
			sb.append(getReason());
		}
		
		return sb.toString();
	}
}
