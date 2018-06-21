package jadex.rules.rulesystem.rete.extractors;

import java.util.Arrays;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  Extractor for chained expressions (e.g. obj1.attr1.attr2)
 */
public class ChainedExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The value extractors. */
	protected IValueExtractor[] extractors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public ChainedExtractor(IValueExtractor[] extractors)
	{
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
	public Object getValue(Tuple left, Object right, Object prefix, IOAVState state)
	{
		prefix = extractors[0].getValue(left, right, prefix, state);
		for(int i=1; prefix!=null && i<extractors.length; i++)
		{
			// Todo: throw exception when prefix nulls
//			if(prefix==null)
//				throw new NullPointerException(this.toString());
			prefix = extractors[i].getValue(left, right, prefix, state);
		}
		return prefix;
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
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<extractors.length; i++)
		{
			ret.append(extractors[i].toString());
			if(i<extractors.length-1)
				ret.append(", ");
		}
		return ret.toString();
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		ret.addAll(extractors[0].getRelevantAttributes());
		return ret;
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		AttributeSet ret = new AttributeSet();
		for(int i=0; i<extractors.length; i++)
		{
			ret.addAll(extractors[i].getIndirectAttributes());
			if(i!=0)
				ret.addAll(extractors[i].getRelevantAttributes());
		}
		return ret;
	}

	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		int ret = 0;
		for(int i=0; i<extractors.length; i++)
		{
			ret += extractors[i].hashCode();
		}
		return ret;
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		return (obj instanceof ChainedExtractor) &&
			Arrays.equals(extractors, ((ChainedExtractor)obj).getExtractors());
	}

	/**
	 *  Get the extractors.
	 *  @return The extractors.
	 */
	public IValueExtractor[] getExtractors()
	{
		return this.extractors;
	}
}
