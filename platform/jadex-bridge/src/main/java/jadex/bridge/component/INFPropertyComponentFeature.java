package jadex.bridge.component;

import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFPropertyProvider;
import jadex.bridge.service.IServiceIdentifier;

/**
 *  Feature for non-functional properties.
 */
public interface INFPropertyComponentFeature
{
	/**
	 *  Get the component property provider.
	 */
	public INFPropertyProvider getComponentPropertyProvider();
	
	/**
	 *  Get the provided service property provider for a service.
	 */
	public INFMixedPropertyProvider getProvidedServicePropertyProvider(IServiceIdentifier sid);
	
	/**
	 *  Get the required service property provider for a service.
	 */
	public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid);

	/**
	 *  Has the service a property provider.
	 */
	public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid);
	
//	/**
//	 *  Get the provided service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getProvidedServicePropertyProvider(Class<?> iface);
	
//	/**
//	 *  Get the required service property provider for a service.
//	 */
//	public INFMixedPropertyProvider getRequiredServicePropertyProvider(String name);
}
