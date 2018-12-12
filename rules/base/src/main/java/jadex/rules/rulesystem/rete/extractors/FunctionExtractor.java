package jadex.rules.rulesystem.rete.extractors;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rules.ILazyValue;
import jadex.rules.rulesystem.rules.functions.IFunction;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A function extractor has the purpose to call a function
 *  and return the result value.
 */
public class FunctionExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The function. */
	protected IFunction function;
	
	/** The extractors. */
	protected IValueExtractor[] extractors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new evaluator.
	 */
	public FunctionExtractor(IFunction function, IValueExtractor[] extractors)
	{
		this.function = function;
		this.extractors = extractors;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value of an attribute from an object or tuple.
	 *  @param left The left input tuple. 
	 *  @param right The right input object.
	 *  @param prefix The prefix input object (last value from previous extractor in a chain).
	 *  @param state The working memory.
	 */
	public Object getValue(final Tuple left, final Object right, final Object prefix, final IOAVState state)
	{
		// Fetch the parameter values.
		Object[] paramvalues = new Object[extractors.length];
		for(int i=0; i<paramvalues.length; i++)
		{
			//paramvalues[i] = extractors[i].getValue(left, right, prefix, state);
			final IValueExtractor ex = extractors[i]; 
			paramvalues[i] = new ILazyValue()
			{
				public Object getValue()
				{
					return ex.getValue(left, right, prefix, state);
				}
			};
		}
		
		// Invoke the function and return the value.
		Object ret = function.invoke(paramvalues, state);
//		System.out.println("Funcall result: "+this+" "+ret);
		return ret;
	}
	
	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index.
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr)
	{
		boolean ret = false;
		
		for(int i=0; !ret && i<extractors.length; i++)
		{
			ret = extractors[i].isAffected(tupleindex, attr);
		}
		if(!ret)
			ret = function.getRelevantAttributes().contains(attr);
		return ret;
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		for(int i=0; i<extractors.length; i++)
			ret.addAll(extractors[i].getRelevantAttributes());
//		System.out.println("function: "+function.getClass());
		ret.addAll(function.getRelevantAttributes());
		return ret;
	}

	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		AttributeSet ret	= new AttributeSet();
		for(int i=0; i<extractors.length; i++)
			ret.addAll(extractors[i].getIndirectAttributes());
//		ret.addAll(function.getIndirectAttributes());
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer(function.toString()).append("(");
		for(int i=0; i<extractors.length; i++)
		{
			if(i>0)
				ret.append(", ");
			ret.append(extractors[i]);
		}
		ret.append(")");
		return ret.toString();
	}

	/**
	 *  Get the value extractors.
	 */
	public IValueExtractor[] getValueExtractors()
	{
		return extractors;
	}

	/**
	 *  Get the function.
	 */
	public IFunction getFunction()
	{
		return function;
	}
	
	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		int	result	= 31 + (function!=null ? function.hashCode() : 0);
		// Arrays.hashCode(Object[]): JDK 1.5
		result	= result*31 + SUtil.arrayHashCode(extractors);
		return result;
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		boolean	ret	= false;
		if(obj instanceof FunctionExtractor)
		{
			FunctionExtractor	other	= (FunctionExtractor)obj;
			ret	= SUtil.equals(extractors, other.getFunction())
				&& SUtil.equals(function, other.getFunction());
		}
		return ret;
	}
}

