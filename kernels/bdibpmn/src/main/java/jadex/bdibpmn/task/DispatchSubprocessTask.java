package jadex.bdibpmn.task;

import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;

import java.util.HashMap;
import java.util.Map;

/**
 *  Dispatch a subprocess and by default wait for the result.
 */
@Task(description= "The dispatch subprocess task can be used for dipatching subprocess " +
		" (any component) and optionally wait for the result.",
parameters={@TaskParameter(name="processref", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The process reference that identifies process model."),
	@TaskParameter(name="parameters", clazz=Map.class, direction=TaskParameter.DIRECTION_IN,
		description="The 'parameter' parameter allows to specify the process parameters."),
	@TaskParameter(name="wait", clazz=boolean.class, direction=TaskParameter.DIRECTION_IN,
		description="The wait parameter to wait for the results."),
	@TaskParameter(name="resultfuture", clazz=IFuture.class, direction=TaskParameter.DIRECTION_OUT,
		description="The future for results to be retrieved later.")}
)
public class DispatchSubprocessTask	implements ITask
{
	/** Future to indicate creation completion. */
	protected ITuple2Future<IComponentIdentifier, Map<String, Object>> creationfut;
	
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess instance)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			BpmnPlanBodyInstance	plan	= (BpmnPlanBodyInstance)instance;
			String processref = (String)context.getParameterValue("processref");
			if(processref==null)
				throw new RuntimeException("Parameter 'processref' for subprocess not specified: "+instance);
			Map params = context.hasParameterValue("parameters")
				? (Map)context.getParameterValue("parameters") : null;
			final boolean	wait	= context.hasParameterValue("wait")
				? ((Boolean)context.getParameterValue("wait")).booleanValue() : true;
			
			IComponentManagementService cms = plan.getInterpreter().getCMS();
//			IComponentManagementService cms = (IComponentManagementService) plan.getScope().getServiceProvider().getService(IComponentManagementService.class);
//			final Object	goal	= new Object() ;
			if(params == null)
				params = new HashMap();
//			cms.createComponent(null, processref, new CreationInfo(params), rf).addResultListener(instance.createResultListener(new DelegationResultListener(creationfut)));
			creationfut = cms.createComponent(null, processref, new CreationInfo(params));
			final Future<Map<String, Object>> rf = new Future<Map<String,Object>>();
			
			creationfut.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
			{
				public void firstResultAvailable(IComponentIdentifier result)
				{
				}
				
				public void secondResultAvailable(Map<String, Object> result)
				{
					if(wait)
					{
						ret.setResult(null);
					}
					rf.setResult(result);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					if(wait)
					{
						ret.setException(exception);
					}
					rf.setException(exception);
				}
			});
			
			
			if(context.getModelElement().hasParameter("resultfuture"))
				context.setParameterValue("resultfuture", rf);
			
			if(!wait)
			{
				ret.setResult(null);
//				listener.resultAvailable(this, null);
			}
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
		final Future<Void> ret = new Future<Void>();
		if(creationfut!=null)
		{
			creationfut.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
			{
				public void firstResultAvailable(final IComponentIdentifier cid)
				{
					(IServiceProvider)instance.getServiceContainer().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
						.addResultListener(instance.createResultListener(new IResultListener<IComponentManagementService>()
					{
						public void resultAvailable(IComponentManagementService cms)
						{
							cms.destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret)
							{
								public void customResultAvailable(Map<String, Object> result)
								{
									ret.setResult(null);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
							ret.setResult(null);
						}
					}));
				}
				
				public void secondResultAvailable(Map<String, Object> result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	private static class ResultFuture implements IResultFuture, IResultListener
//	{
//		private boolean unfinished = true;
//		private Object result;
//		
//		public synchronized void resultAvailable(Object result)
//		{
//			this.result = result;
//			unfinished = false;
//			notifyAll();
//		}
//		
//		public synchronized void exceptionOccurred(Exception exception)
//		{
//			result = exception;
//			unfinished = false;
//			notifyAll();
//		}
//		
//		public synchronized Object getResults()
//		{
//			while(unfinished)
//			{
//				try
//				{
//					wait();
//				}
//				catch (InterruptedException e)
//				{
//				}
//			}
//			return result;
//		}
//	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The dispatch subprocess task can be used for dipatching subprocess " +
			" (any component) and optionally wait for the result.";
		
		ParameterMetaInfo processrefmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "processref", null, "The process reference that identifies process model.");
		ParameterMetaInfo paramsmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Map.class, "parameters", null, "The 'parameter' parameter allows to specify the process parameters.");
		ParameterMetaInfo waitmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			boolean.class, "wait", null, "The wait parameter to wait for the results.");
		ParameterMetaInfo cwmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT, 
				IResultFuture.class, "resultfuture", null, "The future for results to be retrieved later.");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{processrefmi, paramsmi, waitmi, cwmi}); 
	}
}
