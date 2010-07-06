package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

/**
 *  Task for destroying a component.
 */
public class DestroyComponentTask implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(ITaskContext context, BpmnInterpreter instance, final IResultListener listener)
	{
		IComponentManagementService ces = (IComponentManagementService)instance.getServiceProvider().getService(IComponentManagementService.class);
		final IResultListener resultlistener = (IResultListener)context.getParameterValue("resultlistener");
		final boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): false;
		
		IComponentIdentifier cid = (IComponentIdentifier)context.getParameterValue("componentid");
		if(cid==null)
		{
			String name = (String)context.getParameterValue("name");
			cid = ces.createComponentIdentifier(name, true, null);
		}
		
		IFuture ret = ces.destroyComponent(cid);
		if(wait || resultlistener!=null)
		{
			ret.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					if(resultlistener!=null)
						resultlistener.resultAvailable(DestroyComponentTask.this, result);
					if(wait)
						listener.resultAvailable(DestroyComponentTask.this, result);
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					if(resultlistener!=null)
						resultlistener.exceptionOccurred(DestroyComponentTask.this, exception);
					if(wait)
						listener.exceptionOccurred(DestroyComponentTask.this, exception);
				}
			});
		}

		if(!wait)
		{
			listener.resultAvailable(this, null);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The destroy component task can be used for killing a specific component.";
		ParameterMetaInfo cidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IComponentIdentifier.class, "componentid", null, "The componentid parameter serves for specifying the component id.");
		ParameterMetaInfo namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter serves for specifying the local component name (if id not available).");
	
		ParameterMetaInfo lismi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			IResultListener.class, "resultlistener", null, "The resultlistener parameter can be used to be notified when the component terminates.");
		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter specifies is the activity should wait for the component being killed." +
				"This is e.g. necessary if the return values should be used.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{cidmi, namemi, lismi, waitmi}); 
	}
}
