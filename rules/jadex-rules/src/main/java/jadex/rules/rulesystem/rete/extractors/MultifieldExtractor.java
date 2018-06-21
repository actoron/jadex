package jadex.rules.rulesystem.rete.extractors;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  A multifield extractor has the purpose of extracting a value
 *  or a collection of values from a collection type attribute.
 */
public class MultifieldExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The tupleindex. */
	protected int tupleindex;
	
	/** The attribute. */
	protected OAVAttributeType attr;
	
	/** The subindex. */
	protected int subindex;
	
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public MultifieldExtractor(int tupleindex, OAVAttributeType attr, int subindex) 
	{
		assert attr!=null && !OAVAttributeType.NONE.equals(attr.getMultiplicity());
		
		this.tupleindex = tupleindex;
		this.attr = attr;
		this.subindex = subindex;
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
		// Fetch the virtual fact
		Object object = tupleindex==-1? right: left.getObject(tupleindex);
		
		if(!(object instanceof VirtualFact))
		{
			throw new RuntimeException("Multiextractor can only work with virtual fact: "+object);
		}
		VirtualFact vf = (VirtualFact)object;

		return vf.getSubAttributeValue(attr, subindex);
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
		return (this.tupleindex==tupleindex || tupleindex==-1) && SUtil.equals(this.attr, attr);
	}

	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		if(attr!=null)
			ret.addAttribute(attr);
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
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return (tupleindex!=-1? "["+tupleindex+"].": "")+
			(attr==null? "object": attr.getName())+
			(subindex==-1? "": ".["+subindex+"]");
	}

	/**
	 *  Get the attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}

	/**
	 *  Get the tuple index.
	 */
	public int getTupleIndex()
	{
		return tupleindex;
	}

	/**
	 *  Get the sub index.
	 */
	public int getSubindex()
	{
		return subindex;
	}
	
	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		int	result	= 31 + (attr!=null ? attr.hashCode() : 0);
		result	= result*31 + tupleindex;
		result	= result*31 + subindex;
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
		if(obj instanceof MultifieldExtractor)
		{
			MultifieldExtractor	other	= (MultifieldExtractor)obj;
			ret	= SUtil.equals(attr, other.getAttribute())
				&& tupleindex==other.getTupleIndex()
				&& subindex==other.getSubindex();
		}
		return ret;
	}
}
