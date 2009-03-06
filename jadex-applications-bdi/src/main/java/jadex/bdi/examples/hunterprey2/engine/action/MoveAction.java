package jadex.bdi.examples.hunterprey2.engine.action;

import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.examples.hunterprey2.environment.Environment;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.grid.IGridSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoDownTask;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoLeftTask;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoRightTask;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoToDirectionTask;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoUpTask;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

import java.util.List;

public class MoveAction implements ISimAction
{
	public static final String DEFAULT_NAME = "move_to_direction";
	
	
	
	/** Name of the action.
	 */
	private String name_;
	
	public MoveAction()
	{
		name_ = DEFAULT_NAME;
	}
	
	public boolean perform(SimObject actor, SimObject object, List parameters, ISimulationEngine engine)
	{
		
		if (actor != null && parameters != null && parameters.size() == 1 && engine instanceof IGridSimulationEngine){
			try
			{
				IGridSimulationEngine gridengine = (IGridSimulationEngine) engine;
				IVector2 direction = (IVector2) parameters.get(0);
				IVector2 destinationPosition = gridengine
						.getSimulationObjectGridPosition(actor.getId()).copy()
						.add(direction);
				if ((
						Environment.OBJECT_TYPE_PREY.equals(actor.getType()) || 
						Environment.OBJECT_TYPE_HUNTER.equals(actor.getType())
					)
					&& 
					gridengine.getSimulationObjectsByGridPosition(destinationPosition,
								Environment.OBJECT_TYPE_OBSTACLE).length == 0)
				{
					actor.removeTask(GoToDirectionTask.DEFAULT_NAME);
					ISimObjectTask task = null;
					
					if (GoToDirectionTask.DIRECTION_UP.equals(direction))
					{
						task = new GoUpTask(
								Creature.CREATURE_SPEED.copy(), 
								gridengine.getAreaSize().copy(), 
								gridengine.getWorldBehavior());
					}
					else if (GoToDirectionTask.DIRECTION_DOWN.equals(direction))
					{
						task = new GoDownTask(
								Creature.CREATURE_SPEED.copy(), 
								gridengine.getAreaSize().copy(), 
								gridengine.getWorldBehavior());
					}
					else if (GoToDirectionTask.DIRECTION_RIGHT.equals(direction))
					{
						task = new GoRightTask(
								Creature.CREATURE_SPEED.copy(), 
								gridengine.getAreaSize().copy(),
								gridengine.getWorldBehavior());
					}
					else if (GoToDirectionTask.DIRECTION_LEFT.equals(direction))
					{
						task = new GoLeftTask(
								Creature.CREATURE_SPEED.copy(), 
								gridengine.getAreaSize().copy(), 
								gridengine.getWorldBehavior());
					}
					
					if (task != null)
					{
						actor.addTask(task);
						WorldObject wo = (WorldObject) actor.getProperty(Environment.PROPERTY_ONTOLOGY);
						IVector2 tPosition = GoToDirectionTask.createTargetPosition(
								actor.getPosition().copy(), 
								direction, 
								gridengine.getAreaSize(), 
								gridengine.getWorldBehavior());
						wo.setLocation(new Location(tPosition.getXAsInteger(), tPosition.getYAsInteger()));
						return true;
					}
				}
			}
			catch (ClassCastException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return false;
		
	}
	
	
	public String getName()
	{
		return name_;
	}
	
	
}
