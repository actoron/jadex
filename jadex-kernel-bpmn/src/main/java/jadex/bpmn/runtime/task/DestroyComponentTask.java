package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Task for destroying a component.
 */
public class DestroyComponentTask implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(instance.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(instance.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService ces = (IComponentManagementService)result;
				final IResultListener resultlistener = (IResultListener)context.getParameterValue("resultlistener");
				final boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): false;
				
				IComponentIdentifier cid = (IComponentIdentifier)context.getParameterValue("componentid");
				if(cid==null)
				{
					String name = (String)context.getParameterValue("name");
//					cid = ces.createComponentIdentifier(name, true, null);
					if(name.indexOf("@")==-1)
						cid = new ComponentIdentifier(name);
					else
						cid = new ComponentIdentifier(name, instance.getComponentIdentifier().getParent());
				}
				
				IFuture tmp = ces.destroyComponent(cid);
				if(wait || resultlistener!=null)
				{
					tmp.addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							if(resultlistener!=null)
								resultlistener.resultAvailable(result);
							if(wait)
							{
								ret.setResult(result);
//								listener.resultAvailable(DestroyComponentTask.this, result);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(resultlistener!=null)
								resultlistener.exceptionOccurred(exception);
							if(wait)
							{
								ret.setException(exception);
//								listener.exceptionOccurred(DestroyComponentTask.this, exception);
							}
						}
					});
				}

				if(!wait)
				{
					ret.setResult(null);
//					listener.resultAvailable(this, null);
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture cancel(final BpmnInterpreter instance)
	{
		return IFuture.DONE;
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
