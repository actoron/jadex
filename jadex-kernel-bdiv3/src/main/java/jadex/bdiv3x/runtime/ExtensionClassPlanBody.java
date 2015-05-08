package jadex.bdiv3x.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.runtime.impl.AbstractPlanBody;
import jadex.bdiv3.runtime.impl.BodyAborted;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;

/**
 * 
 */
public class ExtensionClassPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The body class. */
	protected Class<?> body;
	
	/** The body instance. */
	protected Object plan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan body.
	 */
	public ExtensionClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body)
	{
		super(ia, rplan);
		this.body = body;
	}
	
	/**
	 *  Get the body impl (object that is actually invoked).
	 *  @return The object representing the body. 
	 */
	public Object getBody()
	{
		if(plan==null)
		{
			try
			{
				// create plan  
				Constructor<?>[] cons = body.getDeclaredConstructors();
				for(Constructor<?> c: cons)
				{
					Object[] params = BDIAgentFeature
						.getInjectionValues(c.getParameterTypes(), c.getParameterAnnotations(), rplan.getModelElement(), null, rplan, null, ia);
					if(params!=null)
					{
						try
						{
							c.setAccessible(true);
							plan = c.newInstance(params);
							break;
						}
						catch(Exception e)
						{
						}							
					}						
				}
				if(plan==null)
					throw new RuntimeException("Plan body has no accessible constructor (maybe wrong args?): "+body);
				
				Field f = Plan.class.getDeclaredField("agent");
				f.setAccessible(true);
				f.set(plan, getAgent());
				f = Plan.class.getDeclaredField("rplan");
				f.setAccessible(true);
				f.set(plan, getRPlan());
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
	//			e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		return plan;
	}
	
	/**
	 *  Invoke the plan body.
	 */
	public Object invokeBody(Object[] params) throws BodyAborted
	{
		try
		{
			getBody();
			Method bm = body.getMethod("body", new Class[0]);
			bm.invoke(plan, new Object[0]);
			return null;
		}
		catch(Exception e)
		{
			Throwable t = e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
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
	}

	/**
	 *  Invoke the plan passed method.
	 */
	public Object invokePassed(Object[] params)
	{
		try
		{
			Method bm = body.getMethod("passed", new Class[0]);
			bm.invoke(plan, new Object[0]);
			return null;			
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
	}

	/**
	 *  Invoke the plan failed method.
	 */
	public Object invokeFailed(Object[] params)
	{
		try
		{
			Method bm = body.getMethod("failed", new Class[0]);
			bm.invoke(plan, new Object[0]);
			return null;			
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
	}

	/**
	 *  Invoke the plan aborted method.
	 */
	public Object invokeAborted(Object[] params)
	{
		try
		{
			Method bm = body.getMethod("aborted", new Class[0]);
			bm.invoke(plan, new Object[0]);
			return null;			
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
	}
	
	/**
	 *  Get the body parameters.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return null;
	}
	
	/**
	 *  Get the passed parameters.
	 */
	public Class<?>[] getPassedParameterTypes()
	{
		return null;
	}

	/**
	 *  Get the failed parameters.
	 */
	public Class<?>[] getFailedParameterTypes()
	{
		return null;
	}

	/**
	 *  Get the aborted parameters.
	 */
	public Class<?>[] getAbortedParameterTypes()
	{
		return null;
	}
}
