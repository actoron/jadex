package jadex.rules.rulesystem.rete.builder;

import jadex.rules.rulesystem.rules.Variable;

/**
 *  A variable info saves information about the first occurrence of a variable,
 *  i.e. its declaration and first binding.
 */
public class VarInfo
{
	//-------- attributes --------
	
	/** The variable. */
	private Variable var;
	
	/** The tupleindex. */
	private int tupleindex;
	
	/** The value source. */
	private Object valuesource;
	
	/** The subindex. */
	private int subindex;
	
	
	//-------- constructors --------
	
	/**
	 *  Create a new VarInfo.
	 *  @param var The variable.
	 *  @param tupleindex The tuple index.
	 *  @param attr The attribute.
	 *  @param subindex The subindex.
	 */
	public VarInfo(Variable var, int tupleindex, Object valuesource, int subindex)
	{
		this.var = var;
		this.tupleindex = tupleindex;
		this.valuesource = valuesource;
		this.subindex = subindex;
	}
	
	//-------- methods --------

	/**
	 *  Get the variable.
	 *  @return The variable.
	 */
	public Variable getVariable()
	{
		return var;
	}

	/**
	 *  Get the tupleindex.
	 *  @return The tupleindex.
	 */
	public int getTupleIndex()
	{
		return tupleindex;
	}

	/**
	 *  Get the subindex.
	 *  @return The subindex.
	 */
	public int getSubindex()
	{
		return subindex;
	}
	
	/**
	 *  Get the value source.
	 *  @return The value source.
	 */
	public Object getValueSource()
	{
		return valuesource;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return var+": "+tupleindex+" "+valuesource+" "+subindex;
	}
}