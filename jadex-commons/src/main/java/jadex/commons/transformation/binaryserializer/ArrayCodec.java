package jadex.commons.transformation.binaryserializer;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding arrays.
 *
 */
public class ArrayCodec implements ITraverseProcessor, IDecoderHandler
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return clazz.isArray();
	}
	
	/**
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
	{
		int length = (int) context.readVarInt();
		
		Class compclazz = clazz.getComponentType();
		Object ret = Array.newInstance(compclazz, length);
		boolean rawmode = compclazz.isPrimitive();
		
		for (int i = 0; i < length; ++i)
		{
			boolean ignoreclass = rawmode;
			if (!rawmode)
				ignoreclass = context.readBool();
			
			Object sub = null;
			if (ignoreclass)
				sub = BinarySerializer.decodeRawObject(compclazz, context);
			else
				sub = BinarySerializer.decodeObject(context);
			Array.set(ret, i, sub);
		}
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return clazz.isArray();
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
		
		ec.writeClass(clazz);
		Class compclazz = clazz.getComponentType();
		
		// Class name is implicit with primitives, no need to write it.
		boolean rawmode = compclazz.isPrimitive();
		
		//int dim = SUtil.getArrayDimension(object);
		//ec.write(VarInt.encode(dim));
		int length = Array.getLength(object);
		ec.write(VarInt.encode(length));
		
		Class type = clazz.getComponentType();
		for(int i=0; i<length; i++) 
		{
			Object val = Array.get(object, i);
			if (val == null)
			{
				ec.writeBoolean(false);
				BinarySerializer.NULL_HANDLER.process(val, type, null, null, null, false, context);
			}
			else
			{
				boolean ignoreclass = rawmode;
				
				if (!rawmode)
				{
					ignoreclass = val.getClass().equals(compclazz);
					ec.writeBoolean(ignoreclass);
				}
				if (ignoreclass)
					ec.ignoreNextClassWrite();
				traverser.traverse(val, type, traversed, processors, clone, context);
			}
		}
		
		return object;
	}
}
