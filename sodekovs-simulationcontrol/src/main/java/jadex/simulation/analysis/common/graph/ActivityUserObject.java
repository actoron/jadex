package jadex.simulation.analysis.common.graph;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;

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
