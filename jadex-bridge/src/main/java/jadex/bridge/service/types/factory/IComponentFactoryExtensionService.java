package jadex.bridge.service.types.factory;

import jadex.commons.future.IFuture;

import java.util.Set;


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
	 *  @return A set of jadex.xml.TypeInfo objects. 
	 */
	public IFuture<Set<Object>>	getExtension(String componenttype); // <Set<TypeInfo>> -> no what about annotation based extensions
	
}
