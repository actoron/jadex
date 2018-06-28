package jadex.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Class<?> clazz)
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
	public Object createObject(Class<?> clazz, IDecodingContext context)
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
			if(SReflect.isSupertype(Set.class, clazz))
			{
				// Using linked hash set as default to avoid loosing order if has order.
				coll = new LinkedHashSet();
			}
			else //if(isSupertype(List.class, clazz))
			{
				coll = new ArrayList();
			}
//			throw new RuntimeException(e);
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
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		Collection coll = (Collection) object;
		int length = (int) context.readVarInt();
		for (int i = 0; i < length; ++i)
		{
			Object element = SBinarySerializer.decodeObject(context);
			coll.add(element);
		}
		return coll;
	}
	
//	/**
//	 *  Test if the processor is applicable.
//	 *  @param object The object.
//	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
//	 *    e.g. by cloning the object using the class loaded from the target class loader.
//	 *  @return True, if is applicable. 
//	 */
//	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
//	{
//		Class<?> clazz = SReflect.getClass(type);
//		return SReflect.isSupertype(Collection.class, clazz);
//	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		ec.writeVarInt(((Collection) object).size());
		
		Collection col = (Collection)object;

		try
		{
			for(Iterator<Object> it=col.iterator(); it.hasNext(); )
			{
				Object val = it.next();
				if (val != null)
				{
					Class valclazz = val.getClass();
					traverser.doTraverse(val, valclazz, preprocessors, processors, mode, targetcl, ec);
				}
				else
				{
					ec.writeClassname(SBinarySerializer.NULL_MARKER);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("thread trav: "+Thread.currentThread());
			e.printStackTrace();
		}
		
		return object;
	}
}
