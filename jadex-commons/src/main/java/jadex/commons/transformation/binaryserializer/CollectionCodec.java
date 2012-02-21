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
public class CollectionCodec implements IDecoderHandler, ITraverseProcessor
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
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class clazz, DecodingContext context)
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
		int length = (int) context.readVarInt();
		while (coll.size() < length)
		{
			int index = (int) context.readVarInt();
			while (coll.size() < index)
				coll.add(null);
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
		ec.write(VarInt.encode(((Collection) object).size()));
		
		Collection col = (Collection)object;

		traversed.put(object, object);
		int count = 0;
		try
		{
			for(Iterator<Object> it=col.iterator(); it.hasNext(); )
			{
				Object val = it.next();
				if (val != null)
				{
					ec.write(VarInt.encode(count));
					Class valclazz = val.getClass();
					traverser.traverse(val, valclazz, traversed, processors, clone, context);
				}
				++count;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return object;
	}
}
