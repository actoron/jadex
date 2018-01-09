package jadex.bridge.service.types.registry;

/**
 *  Event from super-peers to an update request.
 *  Indicates whether the client was removed or was  
 */
public class RegistryResponseEvent extends ARegistryResponseEvent
{
	/** The super-peer lease time. After the lease time the client will be removed. */
	protected long leasetime;
	
	/** The suitable superpeer services. */
	protected ISuperpeerRegistrySynchronizationService[] superpeers;

	/**
	 *  Create a new RegistryUpdateEvent.
	 */
	public RegistryResponseEvent()
	{
	}
	
	/**
	 *  Create a new RegistryUpdateEvent.
	 */
	public RegistryResponseEvent(boolean removed, long leasetime)
	{
		this.unknown = removed;
		this.leasetime = leasetime;
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

	/**
	 *  Get the superpeers.
	 *  @return The superpeers.
	 */
	public ISuperpeerRegistrySynchronizationService[] getSuperpeers()
	{
		return superpeers;
	}

	/**
	 *  Set the superpeers.
	 *  @param superpeers The superpeers to set
	 */
	public void setSuperpeers(ISuperpeerRegistrySynchronizationService[] superpeers)
	{
		this.superpeers = superpeers;
	}
}
