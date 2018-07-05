package jadex.bpmn.runtime.exttask;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;

/**
 *  Wrapper for executing a task on a worker agent.
 *  Workers have to implement ITaskExecutionService.
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
		// todo: scope
		ITaskExecutionService tes	= process.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ITaskExecutionService.class));
		// todo: results
		return tes.execute(task, new ExternalTaskContext((ProcessThread)context));
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
	 *  Transferrable context.
	 */
	public static class ExternalTaskContext implements ITaskContext
	{
		/** The parameter values flattened. */
		protected Map<String, Object> params;
		
		/** The acticity. */
		protected MActivity mactivity;
		
		/**
		 *  Create a new context.
		 */
		public ExternalTaskContext()
		{
		}
		
		/**
		 *  Create a new context.
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
		
		/**
		 *  Get the model.
		 *  @return	The bpmn model.
		 */
		public MBpmnModel getBpmnModel()
		{
			// todo: support this?!
			throw new UnsupportedOperationException();
		}
		
		/**
		 *  Get the activity.
		 *  @return The activity.
		 */
		public MActivity getActivity()
		{
			return mactivity;
		}
		
		/**
		 *  Get the modelelement.
		 *  @return The modelelement.
		 */
		public MActivity getModelElement()
		{
			return mactivity;
		}
		
		/**
		 *  Get a parameter value.
		 *  @param name The name.
		 *  @return The object.
		 */
		public Object getParameterValue(String name)
		{
			return params.get(name);
		}
		
		/**
		 *  Get a property value.
		 *  @param name The name.
		 *  @return The object.
		 */
		public Object getPropertyValue(String name)
		{
			throw new UnsupportedOperationException();
		}
		
		/**
		 *  Test if context has a parameter.
		 *  @param name The name.
		 *  @return True, if has parameter.
		 */
		public boolean hasParameterValue(String name)
		{
			return params.containsKey(name);
		}
		
		/**
		 *  Set a parameter value.
		 *  @param name The name.
		 *  @param key The key.
		 *  @param value The value.
		 */
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
		
		/**
		 *  Set a parameter value.
		 *  @param name The name.
		 *  @param value The value.
		 */
		public void setParameterValue(String name, Object value)
		{
			params.put(name, value);
		}
		
		/**
		 *  Set or create a parameter value directly in this thread.
		 *  @param name	The parameter name.
		 *  @param value The parameter value. 
		 */
		public void setOrCreateParameterValue(String name, Object value)
		{
			setParameterValue(name, value);
		}
		
		/**
		 *  Set or create a parameter value directly in this thread.
		 *  @param name	The parameter name.
		 *  @param key An optional helper (index, key etc.) 
		 *  @param value The parameter value. 
		 */
		public void setOrCreateParameterValue(String name, Object key, Object value)
		{
			setParameterValue(name, key, value);
		}


		/**
		 *  Get the params.
		 *  @return The params.
		 */
		public Map<String, Object> getParameters()
		{
			return params;
		}

		/**
		 *  Set the params.
		 *  @param params The params to set.
		 */
		public void setParameters(Map<String, Object> params)
		{
			this.params = params;
		}

		/**
		 *  Set the mactivity.
		 *  @param mactivity The mactivity to set.
		 */
		public void setActivity(MActivity mactivity)
		{
			this.mactivity = mactivity;
		}
	}
	
}
