package jadex.bpmn.testcases;

import java.net.URLClassLoader;

import jadex.base.Starter;
import jadex.bpmn.annotation.Task;
import jadex.bpmn.annotation.TaskParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
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
	public IFuture<Void> execute(final ITaskContext context, BpmnInterpreter process)
	{
		final Future<Void>	ret	= new Future<Void>();
		String url	= "new String[]{\"../jadex-applications-bpmn/target/classes\"}";	// Todo: support RID for all loaded models.
//		String url	= process.getModel().getResourceIdentifier().getLocalIdentifier().getUrl().toString();
		
		
		Starter.createPlatform(new String[]{"-platformname", process.getComponentIdentifier().getPlatformPrefix()+"_*", "-libpath", url,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-awareness", "false",
			"-gui", "false"
//			"-usepass", "false"//, "-simulation", "false"
//			"-logging_level", "java.util.logging.Level.INFO"
			})
			.addResultListener(process.createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess exta)
			{
				context.setParameterValue("cid", exta.getComponentIdentifier());
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
	public IFuture<Void> compensate(BpmnInterpreter instance)
	{
		return IFuture.DONE;
	}
}
