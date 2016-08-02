package jadex.bridge.service.types.registry;

/**
 *  Listener interface for local registry listeners.
 */
public interface IRegistryListener
{
	/**
	 *  Called when a service was added.
	 *  @param event The event..
	 */
	public void registryChanged(RegistryListenerEvent event);
}
