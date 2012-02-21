package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapCodec implements ITraverseProcessor, IDecoderHandler
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(Map.class, clazz);
	}
	
	/**
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
	{
		Map ret = null;
		try
		{
			if (Collections.EMPTY_MAP.getClass().equals(clazz))
				ret = Collections.EMPTY_MAP;
			else
				ret = (Map) clazz.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		int size = (int) context.readVarInt();
		for (int i = 0; i < size; ++i)
		{
			Object key = BinarySerializer.decodeObject(context);
			Object value = BinarySerializer.decodeObject(context);
			ret.put(key, value);
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
		
		ec.writeClass(clazz);
		ec.write(VarInt.encode(((Map) object).size()));
		
		Set entries = ((Map) object).entrySet();
		for (Iterator<Map.Entry> it = entries.iterator(); it.hasNext(); )
		{
			Map.Entry entry = it.next();
			Object ev = entry.getKey();
			Class evclass = ev!=null? ev.getClass(): null;
			if (ev == null)
				BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, context);
			else
				traverser.traverse(ev, evclass, traversed, processors, clone, context);
			
			ev = entry.getValue();
			evclass = ev!=null? ev.getClass(): null;
			if (ev == null)
				BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, context);
			else
				traverser.traverse(ev, evclass, traversed, processors, clone, context);
		}
		
		return object;
	}
}
