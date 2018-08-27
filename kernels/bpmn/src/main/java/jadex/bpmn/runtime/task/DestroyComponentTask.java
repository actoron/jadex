package jadex.bpmn.runtime.task;

import java.util.Map;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Task for destroying a component.
 */
@Task(description="The destroy component task can be used for killing a specific component.", parameters={
	@TaskParameter(name="componentid", clazz=IComponentIdentifier.class, direction=TaskParameter.DIRECTION_IN,
		description="The componentid parameter serves for specifying the component id."),
	@TaskParameter(name="name", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
		description= "The name parameter serves for specifying the local component name (if id not available)."),
	@TaskParameter(name="resultlistener", clazz=IResultListener.class, direction=TaskParameter.DIRECTION_IN,
		description="The componentid parameter serves for specifying the component id."),
	@TaskParameter(name="wait", clazz=boolean.class, direction=TaskParameter.DIRECTION_IN,
		description="The wait parameter specifies is the activity should wait for the component being killed." +
			"This is e.g. necessary if the return values should be used.")
})
public class DestroyComponentTask implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
		final IResultListener resultlistener = (IResultListener)context.getParameterValue("resultlistener");
		final boolean wait = context.getParameterValue("wait")!=null? ((Boolean)context.getParameterValue("wait")).booleanValue(): false;
		
		IComponentIdentifier cid = (IComponentIdentifier)context.getParameterValue("componentid");
		if(cid==null)
		{
			String name = (String)context.getParameterValue("name");
//			cid = ces.createComponentIdentifier(name, true, null);
			if(name.indexOf("@")==-1)
				cid = new BasicComponentIdentifier(name);
			else
				cid = new BasicComponentIdentifier(name, instance.getId().getParent());
		}
		
		IFuture<Map<String, Object>> tmp = instance.killComponent(cid);
		if(wait || resultlistener!=null)
		{
			tmp.addResultListener(new IResultListener<Map<String, Object>>()
			{
				public void resultAvailable(Map<String, Object> result)
				{
					if(resultlistener!=null)
						resultlistener.resultAvailable(result);
					if(wait)
					{
						ret.setResult(null);
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
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		return IFuture.DONE;
	}
	
//	//-------- static methods --------
//	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The destroy component task can be used for killing a specific component.";
//		ParameterMetaInfo cidmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			IComponentIdentifier.class, "componentid", null, "The componentid parameter serves for specifying the component id.");
//		ParameterMetaInfo namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			String.class, "name", null, "The name parameter serves for specifying the local component name (if id not available).");
//	
//		ParameterMetaInfo lismi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			IResultListener.class, "resultlistener", null, "The resultlistener parameter can be used to be notified when the component terminates.");
//		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			boolean.class, "wait", null, "The wait parameter specifies is the activity should wait for the component being killed." +
//				"This is e.g. necessary if the return values should be used.");
//		
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{cidmi, namemi, lismi, waitmi}); 
//	}
}
