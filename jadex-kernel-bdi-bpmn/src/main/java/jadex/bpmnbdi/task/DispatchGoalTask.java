package jadex.bpmnbdi.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bpmnbdi.BpmnPlanBodyInstance;
import jadex.commons.concurrent.IResultListener;

import java.util.Iterator;
import java.util.Map;

/**
 *  Dispatch a goal and by default wait for the result.
 */
public class DispatchGoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(final ITaskContext context, BpmnInterpreter instance, final IResultListener listener)
	{
		try
		{
			BpmnPlanBodyInstance	plan	= (BpmnPlanBodyInstance)instance;
			String type = (String)context.getParameterValue("type");
			if(type==null)
				throw new RuntimeException("Parameter 'type' for goal not specified: "+instance);
			Map params = context.hasParameterValue("parameters")
				? (Map)context.getParameterValue("parameters") : null;
			boolean	subgoal	= context.hasParameterValue("subgoal")
				? ((Boolean)context.getParameterValue("subgoal")).booleanValue() : true;
			boolean	wait	= context.hasParameterValue("wait")
				? ((Boolean)context.getParameterValue("wait")).booleanValue() : true;

//			System.out.println("Create goal task: "+type+" "+params);
				
			final IGoal	goal	= plan.createGoal(type);
			if(params!=null)
			{
				for(Iterator it=params.keySet().iterator(); it.hasNext(); )
				{
					String	param	= (String) it.next();
					goal.getParameter(param).setValue(params.get(param));
				}
			}
			
			if(context.getModelElement().hasParameter("goal"))
				context.setParameterValue("goal", goal);
			
			if(wait)
			{
				goal.addGoalListener(new IGoalListener()
				{
					public void goalFinished(AgentEvent ae)
					{
						goal.removeGoalListener(this);
						if(goal.isSucceeded())
						{
							listener.resultAvailable(DispatchGoalTask.this, null);
						}
						else
						{
							Exception	e	= new GoalFailureException();
							e.fillInStackTrace();
							listener.exceptionOccurred(DispatchGoalTask.this, e);
						}
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
			}
			
			if(subgoal)
				plan.dispatchSubgoal(goal);
			else
				plan.dispatchTopLevelGoal(goal);
			
			if(!wait)
				listener.resultAvailable(this, null);
		}
		catch(Exception e)
		{
			listener.exceptionOccurred(this, e);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The dispatch goal task can be used for dipatching a goal as top-level " +
			" or subgoal and optinally wait for the result.";
		
		ParameterMetaInfo typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "type", null, "The type parameter identifies the user goal type.");
		ParameterMetaInfo paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the goal parameters.");
		ParameterMetaInfo subgoal = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "subgoal", null, "The subgoal parameter for dispatching as top-level or subgoal.");
		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter to wait for the results.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, paramsmi, subgoal, waitmi}); 
	}
}
