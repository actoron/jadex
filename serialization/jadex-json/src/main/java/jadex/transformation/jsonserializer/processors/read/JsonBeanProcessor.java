package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Bean processor for reading json objects.
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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return object instanceof JsonObject && (clazz!=null && !SReflect.isSupertype(Map.class, clazz));
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
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
			traverseProperties(object, clazz, conversionprocessors, processors, mode, traverser, targetcl, ret, context, intro);
		}
		catch(Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		
		return ret;
	}
	
	/**
	 *  Clone all properties of an object.
	 */
	protected static void traverseProperties(Object object, Type type, List<ITraverseProcessor> postprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, Object ret, Object context, IBeanIntrospector intro)
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
					JsonValue val = jval.get(name);
					if(val!=null && !val.isNull()) 
					{
						Type sot = val instanceof JsonObject?JsonTraverser.findClazzOfJsonObject((JsonObject) val, targetcl):prop.getGenericType();
//						System.out.println("VAL " + ((JsonObject) val).toString());
//						System.out.println("CL " + ((JsonObject) val).getString(JsonTraverser.CLASSNAME_MARKER, null));
//						System.out.println("SOT: " +sot);
						Object newval = traverser.doTraverse(val, sot, postprocessors, processors, mode, targetcl, context);
//						Object newval = traverser.doTraverse(val, sot, rsionprocessors, postprocessors, mode, targetcl, context)
//						Object newval = traverser.doTraverse(val, sot, cloned, null, processors, postprocessors, clone, targetcl, context);

						if(newval != Traverser.IGNORE_RESULT && (object!=ret || val!=newval))
						{
//							if ("result".equals(prop.getName()))
//								System.out.println("PROP SET CALLED");
							
							prop.setPropertyValue(ret, convertBasicType(newval, prop.getType()));
						}
					}
				}
			}
			catch(Exception e)
			{
				throw SUtil.throwUnchecked(e);
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
//				throw SUtil.throwUnchecked(e);
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
		if(value!=null && !SReflect.isSupertype(targetclazz, value.getClass()))
		{
			// Autoconvert basic from string
			if(value instanceof String)
			{
//				IStringObjectConverter conv = BasicTypeConverter.getBasicStringConverter(targetclazz);
				IStringObjectConverter conv = BasicTypeConverter.getExtendedStringConverter(targetclazz);
				if(conv!=null)
				{
					try
					{
						value = conv.convertString((String)value, null);
					}
					catch(Exception e)
					{
						SUtil.rethrowAsUnchecked(e);
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
						SUtil.rethrowAsUnchecked(e);
					}
				}
			}
		}
		
		return value;
	}
}
