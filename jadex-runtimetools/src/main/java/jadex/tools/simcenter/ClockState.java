package jadex.tools.simcenter;

import jadex.commons.transformation.annotations.IncludeFields;

/**
 *  Information about the clock to be transferred.
 */
@IncludeFields
public class ClockState
{
	//-------- attributes --------
	
	/** The clock type. */
	public String	type;
	
	/** The current time. */
	public long	time;
	
	/** The current tick. */
	public double	tick;
	
	/** The start time. */
	public long	starttime;
	
	/** The clock delta. */
	public long	delta;
	
	/** The clock dilation. */
	public double	dilation;
	
	/** Changing clock type allowed? */
	public boolean	changeallowed;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ClockState()
	{
	}
	
	/**
	 *  Create a clock state object.
	 */
	public ClockState(String type, long time, double tick, long starttime, long delta, double dilation, boolean changeallowed)
	{
		this.type	= type;
		this.time	= time;
		this.tick	= tick;
		this.starttime	= starttime;
		this.delta	= delta;
		this.dilation	= dilation;
		this.changeallowed	= changeallowed;
	}
	
	//-------- methods --------
	
	/**
	 *  The hash code.
	 *  Overridden to have only one clock state per update.
	 */
	public int hashCode()
	{
		return 123;
	}
	
	/**
	 *  Test if two objects are equal.
	 *  Overridden to have only one clock state per update.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ClockState;
	}
}