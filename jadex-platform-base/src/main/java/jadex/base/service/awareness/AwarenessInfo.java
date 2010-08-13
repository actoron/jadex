package jadex.base.service.awareness;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *
 */
public class AwarenessInfo
{
	/** The sending component's identifier. */
	protected IComponentIdentifier sender;

	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo()
	{
	}
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo(IComponentIdentifier sender)
	{
		this.sender = sender;
	}

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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AwarenessInfo(sender="+sender+"addresses: "
			+SUtil.arrayToString(sender.getAddresses())+")";
	}
	
}
