package jadex.extension.envsupport;

import jadex.bridge.IComponentFactoryExtensionService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Extension service for loading env support models.
 */
public class EnvSupportExtensionService implements IComponentFactoryExtensionService
{
	/**
	 *  Get extension. 
	 */
	public IFuture getExtension(String componenttype)
	{
		return new Future(MEnvSpaceType.getXMLMapping());
	}
}
