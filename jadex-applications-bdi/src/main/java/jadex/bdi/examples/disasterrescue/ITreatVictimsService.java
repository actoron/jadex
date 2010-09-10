package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.commons.IFuture;

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
}

