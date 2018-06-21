package jadex.bridge.service.types.registry;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class ARegistryResponseEvent
{
	/** The target (the sender of the original event). Can be delivered over different levels. */
	protected IComponentIdentifier receiver;
	
	/** Unknown flag. */
	protected boolean unknown;
	
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
	
	/**
	 *  Ask if the client was removed from the server.
	 *  Should send a full update as next event.
	 */
	public boolean isUnknown()
	{
		return unknown;
	}

	/**
	 *  Set the unknown.
	 *  @param unknown The removed to set
	 */
	public void setUnknown(boolean unknown)
	{
		this.unknown = unknown;
	}
}
