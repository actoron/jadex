package jadex.bdi.examples.disastermanagement;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Interface for treat victim service.
 */
public interface ITreatVictimsService
{
	/**
	 *  Treat victims.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture treatVictims(ISpaceObject disaster);

	/**
	 *  Abort treating victims.
	 *  @return Future, null when done.
	 */
	public IFuture abort();
}

