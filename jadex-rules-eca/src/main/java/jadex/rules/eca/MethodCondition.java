package jadex.rules.eca;

import jadex.commons.IMethodParameterGuesser;

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
	
	/** The parameter guesser. */
	protected IMethodParameterGuesser guesser;
	
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
		this(object, method, invert, null);
	}
	
	/**
	 *  Create a new method condition.
	 */
	public MethodCondition(Object object, Method method, boolean invert, IMethodParameterGuesser guesser)
	{
//		if(object==null)
//			System.out.println("hetre");
		
		this.object = object;
		this.method = method;
		this.invert = invert;
		this.guesser = guesser;
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
				Object[] params = null;
				if(guesser!=null)
				{
					params = guesser.guessParameters();
				}
				else
				{
					params = new Object[]{((Event)event).getContent()};
				}
				
				result = method.invoke(object, params);
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
