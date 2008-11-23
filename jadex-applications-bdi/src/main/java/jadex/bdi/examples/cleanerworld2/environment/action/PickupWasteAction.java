package jadex.bdi.examples.cleanerworld2.environment.action;

import java.util.List;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Long;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public class PickupWasteAction implements ISimAction
{
	public static final String DEFAULT_NAME = "pickup_waste";
	
	/** Name of the action.
	 */
	private String name_;
	
	public PickupWasteAction()
	{
		name_ = DEFAULT_NAME;
	}
	
	public boolean perform(SimObject actor, SimObject object, List parameters, ISimulationEngine engine)
	{
		if ((actor.getType() == "cleaner") &&
			(((IVector1) actor.getProperty("waste_capacity")).greater(Vector1Long.ZERO)) &&
			(object != null) &&
			(object.getType() == "waste") &&
			(actor.getPosition().getDistance(object.getPosition()).less(Configuration.REACH_DISTANCE)))
		{
			engine.destroySimObject(object.getId());
			IVector1 wasteCapacity = (IVector1) actor.getProperty("waste_capacity");
			wasteCapacity.subtract(new Vector1Long(1));
			if (wasteCapacity.equals(Vector1Long.ZERO))
			{
				actor.fireSimulationEvent(new SimulationEvent("waste_container_full"));
			}
			return true;
		}
		return false;
	}
	
	public String getName()
	{
		return name_;
	}
}
