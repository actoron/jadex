package jadex.bpmnbdi.task;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmnbdi.BpmnPlanBodyInstance;
import jadex.commons.concurrent.IResultListener;

import java.util.Iterator;
import java.util.Map;

/**
 *  Dispatch an internal event task.
 */
public class InternalEventTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(final ITaskContext context, IProcessInstance instance, final IResultListener listener)
	{
		try
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
		catch(Exception e)
		{
			listener.exceptionOccurred(e);
		}
	}
}

