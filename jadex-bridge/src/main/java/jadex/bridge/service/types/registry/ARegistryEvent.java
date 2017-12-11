package jadex.bridge.service.types.registry;

/**
 * 
 */
public abstract class ARegistryEvent
{	
	public static final String CLIENTTYPE_CLIENT = "client";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL0 = "superpeer_0";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL1 = "superpeer_1";
	
	/** The number of events that must have occured before a remote message is sent. */
	protected int eventslimit;
	
	/** The timestamp of the first event (change). */
	protected long timestamp; 
	
	/** The time limit. */
	protected long timelimit;
	
	/**
	 *  Create a new event.
	 *  @param eventslimit
	 *  @param timestamp
	 *  @param timelimit
	 */
	public ARegistryEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 *  @param eventslimit
	 *  @param timestamp
	 *  @param timelimit
	 */
	public ARegistryEvent(int eventslimit, long timelimit)
	{
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
	}

	/**
	 * Returns the number of elements added to this event.
	 */
	public abstract int size();
	
	/**
	 *  Check if this event is due and should be sent.
	 *  @param True, if the event is due and should be sent.
	 */
	public boolean isDue()
	{
		int size = size();
		// Send event if more than eventlimit events have been collected
		// OR
		// timeout has been reached (AND and at least one event has been collected)
		// The last aspect is not used because lease times are used
		// so an empty event at least renews the lease
		return size>=getEventslimit() || (System.currentTimeMillis()-getTimestamp()>=getTimelimit());// && size>0);
	}

	/**
	 *  Get the time until this event is due.
	 *  @return The time until the event is due.
	 */
	public long getTimeUntilDue()
	{
		long wait = timelimit-(System.currentTimeMillis()-timestamp);
		return wait>0? wait: 0;
	}

	/**
	 *  Get the eventslimit.
	 *  @return The eventslimit
	 */
	public int getEventslimit()
	{
		return eventslimit;
	}

	/**
	 *  Get the timestamp.
	 *  @return The timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 *  Get the timelimit.
	 *  @return The timelimit
	 */
	public long getTimelimit()
	{
		return timelimit;
	}
	
}
