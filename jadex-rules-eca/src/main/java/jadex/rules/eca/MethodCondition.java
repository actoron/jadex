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
	
	/** The invert flag. Inverts method result. */
	protected boolean invert;
	
	/**
	 * 
	 */
	public MethodCondition(Object object, Method method)
	{
		this(object, method, false);
	}
	
	/**
	 * 
	 */
	public MethodCondition(Object object, Method method, boolean invert)
	{
		this.object = object;
		this.method = method;
		this.invert = invert;
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
			if(invert)
			{
				ret = !ret;
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}
}
