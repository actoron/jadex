package jadex.commons.transformation.binaryserializer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

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
		
		ILRUEntryCleaner cleaner = (ILRUEntryCleaner) BinarySerializer.decodeObject(context);
		ret.setCleaner(cleaner);
		
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
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		ec.writeVarInt(((LRU) object).getMaxEntries());
		ILRUEntryCleaner cleaner = ((LRU) object).getCleaner();
		if(cleaner == null)
		{
			ec.writeClassname(BinarySerializer.NULL_MARKER);
		}
		else
		{
			traverser.doTraverse(cleaner, cleaner.getClass(), traversed, processors, clone, null, ec);
		}
		ec.writeVarInt(((LRU) object).size());
		
		Set entries = ((LRU) object).entrySet();
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
				traverser.doTraverse(ev, ev.getClass(), traversed, processors, clone, null, ec);
			}
			
			ev = entry.getValue();
			if (ev == null)
			{
				ec.writeClassname(BinarySerializer.NULL_MARKER);
				//BinarySerializer.NULL_HANDLER.process(null, null, processors, traverser, traversed, clone, ec);
			}
			else
			{
				traverser.doTraverse(ev, ev.getClass(), traversed, processors, clone, null, ec);
			}
		}
		
		return object;
	}
}
