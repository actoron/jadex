package jadex.bdi.examples.disastermanagement;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.commons.IFuture;
import jadex.commons.service.IService;

/**
 *  Extinguish fire service interface.
 */
public interface IExtinguishFireService	extends	IService
{
	/**
	 *  Extinguish a fire.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture extinguishFire(ISpaceObject disaster);
	

	/**
	 *  Abort extinguishing fire.
	 *  @return Future, null when done.
	 */
	public IFuture abort();
}
