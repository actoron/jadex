package jadex.bdiv3.examples.disastermanagement;

import jadex.commons.future.ITerminableFuture;

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
	public ITerminableFuture<Void> extinguishFire(Object disasterid);
}
