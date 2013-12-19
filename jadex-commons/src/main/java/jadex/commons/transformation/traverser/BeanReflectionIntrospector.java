package jadex.commons.transformation.traverser;

import jadex.commons.Tuple3;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.annotations.Exclude;
import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
	
	/** Flag for excluding getters. */
	protected static final byte EXCLUDE_GETTER = 1;
	
	/** Flag for excluding setters. */
	protected static final byte EXCLUDE_SETTER = 2;

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
		if(Proxy.isProxyClass(clazz))
		{
			System.out.println("sdfkljgosdkj");
		}
		
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
//					Method excludedmethod = getClass().getMethod("excludedMethod", new Class<?>[0]);
					Method[] ms = clazz.getMethods();
//					HashMap getters = new HashMap();
//					ArrayList setters = new ArrayList();
					Map<String, Tuple3<Method, Method, Byte>> methodprops = new HashMap<String, Tuple3<Method, Method, Byte>>();
					for(int i = 0; i < ms.length; i++)
					{
						String method_name = ms[i].getName();
//						if((method_name.startsWith("is") || method_name.startsWith("get"))
//							&& ms[i].getParameterTypes().length == 0)
//						{
//							getters.put(method_name, ms[i]);
//						}
//						else if(method_name.startsWith("set")
//							&& ms[i].getParameterTypes().length == 1)
//						{
//							setters.add(ms[i]);
//						}
						if(ms[i].getParameterTypes().length == 0)
						{
							String pname = null;
							if(method_name.startsWith("is"))
							{
								pname = method_name.substring(2);
							}
							if(method_name.startsWith("get"))
							{
								pname = method_name.substring(3);
							}
							if (pname != null)
							{
								Tuple3<Method, Method, Byte> t = methodprops.get(pname);
								Method setter = t == null? null : t.getSecondEntity();
								Method getter = ms[i];
								byte exclude = t != null? t.getThirdEntity() : 0;
								if (ms[i].isAnnotationPresent(Exclude.class))
								{
									exclude |= EXCLUDE_GETTER;
								}
								t = new Tuple3<Method, Method, Byte>(getter, setter, exclude);
								methodprops.put(pname, t);
							}
						}
						else if(method_name.startsWith("set")
								&& ms[i].getParameterTypes().length == 1)
						{
							String pname = method_name.substring(3);
							Tuple3<Method, Method, Byte> t = methodprops.get(pname);
							Method getter = t == null? null : t.getFirstEntity();
							Method setter = ms[i];
							byte exclude = t != null? t.getThirdEntity() : 0;
							if (ms[i].isAnnotationPresent(Exclude.class))
							{
								exclude |= EXCLUDE_SETTER;
							}
							t = new Tuple3<Method, Method, Byte>(getter, setter, exclude);
							methodprops.put(pname, t);
						}
					}
		
					Iterator<Map.Entry<String, Tuple3<Method, Method, Byte>>> it = methodprops.entrySet().iterator();
		
					while(it.hasNext())
					{
						Map.Entry<String, Tuple3<Method, Method, Byte>> entry = it.next();
						Tuple3<Method, Method, Byte> gettersetter = entry.getValue();
						if (gettersetter.getFirstEntity() != null && gettersetter.getSecondEntity() != null)
						{
							Method getter = gettersetter.getFirstEntity();
							Method setter = gettersetter.getSecondEntity();
							byte exclude = gettersetter.getThirdEntity();
							String property_name = entry.getKey();
							Class<?> setter_param_type = setter != null? setter.getParameterTypes()[0] : null;
							
							String property_java_name = Character.toLowerCase(property_name.charAt(0)) + property_name.substring(1);
							if ((exclude & EXCLUDE_GETTER) == 0 || (exclude & EXCLUDE_SETTER) == 0)
							{
								ret.put(property_java_name, createBeanProperty(property_java_name, 
										getter != null? getter.getReturnType() : null, getter,
										setter, setter_param_type, exclude));
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
	protected BeanProperty createBeanProperty(String name, Class type, Method getter, Method setter, Class settertype, byte exclude)
	{
		return new BeanProperty(name, type, getter, setter, settertype, null, (exclude & EXCLUDE_GETTER) == 0, (exclude & EXCLUDE_SETTER) == 0);
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