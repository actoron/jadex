package jadex.base.service.awareness.discovery;

import jadex.base.service.awareness.AwarenessInfo;

/**
 * 
 */
public class MasterInfo
{
	/** The awareness info. */
	protected AwarenessInfo info;
	
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
