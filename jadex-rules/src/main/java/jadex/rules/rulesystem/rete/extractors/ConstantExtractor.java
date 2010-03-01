package jadex.rules.rulesystem.rete.extractors;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A constant extractor is responsible for extracting a constant value.
 */
public class ConstantExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The value. */
	protected Object value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public ConstantExtractor(Object value)
	{
		this.value = value;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the value of an attribute from an object or tuple.
	 *  @param left The left input tuple. 
	 *  @param right The right input object.
	 *  @param prefix The prefix input object (last value from previous extractor in a chain).
	 *  @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, Object prefix, IOAVState state)
	{
		return value;
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
		return false;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return ""+value;
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the constant value.
	 */
	public Object getValue()
	{
		return value;
	}
	
	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		return 31 + (value!=null ? value.hashCode() : 0);
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		return (obj instanceof ConstantExtractor)
			&& SUtil.equals(value, ((ConstantExtractor)obj).getValue());
	}
}
