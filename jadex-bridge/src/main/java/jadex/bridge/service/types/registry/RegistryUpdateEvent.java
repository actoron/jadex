package jadex.bridge.service.types.registry;

/**
 *  Event from super-peers to an update request.
 *  Indicates whether the client was removed or was  
 */
public class RegistryUpdateEvent
{
	/** Removed flag. */
	protected boolean removed;
	
	/** The super-peer lease time. After the lease time the client will be removed. */
	protected long leasetime;
	
	/**
	 *  Ask if the client was removed from the server.
	 *  Should send a full update as next event.
	 */
	public boolean isRemoved()
	{
		return removed;
	}

	/**
	 *  Set the removed.
	 *  @param removed The removed to set
	 */
	public void setRemoved(boolean removed)
	{
		this.removed = removed;
	}

	/**
	 *  Get the leasetime.
	 *  @return the leasetime
	 */
	public long getLeasetime()
	{
		return leasetime;
	}

	/**
	 *  Set the leasetime.
	 *  @param leasetime The leasetime to set
	 */
	public void setLeasetime(long leasetime)
	{
		this.leasetime = leasetime;
	}
}
