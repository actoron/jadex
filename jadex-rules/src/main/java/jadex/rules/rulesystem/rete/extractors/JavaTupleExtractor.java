package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaAttributeType;

/**
 *  Extractor for fetching a Java value from a rete tuple.
 */
public class JavaTupleExtractor extends TupleExtractor
{
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public JavaTupleExtractor(int tupleindex, OAVJavaAttributeType attr)
	{
		super(tupleindex, attr);
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

		// a) attr == null -> use object
		// b) attr !=null -> use object.getXYZ()
		
		Object object = left.getObject(tupleindex);
		
		if(object instanceof VirtualFact)
			object = ((VirtualFact)object).getObject();
				
		if(attr!=null)
		{
			object = ((OAVJavaAttributeType)attr).accessProperty(object);
		}
		
		return object;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return "[java]"+"["+tupleindex+"]"+"."+(attr==null? "object": attr.getName());
	}
}
