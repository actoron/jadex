package jadex.bdi.examples.hunterprey2.engine.action;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.hunterprey2.environment.Environment;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Long;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.grid.IGridSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

import java.util.List;

public class EatAction implements ISimAction
{
	public static final String DEFAULT_NAME = "eat_action";
	
	/** Name of the action.
	 */
	private String name_;
	
	public EatAction()
	{
		name_ = DEFAULT_NAME;
	}
	
	public boolean perform(SimObject actor, SimObject object, List parameters, ISimulationEngine engine)
	{
		if (engine instanceof IGridSimulationEngine)
		{
			IGridSimulationEngine gridengine = (IGridSimulationEngine) engine;
			
			if (
					(Environment.OBJECT_TYPE_PREY.equals(actor.getType()) &&
							(object != null) &&
							Environment.OBJECT_TYPE_FOOD.equals(object.getType()) &&
							gridengine.getSimulationObjectGridPosition(actor.getId()).equals(
									gridengine.getSimulationObjectGridPosition(object.getId())))
					||
					(Environment.OBJECT_TYPE_HUNTER.equals(actor.getType()) &&
							(object != null) &&
							Environment.OBJECT_TYPE_PREY.equals(object.getType()) &&
							gridengine.getSimulationObjectGridPosition(actor.getId()).equals(
									gridengine.getSimulationObjectGridPosition(object.getId())))
				)
			{
				engine.destroySimObject(object.getId());
				return true;
			}
			
		}
		return false;
	}
	
	public String getName()
	{
		return name_;
	}
}
