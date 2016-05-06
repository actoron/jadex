package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonBeanProcessor implements ITraverseProcessor
{
	
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(5000);
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return object instanceof JsonObject && (clazz!=null && !SReflect.isSupertype(Map.class, clazz));
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		Object ret = null;
		Class<?> clazz = SReflect.getClass(type);
		
		ret = getReturnObject(object, clazz, targetcl);
//		traversed.put(object, ret);
		
		JsonValue idx = (JsonValue)((JsonObject)object).get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		try
		{
			traverseProperties(object, clazz, traversed, processors, traverser, clone, targetcl, ret, context, intro);
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Clone all properties of an object.
	 */
	protected static void traverseProperties(Object object, Type type, Map<Object, Object> cloned, 
		List<ITraverseProcessor> processors, Traverser traverser, boolean clone, ClassLoader targetcl, 
		Object ret, Object context, IBeanIntrospector intro)
	{
		// Get all declared fields (public, protected and private)
		
		JsonObject jval = (JsonObject)object;
		Class<?> clazz = SReflect.getClass(type);
		Map<String, BeanProperty> props = intro.getBeanProperties(clazz, true, false);
		
		for(String name: jval.names())
		{
			try
			{
				BeanProperty prop = (BeanProperty)props.get(name);
				if(prop!=null && prop.isReadable() && prop.isWritable())
				{
					Object val = jval.get(name);
					if(val!=null) 
					{
						Object newval = traverser.doTraverse(val, prop.getGenericType(), cloned, processors, clone, targetcl, context);
						if(newval != Traverser.IGNORE_RESULT && (object!=ret || val!=newval))
						{
							prop.setPropertyValue(ret, convertBasicType(newval, prop.getType()));
						}
					}
				}
			}
			catch(Exception e)
			{
				throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
			}
		}
		
//		for(Iterator<String> it=props.keySet().iterator(); it.hasNext(); )
//		{
//			try
//			{
//				String name = (String)it.next();
//				BeanProperty prop = (BeanProperty)props.get(name);
//				if(prop.isReadable() && prop.isWritable())
//				{
//					Object val = jval.get(name);
//					if(val!=null) 
//					{
//						Object newval = traverser.doTraverse(val, prop.getGenericType(), cloned, processors, clone, targetcl, context);
//						if(newval != Traverser.IGNORE_RESULT && (object!=ret || val!=newval))
//						{
//							prop.setPropertyValue(ret, convertBasicType(newval, prop.getType()));
//						}
//					}
//				}
//			}
//			catch(Exception e)
//			{
//				throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
//			}
//		}
	}
	
	/**
	 *  Get the object that is returned.
	 */
	public Object getReturnObject(Object object, Class<?> clazz, ClassLoader targetcl)
	{
		Object ret = null;
		
		if(targetcl!=null)
			clazz = SReflect.classForName0(clazz.getName(), targetcl);
		
		Constructor<?> c;
		
		try
		{
			c	= clazz.getConstructor(new Class[0]);
		}
		catch(NoSuchMethodException nsme)
		{
			c	= clazz.getDeclaredConstructors()[0];
		}

		try
		{
			c.setAccessible(true);
			Class<?>[] paramtypes = c.getParameterTypes();
			Object[] paramvalues = new Object[paramtypes.length];
			for(int i=0; i<paramtypes.length; i++)
			{
				if(paramtypes[i].equals(boolean.class))
				{
					paramvalues[i] = Boolean.FALSE;
				}
				else if(SReflect.isBasicType(paramtypes[i]))
				{
					paramvalues[i] = 0;
				}
			}
			ret = c.newInstance(paramvalues);
		}
		catch(Exception e)
		{
			System.out.println("beanproc ex: "+object+" "+c);
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param value
	 * @param targetclazz
	 * @return
	 */
	public static Object convertBasicType(Object value, Class<?> targetclazz)
	{
		if(!SReflect.isSupertype(targetclazz, value.getClass()))
		{
			// Autoconvert basic from string
			if(value instanceof String)
			{
				IStringObjectConverter conv = BasicTypeConverter.getBasicStringConverter(targetclazz);
				if(conv!=null)
				{
					try
					{
						value = conv.convertString((String)value, null);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			// Autoconvert basic to string
			else if(String.class.equals(targetclazz))
			{
				IObjectStringConverter conv = BasicTypeConverter.getBasicObjectConverter(value.getClass());
				if(conv!=null)
				{
					try
					{
						value = conv.convertObject(value, null);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return value;
	}
}
