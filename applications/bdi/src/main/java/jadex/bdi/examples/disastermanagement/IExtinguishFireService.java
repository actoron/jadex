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
	 *  @param disasterId The disaster id.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> extinguishFire(Object disasterId);
}
