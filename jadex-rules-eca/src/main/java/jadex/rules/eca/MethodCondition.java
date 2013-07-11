package jadex.rules.eca;

import java.lang.reflect.Method;

/**
 *  Condition implementation that invokes a predefined method.
 */
public class MethodCondition implements ICondition
{
	//-------- attributes --------
	
	/** The object. */
	protected Object object;
	
	/** The method. */
	protected Method method;
	
	/** The invert flag. Inverts method result. */
	protected boolean invert;
	
	//-------- constructors --------
	
	/**
	 *  Create a new method condition.
	 */
	public MethodCondition(Object object, Method method)
	{
		this(object, method, false);
	}
	
	/**
	 *  Create a new method condition.
	 */
	public MethodCondition(Object object, Method method, boolean invert)
	{
//		if(object==null)
//			System.out.println("hetre");
		
		this.object = object;
		this.method = method;
		this.invert = invert;
	}

	//-------- methods --------

	/**
	 *  Evaluate the condition.
	 */
	public boolean evaluate(IEvent event)
	{
		boolean ret = false;
		try
		{
			method.setAccessible(true);
			Object result = null;
			if(method.getParameterTypes().length==0)
			{
				result = method.invoke(object, new Object[0]);
			}
			else
			{
				result = method.invoke(object, new Object[]{((Event)event).getContent()});
			}
			
			ret = ((Boolean)result).booleanValue();
			if(invert)
			{
				ret = !ret;
			}
		}
		catch(Exception e)
		{
			throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
		}
		return ret;
	}
}
