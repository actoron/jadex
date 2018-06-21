package jadex.bdibpmn.task;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Iterator;
import java.util.Map;

/**
 *  Dispatch a goal and by default wait for the result.
 */
@Task(description= "The dispatch goal task can be used for dipatching a goal as top-level " +
		" or subgoal and optinally wait for the result.",
parameters={@TaskParameter(name="type", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The type parameter identifies the user goal type."),
	@TaskParameter(name="parameters", clazz=Map.class, direction=TaskParameter.DIRECTION_IN,
		description="The 'parameter' parameter allows to specify the goal parameters."),
	@TaskParameter(name="subgoal", clazz=boolean.class, direction=TaskParameter.DIRECTION_IN,
		description="The subgoal parameter for dispatching as top-level or subgoal."),
	@TaskParameter(name="wait", clazz=boolean.class, direction=TaskParameter.DIRECTION_IN,
		description="The wait parameter to wait for the results.")}
)
public class DispatchGoalTask	implements ITask
{
	
	/** The goal. */
	protected IGoal	goal;
	
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
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
				
			goal = plan.createGoal(type);
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
							ret.setResult(null);
//							listener.resultAvailable(DispatchGoalTask.this, null);
						}
						else
						{
							Exception	e	= new GoalFailureException();
							e.fillInStackTrace();
							ret.setException(e);
//							listener.exceptionOccurred(DispatchGoalTask.this, e);
						}
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
			}
			
			if(subgoal)
			{
				plan.dispatchSubgoal(goal);
			}
			else
			{
				plan.dispatchTopLevelGoal(goal);
			}
			
			if(!wait)
			{
				ret.setResult(null);
			}
//				listener.resultAvailable(this, null);
		}
		catch(Exception e)
		{
			ret.setException(e);
//			listener.exceptionOccurred(this, e);
		}
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		if(goal!=null && !goal.isFinished())
		{
			goal.drop();
		}
		return IFuture.DONE;
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The dispatch goal task can be used for dipatching a goal as top-level " +
//			" or subgoal and optinally wait for the result.";
//		
//		ParameterMetaInfo typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			String.class, "type", null, "The type parameter identifies the user goal type.");
//		ParameterMetaInfo paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the goal parameters.");
//		ParameterMetaInfo subgoal = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			boolean.class, "subgoal", null, "The subgoal parameter for dispatching as top-level or subgoal.");
//		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			boolean.class, "wait", null, "The wait parameter to wait for the results.");
//
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, paramsmi, subgoal, waitmi}); 
//	}
}
