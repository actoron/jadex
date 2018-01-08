package jadex.bridge.service.types.registry;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class ARegistryResponseEvent
{
	/** The target (the sender of the original event). Can be delivered over different levels. */
	protected IComponentIdentifier receiver;
	
	/**
	 *  Get the receiver.
	 *  @return The receiver
	 */
	public IComponentIdentifier getReceiver()
	{
		return receiver;
	}

	/**
	 *  Set the receiver.
	 *  @param receiver the receiver to set
	 */
	public void setReceiver(IComponentIdentifier receiver)
	{
		this.receiver = receiver;
	}
}
