package jadex.application.space.agr;

import jadex.bridge.IComponentFactoryExtensionService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Extension service for loading agr models.
 */
public class AGRExtensionService implements IComponentFactoryExtensionService
{
	/**
	 *  Get extension. 
	 */
	public IFuture getExtension(String componenttype)
	{
		return new Future(MAGRSpaceType.getXMLMapping());
	}
}