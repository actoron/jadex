package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

public class MultiCollectionCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
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
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		MultiCollection<Object, Object> ret = null;
		try
		{
			if(MultiCollection.class.equals(clazz))
			{
				ret = new MultiCollection<Object, Object>();
			}
			else
			{
				// use reflection due to subclasses
				Constructor c = clazz.getConstructor(new Class[]{Map.class, Class.class});
				ret = (MultiCollection)c.newInstance();
			}
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
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		Map map = (Map)SBinarySerializer.decodeObject(context);
		String classname = context.readClassname();
		Class type = SReflect.classForName0(classname, context.getClassloader());
		if (type == null)
		{
			throw new RuntimeException("MultiCollection type not found: " + String.valueOf(classname));
		}
		
		try
		{
			Field field = SReflect.getField(clazz, "map");
			field.setAccessible(true);
			field.set(object, map);
			field = SReflect.getField(clazz, "type");
			field.setAccessible(true);
			field.set(object, type);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return object;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		MultiCollection mc = (MultiCollection) object;
		try
		{
			Field mapfield = MultiCollection.class.getDeclaredField("map");
			mapfield.setAccessible(true);
			Map map = (Map) mapfield.get(mc);
			traverser.doTraverse(map, map.getClass(), preprocessors, processors, mode, targetcl, ec);
			
			Field typefield = MultiCollection.class.getDeclaredField("type");
			typefield.setAccessible(true);
			Class type = (Class) typefield.get(mc);
			ec.writeClass(type);
		}
		catch (Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
		
		return object;
	}
}
