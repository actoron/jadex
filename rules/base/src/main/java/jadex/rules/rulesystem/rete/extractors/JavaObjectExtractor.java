package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaAttributeType;


/**
 *  Extractor for a Java attribute value (or the whole object).
 */
public class JavaObjectExtractor extends ObjectExtractor
{
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public JavaObjectExtractor(OAVJavaAttributeType attr)
	{
		super(attr);
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
		// b) attr !=null -> use right.getXYZ()
		
		if(right instanceof VirtualFact)
			right = ((VirtualFact)right).getObject();
		
		Object ret;
		if(attr==null)
		{
			ret = right;
		}
		else
		{
			ret = ((OAVJavaAttributeType)attr).accessProperty(right);
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "[java]"+(attr==null? "object": attr.getName());
	}
}
