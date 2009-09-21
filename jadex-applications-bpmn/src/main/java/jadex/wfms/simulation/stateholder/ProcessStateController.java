package jadex.wfms.simulation.stateholder;

import jadex.commons.collection.IndexMap;

import java.util.Iterator;
import java.util.Map;

public class ProcessStateController
{
	private IndexMap activityControllers;
	
	public ProcessStateController()
	{
		activityControllers = new IndexMap();
	}
	
	public void addActivityController(ActivityStateController activityController)
	{
		activityControllers.put(activityController.getActivityName(), activityController);
	}
	
	/**
	 * Resets the state controller to the first available state.
	 */
	public void reset()
	{
		for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
			controller.reset();
		}
	}
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState()
	{
		for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
			if (controller.finalState())
				controller.reset();
			else
			{
				controller.nextState();
				return;
			}
		}
	}
	
	/**
	 * Test if the controller is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState()
	{
		for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
			if (!controller.finalState())
				return false;
		}
		return true;
	}
	
	public long getStateCount()
	{
		if (activityControllers.size() == 0)
			return 0;
		long ret = 1;
		for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
			ret *= controller.getStateCount();
		}
		return ret;
	}
	
	public Map getActivityState(String activityName)
	{
		ActivityStateController controller = (ActivityStateController) activityControllers.get(activityName);
		return controller.getActivityState();
	}
}
