package jadex.wfms.simulation.stateset;

import jadex.commons.collection.IndexMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
		incrementActRec(new ArrayList(activityControllers.getAsList()));
		/*for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
		}*/
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
	
	public Map getActivityState(String activityName, Map parameterValues)
	{
		ActivityStateController controller = (ActivityStateController) activityControllers.get(activityName);
		if (controller == null)
			return null;
		return controller.getActivityState(parameterValues);
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer("ProcessState [");
		for (Iterator it = activityControllers.iterator(); it.hasNext(); )
		{
			ActivityStateController controller = (ActivityStateController) it.next();
			buffer.append(controller.toString());
			buffer.append(", ");
		}
		buffer.setLength(buffer.length() - 2);
		buffer.append("]");
		return buffer.toString();
	}
	
	private void incrementActRec(List controllerList)
	{
		ActivityStateController controller = ((ActivityStateController) controllerList.get(0));
		if (controller.finalState())
		{
			if (controllerList.size() == 1)
				return;
			controller.reset();
			incrementActRec(controllerList.subList(1, controllerList.size()));
		}
		else
		{
			controller.nextState();
		}
	}
}
