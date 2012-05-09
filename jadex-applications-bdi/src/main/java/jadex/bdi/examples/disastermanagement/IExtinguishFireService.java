package jadex.bdi.examples.disastermanagement;

import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Extinguish fire service interface.
 */
public interface IExtinguishFireService
{
	/**
	 *  Extinguish a fire.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> extinguishFire(ISpaceObject disaster);
}
