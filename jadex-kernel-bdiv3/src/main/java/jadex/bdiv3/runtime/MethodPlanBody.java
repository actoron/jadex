package jadex.bdiv3.runtime;

import jadex.bridge.IInternalAccess;

import java.lang.reflect.Method;

/**
 * 
 */
public class MethodPlanBody extends AbstractPlanBody
{
	/** The method. */
	protected Method body;
	
	/**
	 * 
	 */
	public MethodPlanBody(IInternalAccess ia, RPlan rplan, Method body)
	{
		super(ia, rplan);
		this.body = body;
	}
	
	/**
	 * 
	 */
	public Object executeBody(Object agent, Object[] params)
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
	 * 
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return body.getParameterTypes();
	}

}