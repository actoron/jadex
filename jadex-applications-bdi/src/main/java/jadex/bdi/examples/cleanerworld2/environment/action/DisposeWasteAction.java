package jadex.bdi.examples.cleanerworld2.environment.action;

import java.util.List;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public class DisposeWasteAction implements ISimAction
{
	public static final String DEFAULT_NAME = "dispose_waste";
	
	/** Name of the action.
	 */
	private String name_;
	
	public DisposeWasteAction()
	{
		name_ = DEFAULT_NAME;
	}
	
	public boolean perform(SimObject actor, SimObject object, List parameters, ISimulationEngine engine)
	{
		if ((actor.getType() == "cleaner") &&
			(object != null) &&
			(object.getType() == "waste_bin") &&
			(actor.getPosition().getDistance(object.getPosition()).less(Configuration.REACH_DISTANCE)))
		{
			return true;
		}
		return false;
	}
	
	public String getName()
	{
		return name_;
	}
}
