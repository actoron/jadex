package jadex.rules.rulesystem.rete.extractors;

import jadex.commons.SUtil;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  Extractor for an attribute value (or the whole object).
 */
public class ObjectExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The attribute type. */
	protected OAVAttributeType attr;
	
	/** The key value. */
	protected Object key;
	
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public ObjectExtractor(OAVAttributeType attr)
	{
		this.attr = attr;
	}
	
	/**
	 *  Create a new extractor.
	 */
	public ObjectExtractor(OAVAttributeType attr, Object key)
	{
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
		// Fetch the value from the state
		// a) attr == null -> use object
		// b) attr !=null -> use state.getAttributeValue(object, attr) or
		//                       state.getAttributeValues(object, attr);
		
		if(right instanceof VirtualFact)
			right = ((VirtualFact)right).getObject();
		
		Object ret;
		if(attr==OAVAttributeType.OBJECTTYPE)
		{
			ret = state.getType(right);
		}
		else
		{
			ret = attr==null? right: 
				OAVAttributeType.NONE.equals(attr.getMultiplicity())?
				state.getAttributeValue(right, attr):
				key!=null? state.getAttributeValue(right, attr, key):
				state.getAttributeValues(right, attr);
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
		return tupleindex==-1 && SUtil.equals(this.attr, attr);
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret	= new AttributeSet();
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
	 *  Get the attribute.
	 *  @return The attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return ""+(attr==null? "object": attr.getName())+(key==null? "": "["+key+"]");
	}


	/**
	 *  The hash code.
	 */
	public int hashCode()
	{
		return 31 + (attr!=null ? attr.hashCode() : 0);
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		if(this==obj)
			return true;

		return obj instanceof ObjectExtractor
			&& SUtil.equals(attr, ((ObjectExtractor)obj).getAttribute());
	}
}
