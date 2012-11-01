package jadex.commons.transformation.traverser;

import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
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
	public Map getBeanProperties(Class clazz, boolean includemethods, boolean includefields)
	{
		// includefields component of key is call based to avoid reflection calls during cache hits.
		Tuple3<Class, Boolean, Boolean> beaninfokey = new Tuple3<Class, Boolean, Boolean>(clazz, includemethods, includefields);
		Map ret = null;
		try
		{
			ret = (Map)beaninfos.get(beaninfokey);
			
			if(ret == null)
			{
				if(clazz.isAnnotationPresent(IncludeFields.class))
				{
					includefields = true;
				}
				if(!includefields)
				{
					try
					{
						Field incfield = clazz.getField("INCLUDE_FIELDS");
						if(incfield.getBoolean(null))
							includefields = true;
					}
					catch (Exception e)
					{
					}
				}
				
				// todo: allow including single method pairs
				ret = new HashMap();
	
				if(includemethods)
				{
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
							
							boolean exclude = false;
							try
							{
								Field f = clazz.getField(property_name);
								exclude = f.isAnnotationPresent(Exclude.class);
							}
							catch(NoSuchFieldException e)
							{
							}
							if(!exclude)
							{
								exclude = getter.isAnnotationPresent(Exclude.class)
									|| setter.isAnnotationPresent(Exclude.class);
							}

							if(!exclude)
							{
								ret.put(property_java_name, createBeanProperty(property_java_name, 
									getter.getReturnType(), getter, setter, setter_param_type[0]));
							}
						}
					}
				}
				
				// Get all public fields.
				Field[] fields = clazz.getFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if((includefields || fields[i].isAnnotationPresent(Include.class)) 
						&& fields[i].getAnnotation(Exclude.class) == null && !ret.containsKey(property_java_name))
					{
						ret.put(property_java_name, createBeanProperty(property_java_name, fields[i], false));
					}
				}
				
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
								ret.put(property_java_name, createBeanProperty(property_java_name, fields[i], true));
							}
						}
					}
				}
	
				beaninfos.put(beaninfokey, ret);
			}
		}
		catch(Throwable t)
		{
			System.out.println("err: "+clazz);
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
	protected BeanProperty createBeanProperty(String name, Field field, boolean anonclass)
	{
		return new BeanProperty(name, field, null);
	}
}