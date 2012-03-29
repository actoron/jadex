package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *  An enumeration processor allows for traversing enumerations.
 */
public class EnumerationCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(Enumeration.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class clazz, DecodingContext context)
	{
		Vector vec = new Vector();
		int length = (int) context.readVarInt();
		int count = 0;
		
		//FIXME: Filling in subobjects now since adding them later is not possible.
		// May not result in correct behavior but fix would require special support.
		while (count < length)
		{
			int index = (int) context.readVarInt();
			while (count < index)
				vec.add(null);
			Object element = BinarySerializer.decodeObject(context);
			vec.add(element);
		}
		return vec.elements();
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		Enumeration en = (Enumeration)object;
		
		int count = 0;
		Vector copy = new Vector();
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			++count;
			copy.add(val);
		}
		
		ec.writeVarInt(count);
		
		count = 0;
		for (Object val : copy)
		{
			if (val != null)
			{
				ec.writeVarInt(count);
				Class valclazz = val!=null? val.getClass(): null;
				traverser.traverse(val, valclazz, traversed, processors, clone, ec);
			}
			++count;
		}
		
		Enumeration ret = copy.elements();
		
		return ret;
	}
}
