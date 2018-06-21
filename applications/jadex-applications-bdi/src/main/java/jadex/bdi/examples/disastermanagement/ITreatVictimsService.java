package jadex.bdi.examples.disastermanagement;

import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Interface for treat victim service.
 */
public interface ITreatVictimsService
{
	/**
	 *  Treat victims.
	 *  @param disasterId The disaster id.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> treatVictims(Object disasterId);
}

