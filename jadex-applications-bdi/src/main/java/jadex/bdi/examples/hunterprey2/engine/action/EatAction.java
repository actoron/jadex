package jadex.bdi.examples.hunterprey2.engine.action;

import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.examples.hunterprey2.environment.Environment;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.grid.IGridSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

import java.util.List;

public class EatAction implements ISimAction
{
	public static final String DEFAULT_NAME = "eat_action";
	
	// TODO: Move to Environment?
	public static final int POINTS_FOOD = 1;
	public static final int POINTS_PREY = 5;
	
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
			boolean isEatAllowed = false;
			Creature me = null;
			int points = 0;

			if (
					(Environment.SIM_OBJECT_TYPE_PREY.equals(actor.getType()) &&
							(object != null) &&
							Environment.SIM_OBJECT_TYPE_FOOD.equals(object.getType()) &&
							gridengine.getSimulationObjectGridPosition(actor.getId()).equals(
									gridengine.getSimulationObjectGridPosition(object.getId())))
				)
			{
				isEatAllowed = true;
				points = POINTS_FOOD;
			}
			else if (
					(Environment.SIM_OBJECT_TYPE_HUNTER.equals(actor.getType()) &&
							(object != null) &&
							Environment.SIM_OBJECT_TYPE_PREY.equals(object.getType()) &&
							gridengine.getSimulationObjectGridPosition(actor.getId()).equals(
									gridengine.getSimulationObjectGridPosition(object.getId())))
				)
			{
				isEatAllowed = true;
				points = POINTS_PREY;
			}
			
			if (isEatAllowed)
			{
				engine.destroySimObject(object.getId());
				WorldObject eaten = (WorldObject) object.getProperty(Environment.SIM_OBJECT_PROPERTY_ONTOLOGY);
				eaten.setSimId(null);
				me = (Creature) actor.getProperty(Environment.SIM_OBJECT_PROPERTY_ONTOLOGY);
				me.setPoints(me.getPoints()+points);
				return true;
			}
			else
			{
				System.out.println("Creature tried to cheat: " + actor.getProperty(Environment.SIM_OBJECT_PROPERTY_ONTOLOGY));
			}
		}
		
		
		return false;
	}
	
	public String getName()
	{
		return name_;
	}
}
