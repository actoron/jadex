package jadex.bdiv3.runtime.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.model.MElement;
import jadex.bridge.IInternalAccess;

/**
 *  Implementation of a method as a plan body.
 */
public class MethodPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The method. */
	protected Method body;
	
	/** The agent/capability object. */
	protected Object agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan body.
	 */
	public MethodPlanBody(IInternalAccess ia, RPlan rplan, Method body)
	{
		super(ia, rplan);
		this.body = body;
		String	pname	= rplan.getModelElement().getName();
		String	capaname	= pname.indexOf(MElement.CAPABILITY_SEPARATOR)==-1
			? null : pname.substring(0, pname.lastIndexOf(MElement.CAPABILITY_SEPARATOR));
		this.agent	= ((BDIAgentFeature)ia.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname);
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke the body.
	 */
	public Object invokeBody(Object[] params)
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
	
	public Object invokePassed(Object[] params)
	{
		return null;
	}

	public Object invokeFailed(Object[] params)
	{
		return null;
	}

	public Object invokeAborted(Object[] params)
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