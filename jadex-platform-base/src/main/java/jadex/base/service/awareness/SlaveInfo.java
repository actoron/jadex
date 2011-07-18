package jadex.base.service.awareness;

/**
 * 
 */
public class SlaveInfo
{
	/** The awareness info. */
	protected AwarenessInfo info;
	
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
