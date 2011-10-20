package jadex.bridge.service.types.factory;

import jadex.commons.future.IFuture;

/**
 *  Extension mechanism for kernel factories.
 *  The extension delivered here should enable
 *  a factory to load a kernel extension.
 *  
 *  Extensions have to implement
 *  - IExtensionType for model
 *  - IExtensionInstance
 */
public interface IComponentFactoryExtensionService
{
	/**
	 *  Get extension. 
	 */
	public IFuture getExtension(String componenttype);
	
}
