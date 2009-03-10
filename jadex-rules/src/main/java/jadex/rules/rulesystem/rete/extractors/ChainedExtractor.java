package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	 * @param left The left input tuple. 
	 * @param right The right input object.
	 * @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, IOAVState state)
	{
		Object ret = extractors[0].getValue(left, right, state);
		for(int i=1; i<extractors.length; i++)
		{
			ret = extractors[i].getValue(null, ret, state);
		}
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
		}
		return ret.toString();
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public Set	getRelevantAttributes()
	{
		Set ret = new HashSet();
		for(int i=0; i<extractors.length; i++)
		{
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
