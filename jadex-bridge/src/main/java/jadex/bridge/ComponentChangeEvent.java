package jadex.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ComponentChangeEvent implements IComponentChangeEvent
{
	/** Type of event. */
	protected String eventtype;
	
	/** The time of the event. */
	protected long time;
	
	/** The name of the source. */
	protected String sourcename;
	
	/** The type of the source. */
	protected String sourcetype;
	
	/** The category of the source */
	protected String sourcecategory;
	
	/** Reason for the event, if any. */
	protected String reason;
	
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
	 *  Sets a reason why the event occured.
	 *  @param reason Reason why the event occured, may be null.
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (getEventType() != null)
		{
			sb.append(getEventType());
			sb.append(" ");
		}
		if (getSourceCategory() != null)
		{
			sb.append(getSourceCategory());
			sb.append(" ");
		}
		sb.append("(");
		sb.append(getTime());
		sb.append("): ");
		if (getSourceName() != null)
		{
			sb.append("Instance: ");
			sb.append(getSourceName());
			sb.append(", ");
		}
		
		sb.append("Type: ");
		sb.append(getSourceType());
		
		if (getReason() != null)
		{
			sb.append(", Reason: ");
			sb.append(getReason());
		}
		
		return sb.toString();
	}
}
