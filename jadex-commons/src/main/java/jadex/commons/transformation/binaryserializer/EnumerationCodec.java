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
public class EnumerationCodec implements ITraverseProcessor, IDecoderHandler
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
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
	{
		Vector vec = new Vector();
		int length = (int) context.readVarInt();
		int count = 0;
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
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		EncodingContext ec = (EncodingContext) context;
		
		object = ec.runPreProcessors(object, clazz, processors, traverser, traversed, clone, context);
		clazz = object == null? null : object.getClass();
		
		Enumeration en = (Enumeration)object;
		
		ec.writeClass(clazz);
		
		int count = 0;
		Vector copy = new Vector();
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			++count;
			copy.add(val);
		}
		
		ec.write(VarInt.encode(count));
		
		count = 0;
		for (Object val : copy)
		{
			if (val != null)
			{
				ec.write(VarInt.encode(count));
				Class valclazz = val!=null? val.getClass(): null;
				traverser.traverse(val, valclazz, traversed, processors, clone, context);
			}
			++count;
		}
		
		Enumeration ret = copy.elements();
		
		traversed.put(object, ret);
		
		return ret;
	}
}
