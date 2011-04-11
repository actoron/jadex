package jadex.base.service.awareness;

import jadex.bridge.IComponentIdentifier;

/**
 *  Simple info object that is sent between awareness agents.
 */
public class AwarenessInfo
{
	//-------- constants --------
	
	/** State indicating that a component is currently online. */
	public static final String	STATE_ONLINE	= "online";
	
	/** State indicating that a component is going offline. */
	public static final String	STATE_OFFLINE	= "offline";
	
	//-------- attributes --------
	
	/** The sending component's identifier. */
	protected IComponentIdentifier	sender;

	/** The component state. */
	protected String	state;
	
	/** The current send time delay (interval). */
	protected long	delay;
	
	//-------- constructors --------
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo()
	{
	}
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo(IComponentIdentifier sender, String state, long delay)
	{
		this.sender = sender;
		this.state = state;
		this.delay = delay;
	}
	
	//-------- methods --------

	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public IComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(IComponentIdentifier sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public String	getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AwarenessInfo(sender="+sender+", state="+state+", delay="+delay+")";
	}
}
