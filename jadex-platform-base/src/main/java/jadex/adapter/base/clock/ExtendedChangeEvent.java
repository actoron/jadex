package jadex.adapter.base.clock;

import javax.swing.event.ChangeEvent;

/**
 * 
 */
public class ExtendedChangeEvent extends ChangeEvent
{
	/** The type. */
	protected String type;
	
	/** The value. */
	protected Object value;
	
	/**
	 * 
	 */
	public ExtendedChangeEvent(Object source, String type)
	{
		this(source, type, null);
	}
	
	/**
	 * 
	 */
	public ExtendedChangeEvent(Object source, String type, Object value)
	{
		super(source);
		this.type = type;
		this.value = value;
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		return "(ClockEvent: type="+type+", value="+value+")";
	}
}
