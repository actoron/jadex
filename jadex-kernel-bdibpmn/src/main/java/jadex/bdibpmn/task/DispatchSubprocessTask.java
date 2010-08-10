package jadex.bdibpmn.task;

import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 *  Dispatch a subprocess and by default wait for the result.
 */
public class DispatchSubprocessTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture execute(final ITaskContext context, BpmnInterpreter instance)
	{
		final Future ret = new Future();
		
		try
		{
			BpmnPlanBodyInstance	plan	= (BpmnPlanBodyInstance)instance;
			String processref = (String)context.getParameterValue("processref");
			if(processref==null)
				throw new RuntimeException("Parameter 'processref' for subprocess not specified: "+instance);
			Map params = context.hasParameterValue("parameters")
				? (Map)context.getParameterValue("parameters") : null;
			boolean	wait	= context.hasParameterValue("wait")
				? ((Boolean)context.getParameterValue("wait")).booleanValue() : true;
			
			IComponentManagementService cms = plan.getInterpreter().getCMS();
//			IComponentManagementService cms = (IComponentManagementService) plan.getScope().getServiceProvider().getService(IComponentManagementService.class);
//			final Object	goal	= new Object() ;
			ResultFuture rf = new ResultFuture();
			if (params == null)
				params = new HashMap();
			cms.createComponent(null, processref, new CreationInfo(params), rf);
			
			if(context.getModelElement().hasParameter("resultfuture"))
				context.setParameterValue("resultfuture", rf);
			
			if(wait)
			{
				Object result = rf.getResults();
				if(result instanceof Exception)
					ret.setException((Exception)result);
//					listener.exceptionOccurred(DispatchSubprocessTask.this, (Exception)result);
				else
					ret.setResult(result);
//					listener.resultAvailable(DispatchSubprocessTask.this, null);
			}
			else
				ret.setResult(null);
//				listener.resultAvailable(this, null);
		}
		catch(Exception e)
		{
			ret.setException(e);
//			listener.exceptionOccurred(this, e);
		}
		
		return ret;
	}
	
	private static class ResultFuture implements IResultFuture, IResultListener
	{
		private boolean unfinished = true;
		private Object result;
		
		public synchronized void resultAvailable(Object source, Object result)
		{
			this.result = result;
			unfinished = false;
			notifyAll();
		}
		
		public synchronized void exceptionOccurred(Object source, Exception exception)
		{
			result = exception;
			unfinished = false;
			notifyAll();
		}
		
		public synchronized Object getResults()
		{
			while (unfinished)
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
				}
			return result;
		}
	}
	
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
