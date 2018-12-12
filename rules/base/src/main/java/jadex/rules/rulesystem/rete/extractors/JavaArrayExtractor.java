package jadex.rules.rulesystem.rete.extractors;

import java.lang.reflect.Array;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

/**
 *  An extractor for getting elements from arrays.
 */
public class JavaArrayExtractor implements IValueExtractor
{
	//-------- attributes --------
	
	/** The object extractor. */
	protected IValueExtractor	objex;
	
	/** The index value extractor. */
	protected IValueExtractor	index;
	
	//-------- constructors --------
	
	/**
	 *  Create a new array extractor.
	 *  @param objex	The object extractor.
	 *  @param index	The index extractor.
	 */
	public JavaArrayExtractor(IValueExtractor objex, IValueExtractor index)
	{
		this.objex	= objex;
		this.index	= index;
	}
	
	//-------- IValueExtractor interface --------
	
	/**
	 *  Get the value of an attribute from an object or tuple.
	 *  @param left The left input tuple. 
	 *  @param right The right input object.
	 *  @param prefix The prefix input object (last value from previous extractor in a chain).
	 *  @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, Object prefix, IOAVState state)
	{
		Object	array	= objex.getValue(left, right, prefix, state);
		int	i	= ((Number)index.getValue(left, right, prefix, state)).intValue();
		return Array.get(array, i);
	}

	/**
	 *  Test if a constraint evaluator is affected from a 
	 *  change of a certain attribute.
	 *  @param tupleindex The tuple index (-1 for object).
	 *  @param attr The attribute.
	 *  @return True, if affected.
	 */
	public boolean isAffected(int tupleindex, OAVAttributeType attr)
	{
		return objex.isAffected(tupleindex, attr) || index.isAffected(tupleindex, attr);
	}

	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		AttributeSet ret = new AttributeSet();
		AttributeSet objrel	= objex.getRelevantAttributes();
		AttributeSet indrel	= index.getRelevantAttributes();
		
		ret.addAll(objrel);
		ret.addAll(indrel);
		
		return ret;
		
//		if(!objrel.isEmpty() && !indrel.isEmpty())
//		{
//			ret	= new HashSet();
//			ret.addAll(objrel);
//			ret.addAll(indrel);
//		}
//		else if(objrel.isEmpty())
//		{
//			ret	= indrel;
//		}
//		else
//		{
//			ret	= objrel;
//		}
//		return ret;
	}

	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		// Todo... also in method extractor!?
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}
}
