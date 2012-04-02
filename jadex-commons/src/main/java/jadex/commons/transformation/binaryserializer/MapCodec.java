package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapCodec extends AbstractCodec
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
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class clazz, DecodingContext context)
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
		
		return ret;
	}
	
	/**
	 *  Decodes and adds sub-objects during decoding.
	 *  
	 *  @param object The instantiated object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The finished object.
	 */
	public Object decodeSubObjects(Object object, Class clazz, DecodingContext context)
	{
		Map ret = (Map) object;
		
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
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		ec.writeVarInt(((Map) object).size());
		
		Set entries = ((Map) object).entrySet();
		for (Iterator<Map.Entry> it = entries.iterator(); it.hasNext(); )
		{
			Map.Entry entry = it.next();
			Object ev = entry.getKey();
			if (ev == null)
			{
				ec.writeClassname(BinarySerializer.NULL_MARKER);
				//BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, ec);
			}
			else
			{
				traverser.traverse(ev, ev.getClass(), traversed, processors, clone, ec);
			}
			
			ev = entry.getValue();
			if (ev == null)
			{
				ec.writeClassname(BinarySerializer.NULL_MARKER);
				//BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, ec);
			}
			else
			{
				traverser.traverse(ev, ev.getClass(), traversed, processors, clone, ec);
			}
		}
		
		return object;
	}
}
