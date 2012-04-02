package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding collections.
 *
 */
public class CollectionCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(Collection.class, clazz);
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
		Collection coll = null;
		try
		{
			if (Collections.EMPTY_LIST.getClass().equals(clazz))
				coll = Collections.EMPTY_LIST;
			else if (Collections.EMPTY_SET.getClass().equals(clazz))
				coll = Collections.EMPTY_SET;
			else
				coll = (Collection) clazz.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return coll;
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
		Collection coll = (Collection) object;
		int length = (int) context.readVarInt();
		for (int i = 0; i < length; ++i)
		{
			Object element = BinarySerializer.decodeObject(context);
			coll.add(element);
		}
		return coll;
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return SReflect.isSupertype(Collection.class, clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		ec.writeVarInt(((Collection) object).size());
		
		Collection col = (Collection)object;

		traversed.put(object, object);
		try
		{
			for(Iterator<Object> it=col.iterator(); it.hasNext(); )
			{
				Object val = it.next();
				if (val != null)
				{
					Class valclazz = val.getClass();
					traverser.traverse(val, valclazz, traversed, processors, clone, ec);
				}
				else
				{
					ec.writeClassname(BinarySerializer.NULL_MARKER);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return object;
	}
}
