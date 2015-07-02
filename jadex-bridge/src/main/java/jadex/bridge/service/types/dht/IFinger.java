package jadex.bridge.service.types.dht;

import jadex.bridge.service.IServiceIdentifier;

/**
 * Finger Table entry.
 */
public interface IFinger
{
	/**
	 * Get the start IID Key of this Finger.
	 */
	public IID getStart();

	/**
	 * Get the Node ID of this Finger.
	 */
	public IID getNodeId();

	/**
	 * Get the SID of this Finger.
	 */
	public IServiceIdentifier getSid();
}
