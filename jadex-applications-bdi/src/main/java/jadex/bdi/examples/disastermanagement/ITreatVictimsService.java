package jadex.bdi.examples.disastermanagement;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.commons.IFuture;
import jadex.commons.service.IService;

/**
 *  Interface for treat victim service.
 */
public interface ITreatVictimsService  extends IService
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

