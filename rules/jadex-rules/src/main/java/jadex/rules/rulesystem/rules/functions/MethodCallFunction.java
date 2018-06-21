package jadex.rules.rulesystem.rules.functions;

import java.lang.reflect.Method;

import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.state.IOAVState;

/**
 *  Invoke a method on an object.
 *  Parameters: {object (null if static), parametervalues...]}
 */
public class MethodCallFunction implements IFunction
{
	//-------- attributes -------- 
	
	/** The method. */
	protected Method method;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a new function.
	 * /
	public MethodCallFunction()
	{
	}*/
	
	/**
	 *  Create a new function.
	 */
	public MethodCallFunction(Method method)
	{
		if(method==null)
			throw new IllegalArgumentException("Method must not null.");
		this.method = method;
	}
	
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state)
	{
		Object	obj;
		Object[] params;
		
//		if(method!=null)
//		{
		
			obj	= paramvalues[0] instanceof ILazyValue? ((ILazyValue)paramvalues[0]).getValue(): paramvalues[0];
			params	= new Object[paramvalues.length-1];
			if(params.length>0)
			{
				for(int i=0; i<params.length; i++)
				{
					params[i] = paramvalues[i+1] instanceof ILazyValue? ((ILazyValue)paramvalues[i+1]).getValue(): paramvalues[i+1];
				}
			}
//			if(params.length>0)
//				System.arraycopy(paramvalues, 1, params, 0, params.length);
//		}
		
//		else
//		{
//			// Hack!!! Should know class / method / parameter types in advance (static)
//			// Hack!!! Should support overloading and polymorphism of parameters as well as 'null' parameter and 'null' object for static methods
//			obj	= paramvalues[0];
//			String	method	= (String)paramvalues[1];
//			params	= new Object[paramvalues.length-2];
//			if(params.length>0)
//				System.arraycopy(paramvalues, 2, params, 0, params.length);
//			Class[]	paramtypes	= new Class[params.length];
//			for(int i=0; i<params.length; i++)
//				paramtypes[i]	= params[i].getClass();
//			
//			try
//			{
//				this.method = obj.getClass().getMethod(method, paramtypes);
//			}
//			catch(Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
		
		try
		{
			return method.invoke(obj, params);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType()
	{
		return Object.class;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet getRelevantAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return method.getName();
	}
}

