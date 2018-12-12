package jadex.rules.rulesystem.rules;

import java.util.ArrayList;
import java.util.List;

import jadex.rules.rulesystem.rules.functions.IFunction;

/**
 *  Represents a function call.
 */
public class FunctionCall
{
	//-------- attributes --------
	
	/** The function name. */
	protected IFunction function;
	
	/** The sources of the parameter values. */
	protected List paramsources;
	
	//-------- constructors --------
	
	/**
	 *  Create a new function call.
	 */
	public FunctionCall(IFunction function)
	{
		this(function, new ArrayList());
	}
	
	/**
	 *  Create a new function call.
	 */
	public FunctionCall(IFunction function, List paramsources)
	{
		this.function = function;
		this.paramsources = paramsources;
	}
	
	/**
	 *  Create a new function call.
	 */
	public FunctionCall(IFunction function, Object[] paramsources)
	{
		this.function = function;
		this.paramsources = new ArrayList();
		for(int i=0; i<paramsources.length; i++)
			this.paramsources.add(paramsources[i]);
	}
	
	//-------- methods --------

	/**
	 *  Get the function.
	 *  @return The function.
	 */
	public IFunction getFunction()
	{
		return function;
	}
	
	/**
	 *  Add a new parameter source. Can be
	 *  - constant value
	 *  - variable (value)
	 *  - function call (value)
	 */
	public void addParameterSource(Object paramsource)
	{
		this.paramsources.add(paramsource);
	}
	
	/**
	 *  Get the parameter sources.
	 *  @return The param sources.
	 */
	public List getParameterSources()
	{
		return paramsources;
	}

	/**
	 *  Get the variables.
	 *  @return The declared variables.
	 */
	public List getVariables()
	{
		List ret = new ArrayList();
		for(int i=0; i<paramsources.size(); i++)
		{
			ret.addAll(Constraint.getVariablesForValueSource(paramsources.get(i)));
		}
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer(function.toString());
		ret.append("(");
		for(int i=0; i<paramsources.size(); i++)
		{
			if(i>0)
				ret.append(", ");
			ret.append(paramsources.get(i));
		}
		ret.append(")");
		return ret.toString();
	}
}
