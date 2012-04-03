package jadex.commons.transformation.traverser;

import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.annotations.Exclude;
import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Introspector for Java beans. It uses the reflection to build up a map with
 * property infos (name, read/write method, etc.)
 */
public class BeanReflectionIntrospector implements IBeanIntrospector
{
	// -------- attributes --------

	/** The cache for saving time for multiple lookups. */
	protected LRU	beaninfos;

	// -------- constructors --------

	/**
	 * Create a new introspector.
	 */
	public BeanReflectionIntrospector()
	{
		this(200);
	}

	/**
	 * Create a new introspector.
	 */
	public BeanReflectionIntrospector(int lrusize)
	{
		this.beaninfos = new LRU(lrusize);
	}

	// -------- methods --------

	/**
	 * Get the bean properties for a specific clazz.
	 */
	public Map getBeanProperties(Class clazz, boolean includefields)
	{
		// includefields component of key is call based to avoid reflection calls during cache hits.
		Tuple2<Class, Boolean> beaninfokey = new Tuple2<Class, Boolean>(clazz, includefields);
		Map ret = (Map)beaninfos.get(beaninfokey);
		
		if(ret == null)
		{
			if (clazz.getAnnotation(IncludeFields.class) != null)
				includefields = true;
			try
			{
				Field incfield = clazz.getField("INCLUDE_FIELDS");
				if (incfield.getBoolean(null))
					includefields = true;
			}
			catch (Exception e)
			{
			}
			
			Method[] ms = clazz.getMethods();
			HashMap getters = new HashMap();
			ArrayList setters = new ArrayList();
			for(int i = 0; i < ms.length; i++)
			{
				String method_name = ms[i].getName();
				if((method_name.startsWith("is") || method_name.startsWith("get"))
					&& ms[i].getParameterTypes().length == 0)
				{
					getters.put(method_name, ms[i]);
				}
				else if(method_name.startsWith("set")
					&& ms[i].getParameterTypes().length == 1)
				{
					setters.add(ms[i]);
				}
			}

			ret = new HashMap();
			Iterator it = setters.iterator();

			while(it.hasNext())
			{
				Method setter = (Method)it.next();
				String setter_name = setter.getName();
				String property_name = setter_name.substring(3);
				Method getter = (Method)getters.get("get" + property_name);
				if(getter == null)
					getter = (Method)getters.get("is" + property_name);

				if(getter != null)
				{
					Class[] setter_param_type = setter.getParameterTypes();
					String property_java_name = Character.toLowerCase(property_name.charAt(0))
						+ property_name.substring(1);
					
					Annotation exclude = null;
					try
					{
						exclude = clazz.getField(property_name).getAnnotation(Exclude.class);
					}
					catch(NoSuchFieldException e)
					{
					}
					
					if (exclude == null)
					{
						ret.put(property_java_name, createBeanProperty(property_java_name, 
							getter.getReturnType(), getter, setter, setter_param_type[0]));
					}
				}
			}

			// Get all public fields.
			Field[] fields = clazz.getFields();
			for(int i = 0; i < fields.length; i++)
			{
				String property_java_name = fields[i].getName();
				if((includefields || fields[i].getAnnotation(Include.class) != null) && fields[i].getAnnotation(Exclude.class) == null && !ret.containsKey(property_java_name))
				{
					ret.put(property_java_name, createBeanProperty(property_java_name, fields[i]));
				}
			}
			
			/*if(includefields)
			{
				Field[] fields = clazz.getFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(!ret.containsKey(property_java_name))
					{
						ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
					}
				}
			}*/
//			else
//			{
//				Field[] fields = clazz.getFields();
//				for(int i = 0; i < fields.length; i++)
//				{
//					String property_java_name = fields[i].getName();
//					if(!ret.containsKey(property_java_name))
//					{
//						ret.put(property_java_name, new BeanProperty(property_java_name, fields[i]));
//					}
//				}
//			}

			// Get final values (val$xyz fields) for anonymous classes.
			if(clazz.isAnonymousClass())
			{
				fields = clazz.getDeclaredFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(property_java_name.startsWith("val$"))
					{
						property_java_name = property_java_name.substring(4);
						if(!ret.containsKey(property_java_name))
						{
							ret.put(property_java_name, createBeanProperty(property_java_name, fields[i]));
						}
					}
				}
			}

			beaninfos.put(beaninfokey, ret);
		}

		return ret;
	}
	
	/**
	 *  Creates a bean property based on getter/setter.
	 *  
	 *  @param name Property name
	 *  @param type Property type.
	 *  @param getter The getter method.
	 *  @param setter The setter method.
	 *  @param settertype The type used by the setter.
	 *  @return The bean property.
	 */
	protected BeanProperty createBeanProperty(String name, Class type, Method getter, Method setter, Class settertype)
	{
		return new BeanProperty(name, type, getter, setter, settertype, null);
	}
	
	/**
	 *  Creates a bean property based on a field.
	 * 
	 *  @param name Property name
	 *  @param field The field.
	 *  @return The bean property.
	 */
	protected BeanProperty createBeanProperty(String name, Field field)
	{
		return new BeanProperty(name, field, null);
	}
}