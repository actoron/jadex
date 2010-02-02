package jadex.bpmnbdi.task;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmnbdi.BpmnPlanBodyInstance;

import java.util.Iterator;
import java.util.Map;

/**
 *  Dispatch an internal event task.
 */
public class InternalEventTask	extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(final ITaskContext context, BpmnInterpreter instance)
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
}

