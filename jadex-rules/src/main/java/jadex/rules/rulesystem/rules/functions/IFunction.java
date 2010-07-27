package jadex.rules.rulesystem.rules.functions;

import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;

/**
 *  Interface for functions.
 */
public interface IFunction
{
	//-------- constants --------
	
	/** The sum function for adding two or more values. */
	public static final IFunction	SUM	= new Sum();
	
	/** The sub function for subtracting one or more values from another value. */
	public static final IFunction	SUB	= new Sub();
	
	/** The mult function for multiplying two or more values. */
	public static final IFunction	MULT	= new Mult();
	
	/** The div function for dividing a value by another value. */
	public static final IFunction	DIV	= new Div();
	
	/** The modulo function for building the remainder after dividing two values. */
	public static final IFunction	MOD	= new Modulo();
	
	//-------- methods --------
	
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
	public AttributeSet	getRelevantAttributes();
}
