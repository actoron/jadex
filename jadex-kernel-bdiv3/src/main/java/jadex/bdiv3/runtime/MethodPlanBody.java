package jadex.bdiv3.runtime;

import jadex.bridge.IInternalAccess;

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
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the body parameter types.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return body.getParameterTypes();
	}

}