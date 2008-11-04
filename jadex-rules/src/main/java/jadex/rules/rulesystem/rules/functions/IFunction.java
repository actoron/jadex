package jadex.rules.rulesystem.rules.functions;

import jadex.rules.state.IOAVState;

import java.util.Set;

/**
 *  Interface for functions.
 */
public interface IFunction
{
	/**
	 *  Invoke a function and return a value (optional).
	 *  @param paramvalues The parameter values.
	 *  @param state The state.
	 *  @return The function value. 
	 */
	public Object invoke(Object[] paramvalues, IOAVState state);
	
	/**
	 *  Get the return type of this function.
	 */
	public Class getReturnType();
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public Set	getRelevantAttributes();
}
