package jadex.bdibpmn.task;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;

import java.util.Iterator;
import java.util.Map;

/**
 *  Dispatch an internal event task.
 */
public class DispatchInternalEventTask	extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(final ITaskContext context, IInternalAccess instance)
	{
		BpmnPlanBodyInstance	plan	= (BpmnPlanBodyInstance)instance;
		String type = (String)context.getParameterValue("type");
		if(type==null)
			throw new RuntimeException("Parameter 'type' for internal event not specified: "+instance);
		
		final IInternalEvent event	= plan.createInternalEvent((String)type);
		
		Map params	= context.hasParameterValue("parameters")
			? (Map) context.getParameterValue("parameters") : null;
		if(params!=null)
		{
			for(Iterator it=params.keySet().iterator(); it.hasNext(); )
			{
				String	param = (String)it.next();
				event.getParameter(param).setValue(params.get(param));
			}
		}
		
		plan.dispatchInternalEvent(event);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The dispatch internal event task can be used for dipatching an internal event.";
		
		ParameterMetaInfo typemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "type", null, "The type parameter identifies the user goal type.");
		ParameterMetaInfo paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the goal parameters.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{typemi, paramsmi}); 
	}
}

