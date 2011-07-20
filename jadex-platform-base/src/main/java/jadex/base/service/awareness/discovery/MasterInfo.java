package jadex.base.service.awareness.discovery;

import jadex.base.service.awareness.AwarenessInfo;

/**
 *  For local master/slave protocol.
 *  Indicates awareness message from a master.
 */
public class MasterInfo
{
	//-------- attributes --------
	
	/** The awareness info. */
	protected AwarenessInfo info;
	
	//-------- constructors --------
	
	/**
	 *  Create a new master info.
	 */
	public MasterInfo()
	{
	}
	
	/**
	 *  Create a new master info.
	 */
	public MasterInfo(AwarenessInfo info)
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
