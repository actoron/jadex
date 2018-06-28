package jadex.bdiv3.examples.marsworld.carry;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 * 
 */
public interface ICarryService 
{
	/**
	 * 
	 */
	public IFuture<Void> doCarry(@Reference ISpaceObject target);
}
