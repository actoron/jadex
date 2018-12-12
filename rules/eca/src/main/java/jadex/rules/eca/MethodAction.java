package jadex.rules.eca;

import java.lang.reflect.Method;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class MethodAction<T> implements IAction<T>
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
	public IFuture<T> execute(IEvent event, IRule rule, Object context, Object condresult)
	{
		try
		{
			method.setAccessible(true);
			Object result = method.invoke(object, new Object[]{event, rule, context, condresult});
			if(result instanceof IFuture)
			{
				return (Future<T>)result;
			}
			else
			{
				return new Future<T>((T)result);
			}
		}
		catch(Exception e)
		{
			return new Future<T>(e);
//			throw new RuntimeException(e);
		}
	}
}
