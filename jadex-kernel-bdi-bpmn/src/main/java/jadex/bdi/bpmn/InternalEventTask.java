package jadex.bdi.bpmn;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

import java.util.List;

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
			String type = (String)context.getPropertyValue("type");
			if(type==null)
				throw new RuntimeException("Type property for internal event not specified: "+this);
			
			final IInternalEvent event	= plan.createInternalEvent((String)type);
			
			List params	= context.getModelElement().getParameters();
			for(int i=0; params!=null && i<params.size(); i++)
			{
				MParameter	param	= (MParameter)params.get(i);
				if(!param.getDirection().equals(MParameter.DIRECTION_OUT) && context.hasParameterValue(param.getName()))
				{
					event.getParameter(param.getName()).setValue(context.getParameterValue(param.getName()));
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

