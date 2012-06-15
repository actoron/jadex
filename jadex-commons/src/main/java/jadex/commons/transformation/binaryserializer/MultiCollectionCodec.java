package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class MultiCollectionCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(MultiCollection.class, clazz);
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
		MultiCollection ret = null;
		try
		{
			//FIXME: Separation of sub-object decoding not possible with current MultiMap interface?
			Map map = (Map) BinarySerializer.decodeObject(context);
			Class type = SReflect.classForName(context.readClassname(), context.getClassloader());
			
			if(MultiCollection.class.equals(clazz))
			{
				ret = new MultiCollection(map, type);
			}
			else
			{
				// use reflection due to subclasses
				Constructor c = clazz.getConstructor(new Class[] { Map.class, Class.class } );
				ret = (MultiCollection) c.newInstance(new Object[] { map, type });
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
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
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		MultiCollection mc = (MultiCollection) object;
		try
		{
			Field mapfield = MultiCollection.class.getDeclaredField("map");
			mapfield.setAccessible(true);
			Map map = (Map) mapfield.get(mc);
			traverser.traverse(map, map.getClass(), traversed, processors, clone, null, ec);
			
			Field typefield = MultiCollection.class.getDeclaredField("type");
			typefield.setAccessible(true);
			Class type = (Class) typefield.get(mc);
			ec.writeClass(type);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return object;
	}
}
