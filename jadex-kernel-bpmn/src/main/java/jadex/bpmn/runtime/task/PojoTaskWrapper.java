package jadex.bpmn.runtime.task;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bridge.IInternalAccess;
import jadex.commons.MethodInfo;
import jadex.commons.SimpleParameterGuesser;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *  Task that acts as wrapper for pojo tasks. 
 *  Allows for using pojo tasks in the same way as conventional ITasks.
 */
public class PojoTaskWrapper implements ITask
{	
	/** The pojo task. */
	protected Object pojotask;
	
	/** The cancel method. */
	protected Method cancelmethod;
	
	/**
	 *  Create a new wrapper task.
	 */
	public PojoTaskWrapper(Object pojotask)
	{
		this.pojotask = pojotask;
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
		
		MethodInfo cancelmi = context.getActivity().getCancelMethod(process.getClassLoader());
		if(cancelmi!=null)
		{
			cancelmethod = cancelmi.getMethod(process.getClassLoader());
		}
		
		MethodInfo bodymi = context.getActivity().getBodyMethod(process.getClassLoader());
		Method bodymethod = bodymi.getMethod(process.getClassLoader());
		try
		{
			bodymethod.setAccessible(true);
			
			Set<Object> vals = new LinkedHashSet<Object>();
			vals.add(context);
			vals.add(process);
			if(context.getModelElement().getParameters()!=null)
			{
				for(String pname: context.getActivity().getParameters().keySet())
				{
					if(context.getParameterValue(pname)!=null)
					{
						vals.add(context.getParameterValue(pname));
					}
				}
			}

			Object re = bodymethod.invoke(pojotask, guessParameters(bodymethod.getParameterTypes(), vals));
			if(re instanceof Future)
			{
				((Future<Object>)re).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						List<MParameter> outs = context.getActivity().getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
						if(outs!=null && outs.size()==1)
						{
							MParameter mparam = outs.get(0);
							context.setParameterValue(mparam.getName(), result);
						}
						ret.setResult(null);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			if(t instanceof Error)
			{
				throw (Error)t;
			}
			else if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else
			{
				throw new RuntimeException(t);
			}
//			throw t instanceof BodyAborted? (BodyAborted)t: t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
		
		return ret;
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess process)
	{
		final Future<Void> ret = new Future<Void>();

		if(cancelmethod==null)
		{
			return IFuture.DONE;
		}
		
		try
		{
			Set<Object> vals = new LinkedHashSet<Object>();
			vals.add(process);
			
			cancelmethod.setAccessible(true);
			Object re = cancelmethod.invoke(pojotask, guessParameters(cancelmethod.getParameterTypes(), vals));
			
			if(re instanceof Future)
			{
				((Future<Object>)re).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(null);
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			if(t instanceof Error)
			{
				throw (Error)t;
			}
			else if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else
			{
				throw new RuntimeException(t);
			}
//			throw t instanceof BodyAborted? (BodyAborted)t: t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
		
		return ret;
	}
	
	/**
	 *  Method that tries to guess the parameters for the method call.
	 */
	// Todo: parameter annotations (currently only required for event injection)
	public Object[] guessParameters(Class<?>[] ptypes, Set<Object> vals)
	{
		if(ptypes==null)
			return null;
				
		Object[] ret = new Object[ptypes.length];
		SimpleParameterGuesser g = new SimpleParameterGuesser(vals);
		Annotation[][] anns = null;
		
		for(int i=0; i<ptypes.length; i++)
		{
			boolean	done	= false;
//			for(int j=0; !done && anns!=null && j<anns[i].length; j++)
//			{
//				if(anns[i][j] instanceof ParameterInfo)
//				{
//					done	= true;
//				}
////				else if(anns[i][j] instanceof CheckNotNull)
////				{
////					notnulls[i] = true;
////				}
//			}
			
			if(!done)
			{
				ret[i]	= g.guessParameter(ptypes[i], false);
			}
		}
		
		return ret;
	}

}
