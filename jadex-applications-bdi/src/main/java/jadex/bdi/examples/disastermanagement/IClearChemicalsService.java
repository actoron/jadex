package jadex.bdi.examples.disastermanagement;

import jadex.commons.future.ITerminableFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Clear chemicals service interface.
 */
public interface IClearChemicalsService
{	
	/**
	 *  Clear chemicals.
	 *  @param disasterId The disaster id.
	 *  @return Future, null when done.
	 */
//	public ITerminableFuture<Void> clearChemicals(ISpaceObject disaster);
	public ITerminableFuture<Void> clearChemicals(Object disasterId);
}
