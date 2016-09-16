package jadex.bpmn.runtime.task;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.SimpleParameterGuesser;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	
	/** The resinjections. */
	protected Map<String, FieldInfo> resinjections;
	
	/**
	 *  Bean constructor.
	 */
	public PojoTaskWrapper()
	{
	}
	
	/**
	 *  Create a new wrapper task.
	 */
	public PojoTaskWrapper(Object pojotask, IInternalAccess ia, ProcessThread thread, List<FieldInfo> cominjections, Map<String, 
		List<FieldInfo>> arginjections, Map<String, FieldInfo> resinjections)
	{
		this.pojotask = pojotask;
		this.resinjections = resinjections;
		
		for(FieldInfo fi: cominjections)
		{
			try
			{
				Field f = fi.getField(ia.getClassLoader());
				if(SReflect.isSupertype(f.getType(), IInternalAccess.class))
				{
					f.setAccessible(true);
					f.set(pojotask, ia);						
				}
				else if(SReflect.isSupertype(f.getType(), IExternalAccess.class))
				{
					f.setAccessible(true);
					f.set(pojotask, ia.getExternalAccess());
				}
			}
			catch(Exception e)
			{
				System.out.println("Component injection failed: "+e);
			}
		}
		
		for(String name: arginjections.keySet())
		{
			List<FieldInfo> infos = arginjections.get(name);
			
			for(FieldInfo fi: infos)
			{
				try
				{
					Field f = fi.getField(ia.getClassLoader());
					f.setAccessible(true);
					f.set(pojotask, thread.getParameterValue(name));
				}
				catch(Exception e)
				{
					System.out.println("Argument injection failed: "+e);
				}
			}
		}
		
		for(String name: resinjections.keySet())
		{
			FieldInfo fi = resinjections.get(name);
			
			try
			{
				Field f = fi.getField(ia.getClassLoader());
				f.setAccessible(true);
				f.set(pojotask, thread.getParameterValue(name));
			}
			catch(Exception e)
			{
				System.out.println("Result injection failed: "+e);
			}
		}
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

			Class<?> rettype = bodymethod.getReturnType();
			final boolean noret = Void.class.equals(rettype) || void.class.equals(rettype);
 			Object re = bodymethod.invoke(pojotask, guessParameters(bodymethod.getParameterTypes(), vals));
			if(re instanceof Future)
			{
				((Future<Object>)re).addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						setResults(noret, result, context, process);
						ret.setResult(null);
					}
				});
			}
			else
			{
				setResults(noret, re, context, process);
				ret.setResult(null);
			}
		}
		catch(Exception e)
		{
			throw SUtil.throwUnchecked(e);
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
			throw SUtil.throwUnchecked(e);
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

	/**
	 *  Set the results.
	 */
	protected void setResults(boolean noret, Object result, ITaskContext context, IInternalAccess process)
	{
		List<MParameter> outs = context.getActivity().getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
		if(!noret && outs!=null && outs.size()==1)
		{
			MParameter mparam = outs.get(0);
			context.setParameterValue(mparam.getName(), result);
		}
		else if(outs.size()>0)
		{
			if(resinjections!=null)
			{
				for(String name: resinjections.keySet())
				{
					try
					{
						FieldInfo fi = resinjections.get(name);
						Field f = fi.getField(process.getClassLoader());
						f.setAccessible(true);
						Object val = f.get(pojotask);
						context.setParameterValue(name, val);
					}
					catch(Exception e)
					{
						System.out.println("Could not set result: "+e);
					}
				}
			}
		}
	}

	/**
	 *  Get the pojotask.
	 *  @return The pojotask.
	 */
	public Object getPojoTask()
	{
		return pojotask;
	}

	/**
	 *  Set the pojotask.
	 *  @param pojotask The pojotask to set.
	 */
	public void setPojoTask(Object pojotask)
	{
		this.pojotask = pojotask;
	}

	/**
	 *  Get the cancelmethod.
	 *  @return The cancelmethod.
	 */
	public Method getCancelMethod()
	{
		return cancelmethod;
	}

	/**
	 *  Set the cancelmethod.
	 *  @param cancelmethod The cancelmethod to set.
	 */
	public void setCancelMethod(Method cancelmethod)
	{
		this.cancelmethod = cancelmethod;
	}

	/**
	 *  Get the resinjections.
	 *  @return The resinjections.
	 */
	public Map<String, FieldInfo> getResultInjections()
	{
		return resinjections;
	}

	/**
	 *  Set the resinjections.
	 *  @param resinjections The resinjections to set.
	 */
	public void setResultInjections(Map<String, FieldInfo> resinjections)
	{
		this.resinjections = resinjections;
	}
	
}
