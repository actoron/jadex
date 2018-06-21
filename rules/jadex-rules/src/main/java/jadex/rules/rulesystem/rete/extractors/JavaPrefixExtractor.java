package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaAttributeType;


/**
 *  Extractor for a Java attribute value.
 */
public class JavaPrefixExtractor extends ObjectExtractor
{
	//-------- constructors --------
	
	/**
	 *  Create a new extractor.
	 */
	public JavaPrefixExtractor(OAVJavaAttributeType attr)
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
		return ((OAVJavaAttributeType)attr).accessProperty(prefix);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return ".[java]" + attr.getName();
	}
}
