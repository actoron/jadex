package jadex.rules.eca;

import java.lang.reflect.Method;

/**
 * 
 */
public class MethodAction implements IAction
{
	/** The object. */
	protected Object object;
	
	/** The method. */
	protected Method method;
	
	/**
	 * 
	 */
	public MethodAction(Object object, Method method)
	{
		this.object = object;
		this.method = method;
	}

	/**
	 * 
	 */
	public void execute(IEvent event, IRule rule, Object context)
	{
		try
		{
			method.setAccessible(true);
			Object result = method.invoke(object, new Object[]{event, rule, context});
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
