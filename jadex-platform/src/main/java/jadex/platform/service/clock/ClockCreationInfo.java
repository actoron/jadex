package jadex.platform.service.clock;

/**
 *  Clock creation info.
 */
public class ClockCreationInfo
{
	//-------- attributes --------
	
	/** The clock type. */
	protected String clocktype;
	
	/** The clock name. */
	protected String name;
	
	/** The start time. */
	protected long start;
	
	/** The delta. */
	protected long delta;
	
	/** The dilation. */
	protected double dilation;
	
	//-------- constructors --------

	/**
	 *  Create a new clock info.
	 */
	public ClockCreationInfo(String clocktype, String name)
	{
		this(clocktype, name, 0, 0);
	}
	
	/**
	 *  Create a new clock info.
	 */
	public ClockCreationInfo(String clocktype, String name, long start, long delta)
	{
		this(clocktype, name, start, delta, 1);
	}
	
	/**
	 *  Create a new clock info.
	 */
	public ClockCreationInfo(String clocktype, String name, long start, long delta, double dilation)
	{
		this.clocktype = clocktype;
		this.name = name;
		this.start = start;
		this.delta = delta;
		this.dilation = dilation;
	}

	//-------- methods --------

	/**
	 *  Get the clocktype.
	 *  @return the clocktype.
	 */
	public String getClockType()
	{
		return clocktype;
	}

	/**
	 *  Set the clocktype.
	 *  @param clocktype The clocktype to set.
	 */
	public void setClockType(String clocktype)
	{
		this.clocktype = clocktype;
	}

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the start.
	 *  @return the start.
	 */
	public long getStart()
	{
		return start;
	}

	/**
	 *  Set the start.
	 *  @param start The start to set.
	 */
	public void setStart(long start)
	{
		this.start = start;
	}

	/**
	 *  Get the delta.
	 *  @return the delta.
	 */
	public long getDelta()
	{
		return delta;
	}

	/**
	 *  Set the delta.
	 *  @param delta The delta to set.
	 */
	public void setDelta(long delta)
	{
		this.delta = delta;
	}

	/**
	 *  Get the dilation.
	 *  @return the dilation.
	 */
	public double getDilation()
	{
		return dilation;
	}

	/**
	 *  Set the dilation.
	 *  @param dilation The dilation to set.
	 */
	public void setDilation(double dilation)
	{
		this.dilation = dilation;
	}
	
	
}
