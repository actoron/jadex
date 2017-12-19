package jadex.commons.transformation.traverser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.Tuple3;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.annotations.Exclude;
import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;


/**
 * Introspector for Java beans. It uses the reflection to build up a map with
 * property infos (name, read/write method, etc.)
 */
public class BeanReflectionIntrospector implements IBeanIntrospector
{
	// -------- attributes --------
	
//	/** Flag for excluding getters. */
//	protected static final byte EXCLUDE_GETTER = 1;
//	
//	/** Flag for excluding setters. */
//	protected static final byte EXCLUDE_SETTER = 2;

	/** The cache for saving time for multiple lookups. */
	protected LRU<Tuple3<Class<?>, Boolean, Boolean>, Map<String, BeanProperty>>	beaninfos;

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
		this.beaninfos = new LRU<Tuple3<Class<?>, Boolean, Boolean>, Map<String, BeanProperty>>(lrusize);
	}

	// -------- methods --------

	/**
	 * Get the bean properties for a specific clazz.
	 */
	public Map<String, BeanProperty> getBeanProperties(Class<?> clazz, boolean includemethods, boolean includefields)
	{
		// includefields component of key is call based to avoid reflection calls during cache hits.
		Tuple3<Class<?>, Boolean, Boolean> beaninfokey = new Tuple3<Class<?>, Boolean, Boolean>(clazz, includemethods, includefields);
		Map<String, BeanProperty> ret = null;
		
		try
		{
			ret = beaninfos.get(beaninfokey);
			
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

				boolean includePrivateFields = false;
				if (includefields) {
					IncludeFields annotation = clazz.getAnnotation(IncludeFields.class);
					if (annotation != null && annotation.includePrivate()) {
						includePrivateFields = true;
					}
				}
				
				// todo: allow including single method pairs
				ret = new HashMap<String, BeanProperty>();
	
				if(includemethods)
				{
					Method[] ms = clazz.getMethods();
					Map<String, Method> getters = new LinkedHashMap<String, Method>();
					List<Method> setters = new ArrayList<Method>();
					for(int i=0; i<ms.length; i++)
					{
						String method_name = ms[i].getName();
						if(ms[i].getParameterTypes().length==0)
						{
							if(method_name.startsWith("is"))
							{
								getters.put(method_name.substring(2), ms[i]);
							}
							else if(method_name.startsWith("get"))
							{
								getters.put(method_name.substring(3), ms[i]);
							}
						}
						else if(method_name.startsWith("set")
							&& ms[i].getParameterTypes().length == 1)
						{
							setters.add(ms[i]);
						}
					}
					
					for(Method setter: setters)
					{
						String	propname	= setter.getName().substring(3);
						Method	getter	= getters.get(propname);
						if(getter!=null && getter.getReturnType().equals(setter.getParameterTypes()[0]))
						{
							propname = Character.toLowerCase(propname.charAt(0)) + propname.substring(1);
							ret.put(propname, createBeanProperty(propname, getter.getReturnType(), getter, setter, setter.getParameterTypes()[0], getter.getGenericReturnType()));
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

				// Get all private fields (and include if requested)
				fields = SReflect.getAllFields(clazz);
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(((includefields && includePrivateFields)|| fields[i].isAnnotationPresent(Include.class))
							&& fields[i].getAnnotation(Exclude.class) == null && !ret.containsKey(property_java_name))
					{
						ret.put(property_java_name, createBeanProperty(property_java_name, fields[i], false));
					}
				}
				
				// Get final values (val$xyz fields) for anonymous classes.
				if(clazz.isAnonymousClass())
				{
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
	
				// Todo: find a way to make lru keys and contents weak.
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
	protected BeanProperty createBeanProperty(String name, Class<?> type, Method getter, Method setter, Class<?> settertype, Type generictype)
	{
		return new BeanProperty(name, type, getter, setter, settertype, null, !getter.isAnnotationPresent(Exclude.class), !setter.isAnnotationPresent(Exclude.class), generictype);
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