package jadex.bdi.examples.disastermanagement;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;

/**
 *  Clear chemicals service interface.
 */
public interface IClearChemicalsService extends IService
{	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture clearChemicals(ISpaceObject disaster);

	/**
	 *  Abort clearing chemicals.
	 *  @return Future, null when done.
	 */
	public IFuture abort();
}
