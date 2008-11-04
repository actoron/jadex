package jadex.rules.rulesystem.rete.extractors;

import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.nodes.VirtualFact;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaAttributeType;

import java.lang.reflect.Method;

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
	 *  @param state The working memory.
	 */
	public Object getValue(Tuple left, Object right, IOAVState state)
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
			Method rm = ((OAVJavaAttributeType)attr).getPropertyDescriptor()
				.getReadMethod();
			if(rm==null)
				throw new RuntimeException("No attribute accessor found: "+attr);
			try
			{
				ret = rm.invoke(right, new Object[0]);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
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
