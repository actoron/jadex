package jadex.bdiv3.examples.marsworld.producer;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public interface IProduceService
{
	/**
	 * 
	 */
	public IFuture<Void> doProduce(@Reference ISpaceObject target);
}