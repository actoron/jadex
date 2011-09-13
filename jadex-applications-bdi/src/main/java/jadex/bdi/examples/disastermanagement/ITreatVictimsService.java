package jadex.bdi.examples.disastermanagement;

import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

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
	public IFuture<Void> treatVictims(ISpaceObject disaster);

	/**
	 *  Abort treating victims.
	 *  @return Future, null when done.
	 */
	public IFuture<Void> abort();
}

