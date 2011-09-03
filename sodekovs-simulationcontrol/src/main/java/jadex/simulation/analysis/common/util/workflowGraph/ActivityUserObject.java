package jadex.simulation.analysis.common.util.workflowGraph;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;

/**
 * Analysis user object of the Jgraph
 * @author 5Haubeck
 *
 */
public class ActivityUserObject
{
	MActivity activity;
	public ActivityUserObject(MActivity activity)
	{
		this.activity = activity;
	}
	
	public MActivity getActivity()
	{
		return activity;
	}
	
	@Override
	public String toString()
	{
		if (activity.getActivityType().equals(MBpmnModel.TASK))
		{
			return activity.getName();
		} else
		{
			return "";
		}
		
	}
}
