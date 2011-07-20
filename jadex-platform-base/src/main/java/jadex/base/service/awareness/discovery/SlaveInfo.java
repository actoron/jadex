package jadex.base.service.awareness.discovery;

import jadex.base.service.awareness.AwarenessInfo;

/**
 *  For local master/slave protocol.
 *  Indicates awareness message from a slave.
 */
public class SlaveInfo
{
	//-------- attributes --------
	
	/** The awareness info. */
	protected AwarenessInfo info;
	
	//-------- constructors --------
	
	/**
	 *  Create a new slave info.
	 */
	public SlaveInfo()
	{
	}
	
	/**
	 *  Create a new slave info.
	 */
	public SlaveInfo(AwarenessInfo info)
	{
		this.info = info;
	}

	//-------- methods --------

	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public AwarenessInfo getAwarenessInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setAwarenessInfo(AwarenessInfo info)
	{
		this.info = info;
	}
}
