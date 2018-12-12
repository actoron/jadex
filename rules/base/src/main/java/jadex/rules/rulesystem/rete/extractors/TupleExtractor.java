package jadex.rules.rulesystem.rete.extractors;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  Extractor for fetching a value from a rete tuple.
 */
public class TupleExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The tuple index. */
	protected int tupleindex;
	
	/** The attribute type. */
	protected OAVAttributeType attr;
	
	/** The key value. */
	protected Object key;
	
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public TupleExtractor(int tupleindex, OAVAttributeType attr)
	{
		this(tupleindex, attr, null);
	}
	
	/**
	 *  Create a new extractor.
	 */
	public TupleExtractor(int tupleindex, OAVAttributeType attr, Object key)
	{
		this.tupleindex = tupleindex;
		this.attr = attr;
		this.key = key;
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
		// Fetch the object from the tuple
		
		// Fetch the value from the state
		// a) attr == null -> use object
		// b) attr !=null -> use state.getAttributeValue(object, attr) or
		//                       state.getAttributeValues(object, attr);
		
		Object object = left.getObject(tupleindex);
		
		if(object instanceof VirtualFact)
			object = ((VirtualFact)object).getObject();
		
		Object ret;
		if(attr==OAVAttributeType.OBJECTTYPE)
		{
			ret = state.getType(object);
		}
		else
		{
			ret = attr==null?  object: 
				OAVAttributeType.NONE.equals(attr.getMultiplicity())?
					state.getAttributeValue(object, attr):
					key!=null? state.getAttributeValue(object, attr, key):
					state.getAttributeValues(object, attr);
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
		return (this.tupleindex==tupleindex || tupleindex==-1) && SUtil.equals(this.attr, attr);
	}
	
	/**
	 *  Get the attribute.
	 *  @return The attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}
	
	/**
	 *  Get the set of relevant attribute types.
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
	 *  Get the tuple index.
	 *  @return The tuple index.
	 */
	public int getTupleIndex()
	{
		return tupleindex;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "["+tupleindex+"]"+"."+(attr==null? "object": attr.getName())+(key==null? "": "["+key+"]");
	}

	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		int	result	= 31 + (attr!=null ? attr.hashCode() : 0);
		result	= result*31 + tupleindex;
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
		if(obj instanceof TupleExtractor)
		{
			TupleExtractor	other	= (TupleExtractor)obj;
			ret	= SUtil.equals(attr, other.getAttribute())
				&& tupleindex==other.getTupleIndex();
		}
		return ret;
	}
}
