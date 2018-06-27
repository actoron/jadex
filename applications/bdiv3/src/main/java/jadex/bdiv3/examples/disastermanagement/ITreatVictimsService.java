package jadex.bdiv3.examples.disastermanagement;

import jadex.commons.future.ITerminableFuture;

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
	public ITerminableFuture<Void> treatVictims(Object disasterid);
}

