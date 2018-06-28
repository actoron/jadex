package jadex.binary;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

public class LRUCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(LRU.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		LRU ret = null;
		try
		{
			ret = new LRU();
		}
		catch(Exception e)
		{
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
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
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		LRU ret = (LRU) object;
		
		int maxentries = (int) context.readVarInt();
		ret.setMaxEntries(maxentries);
		
		ILRUEntryCleaner cleaner = (ILRUEntryCleaner) SBinarySerializer.decodeObject(context);
		ret.setCleaner(cleaner);
		
		int size = (int) context.readVarInt();
		for (int i = 0; i < size; ++i)
		{
			Object key = SBinarySerializer.decodeObject(context);
			Object value = SBinarySerializer.decodeObject(context);
			ret.put(key, value);
		}
		
		return ret;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		ec.writeVarInt(((LRU) object).getMaxEntries());
		ILRUEntryCleaner cleaner = ((LRU) object).getCleaner();
		if(cleaner == null)
		{
			ec.writeClassname(SBinarySerializer.NULL_MARKER);
		}
		else
		{
			traverser.doTraverse(cleaner, cleaner.getClass(), preprocessors, processors, mode, targetcl, ec);
		}
		ec.writeVarInt(((LRU) object).size());
		
		Set entries = ((LRU) object).entrySet();
		for (Iterator<Map.Entry> it = entries.iterator(); it.hasNext(); )
		{
			Map.Entry entry = it.next();
			Object ev = entry.getKey();
			if (ev == null)
			{
				ec.writeClassname(SBinarySerializer.NULL_MARKER);
				//BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, ec);
			}
			else
			{
				traverser.doTraverse(ev, ev.getClass(), preprocessors, processors, mode, targetcl, ec);
			}
			
			ev = entry.getValue();
			if (ev == null)
			{
				ec.writeClassname(SBinarySerializer.NULL_MARKER);
				//BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, ec);
			}
			else
			{
				traverser.doTraverse(ev, ev.getClass(), preprocessors, processors, mode, targetcl, ec);
			}
		}
		
		return object;
	}
}
