package jadex.bpmn.testcases;

import jadex.base.Starter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Create a second platform for testing.
 */
@Task(description="Create a second platform for testing", parameters={
	@TaskParameter(name="cid", clazz=IComponentIdentifier.class, direction=TaskParameter.DIRECTION_OUT)
})
public class CreatePlatformTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess process)
	{
		final Future<Void>	ret	= new Future<Void>();
		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUri().toString();
		
		Starter.createPlatform(new String[]{"-platformname", process.getIdentifier().getPlatformPrefix()+"_*",
//			"-logging", "true",
			"-libpath", "new String[]{\""+url+"\"}",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
			"-gui", "false"
//			"-usepass", "false"//, "-simulation", "false"
			})
			.addResultListener(process.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess exta)
			{
				context.setParameterValue("cid", exta.getIdentifier());
//				System.out.println("cid: "+exta.getComponentIdentifier().getAddresses());
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		return IFuture.DONE;
	}
}
