package jadex.bridge.service.types.cms;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.future.IFuture;

/**
 *  Interface for bootstrap component factories, i.e.
 *  factories that are used at startup time of the platform.
 */
public interface IBootstrapFactory extends IComponentFactory
{
	/**
	 *  Start the service. Is called via the component
	 *  management service startup. Allows to initialize the
	 *  service with a valid service provider.
	 *  @param component The component.
	 *  @param rid The resource identifier.
	 */
	public IFuture<Void> startService(IInternalAccess component, IResourceIdentifier rid);
}
