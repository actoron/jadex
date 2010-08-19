package jadex.base.service.awareness;

import jadex.bridge.IComponentIdentifier;

/**
 *  Simple info object that is sent between awareness agents.
 */
public class AwarenessInfo
{
	//-------- attributes --------
	
	/** The sending component's identifier. */
	protected IComponentIdentifier sender;

	/** Time sending timestamp. */
	protected long sendtime;
	
	/** The current send time delay (interval). */
	protected long delay;
	
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
	public AwarenessInfo(IComponentIdentifier sender, long sendtime, long delay)
	{
		this.sender = sender;
		this.sendtime = sendtime;
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
	 *  Get the sendtime.
	 *  @return the sendtime.
	 */
	public long getSendTime()
	{
		return sendtime;
	}

	/**
	 *  Set the sendtime.
	 *  @param sendtime The sendtime to set.
	 */
	public void setSendTime(long sendtime)
	{
		this.sendtime = sendtime;
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
		return "AwarenessInfo(sender="+sender+", sendtime="+sendtime+", delay="+delay+")";
	}
}
