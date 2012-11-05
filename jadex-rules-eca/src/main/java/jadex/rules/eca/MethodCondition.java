package jadex.rules.eca;

import java.lang.reflect.Method;

/**
 * 
 */
public class MethodCondition implements ICondition
{
	/** The object. */
	protected Object object;
	
	/** The method. */
	protected Method method;
	
	/**
	 * 
	 */
	public MethodCondition(Object object, Method method)
	{
		this.object = object;
		this.method = method;
	}

	/**
	 * 
	 */
	public boolean evaluate(IEvent event)
	{
		boolean ret = false;
		try
		{
			method.setAccessible(true);
			Object result = method.invoke(object, ((Event)event).getContent());
			ret = ((Boolean)result).booleanValue();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}
}
