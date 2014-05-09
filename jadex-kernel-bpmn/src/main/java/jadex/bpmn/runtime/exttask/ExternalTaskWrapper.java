package jadex.bpmn.runtime.exttask;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ExternalTaskWrapper implements ITask
{	
	/** The pojo task. */
	protected ITask task;
	
	/**
	 *  Create a new wrapper task.
	 */
	public ExternalTaskWrapper(ITask task)
	{
		this.task = task;
	}
	
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess process)
	{
		final Future<Void> ret = new Future<Void>();
		
		SServiceProvider.getService(process.getServiceContainer(), ITaskExecutionService.class)
			.addResultListener(new ExceptionDelegationResultListener<ITaskExecutionService, Void>(ret)
		{
			public void customResultAvailable(ITaskExecutionService tes)
			{
				tes.execute(task, context).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess process)
	{
//		final Future<Void> ret = new Future<Void>();
//		return ret;
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public static class ExternalTaskContext implements ITaskContext
	{
		protected Map<String, Object> params;
		
		protected MActivity mactivity;
		
		/**
		 * 
		 */
		public ExternalTaskContext()
		{
		}
		
		/**
		 * 
		 */
		public ExternalTaskContext(ProcessThread thread)
		{
			this.mactivity = thread.getActivity();
			this.params = new HashMap<String, Object>();
			for(String name: thread.getAllParameterNames())
			{
				params.put(name, thread.getParameterValue(name));
			}
		}
		
		public MActivity getActivity()
		{
			return mactivity;
		}
		
		public MActivity getModelElement()
		{
			return mactivity;
		}
		
		public Object getParameterValue(String name)
		{
			return params.get(name);
		}
		
		public Object getPropertyValue(String name)
		{
			throw new UnsupportedOperationException();
		}
		
		public boolean hasParameterValue(String name)
		{
			return params.containsKey(name);
		}
		
		public void setParameterValue(String name, Object key, Object value)
		{
			if(params==null)
				params = new HashMap<String, Object>();
			
			if(key==null)
			{
				params.put(name, value);	
			}
			else
			{
				Object coll = params.get(name);
				if(coll instanceof List)
				{
					int index = ((Number)key).intValue();
					if(index>=0)
						((List)coll).set(index, value);
					else
						((List)coll).add(value);
				}
				else if(coll!=null && coll.getClass().isArray())
				{
					int index = ((Number)key).intValue();
					Array.set(coll, index, value);
				}
				else if(coll instanceof Map)
				{
					((Map)coll).put(key, value);
				}
//				else
//				{
//					throw new RuntimeException("Unsupported collection type: "+coll);
//				}
			}
		}
		
		public void setParameterValue(String name, Object value)
		{
			params.put(name, value);
		}
	}
	
}
