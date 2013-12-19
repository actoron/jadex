package jadex.bdiv3.runtime.impl;

import jadex.bridge.IInternalAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *  Implementation of a method as a plan body.
 */
public class MethodPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The method. */
	protected Method body;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan body.
	 */
	public MethodPlanBody(IInternalAccess ia, RPlan rplan, Method body)
	{
		super(ia, rplan);
		this.body = body;
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke the body.
	 */
	public Object invokeBody(Object agent, Object[] params)
	{
		try
		{
			body.setAccessible(true);
			return body.invoke(agent, params);
		}
		catch(Exception e)
		{
			Throwable	t	= e;
			if(e instanceof InvocationTargetException)
			{
				t	= ((InvocationTargetException)e).getTargetException();
			}
			if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else if(t instanceof Error)
			{
				throw (Error)t;
			}
			else
			{
				throw new RuntimeException(t);
			}
		}
	}
	
	public Object invokePassed(Object agent, Object[] params)
	{
		return null;
	}

	public Object invokeFailed(Object agent, Object[] params)
	{
		return null;
	}

	public Object invokeAborted(Object agent, Object[] params)
	{
		return null;
	}

	/**
	 *  Get the body parameter types.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return body.getParameterTypes();
	}

	public Class<?>[] getPassedParameterTypes()
	{
		return null;
	}

	public Class<?>[] getFailedParameterTypes()
	{
		return null;
	}

	public Class<?>[] getAbortedParameterTypes()
	{
		return null;
	}
}