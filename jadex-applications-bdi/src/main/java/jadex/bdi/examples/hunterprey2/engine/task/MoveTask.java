package jadex.bdi.examples.hunterprey2.engine.task;

import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.GoToPreciseDestinationTask;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

/**
 * @deprecated
 */
public class MoveTask extends GoToPreciseDestinationTask
{
	// ------- constants -------
	
	public static final String MOVE_TASK_FINISHED = MoveTask.class.getName() + ".MOVE_TASK_FINISHED";
	
	// ------- attributes ------
	
	/** The Creature to move with this task */
	protected Creature me;
	
	/** The start position of the creature */
	protected Location startPosition;
	
	/** The end position of the creature */
	protected Location destPosition;
	
	/**
	 * Create a new creature MoveTask
	 * @param creature to move
	 * @param targetPosition to move to
	 * @param speed to move the creature (distance/second)
	 */
	public MoveTask(Creature me, Location targetLocation, IVector1 speed)
	{
		super(targetLocation.getAsIVector2(), speed);
		this.me = me;
		this.startPosition = me.getLocation();
		this.destPosition = targetLocation;
	}

	/** Get the creature to move with this task */
	public Creature getCreature()
	{
		return me;
	}

	/** Get the start location of this move task */
	public Location getStartLocation()
	{
		return startPosition;
	}

	/** Get the destination location of this move task */
	public Location getDestLocation()
	{
		return destPosition;
	}
	
	// --- overide methods ---
	
	/** 
	 * @see ISimObjectTask#shutdown(SimObject)
	 */
	public void shutdown(SimObject object)
	{
		synchronized (object)
		{
			IVector2 currentPosition = object.getPositionAccess();
			SimulationEvent evt = new SimulationEvent(MoveTask.MOVE_TASK_FINISHED);
			evt.setParameter("task", this);
			evt.setParameter("position", currentPosition.copy());
			object.fireSimulationEvent(evt);
		}
	}

	
	
}
