package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.commons.IFuture;

/**
 *  Clear chemicals service interface.
 */
public interface IClearChemicalsService 
{	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture clearChemicals(ISpaceObject disaster);
}
