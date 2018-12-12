package jadex.rules.rulesystem.rules;


/**
 *  Test if the function result equals an attribute or method return value.
 *  (slot|method <op> f(var1, var2, ...))
 */
public class ValueSourceReturnValueConstraint extends ReturnValueConstraint
{
	//-------- attributes --------
	
	/** The attribute or method. */
	protected Object valuesource;
	
	//-------- constructors --------
	
	/**
	 *  Create a new return value constraint.
	 */
	public ValueSourceReturnValueConstraint(Object valuesource, FunctionCall funcall)
	{
		this(valuesource, funcall, IOperator.EQUAL);
	}
	
	/**
	 *  Create a new return value constraint.
	 */
	public ValueSourceReturnValueConstraint(Object valuesource, FunctionCall funcall, IOperator operator)
	{
		super(funcall, operator);
		this.valuesource = valuesource;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value source.
	 *  @return The attribute or method.
	 */
	public Object getValueSource()
	{
		return valuesource;
	}
}
