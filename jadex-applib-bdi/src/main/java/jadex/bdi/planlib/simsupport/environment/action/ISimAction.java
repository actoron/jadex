package jadex.bdi.planlib.simsupport.environment.action;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Action executeable by SimObjects.
 */
public interface ISimAction
{
	/** Performs the action.
	 * 
	 *  @param actor the object executing the action
	 *  @param object the object that is being acted upon (may be null)
	 *  @param engine the simulation engine
	 *  @return true if the action was successful, false otherwise
	 */
	public boolean perform(SimObject actor, SimObject object, ISimulationEngine engine);
	
	/** Returns the name of the action.
	 *  
	 *  @return name of the action
	 */
	public String getName();
}
