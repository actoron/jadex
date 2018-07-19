package jadex.commons.transformation.traverser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.collection.WeakObject;
import jadex.commons.transformation.annotations.Exclude;
import jadex.commons.transformation.annotations.Include;
import jadex.commons.transformation.annotations.IncludeFields;


/**
 * Introspector for Java beans. It uses the reflection to build up a map with
 * property infos (name, read/write method, etc.)
 */
public class DefaultBeanIntrospector implements IBeanIntrospector
{
	// -------- attributes --------
	
//	/** Flag for excluding getters. */
//	protected static final byte EXCLUDE_GETTER = 1;
//	
//	/** Flag for excluding setters. */
//	protected static final byte EXCLUDE_SETTER = 2;

	/** The cache for saving time for multiple lookups. */
	protected LRU<WeakObject<Class<?>>, Map<Tuple2<Boolean, Boolean>, BeanClassInfo>> beaninfos;

	// -------- constructors --------

	/**
	 * Create a new introspector.
	 */
	public DefaultBeanIntrospector()
	{
		this(20000);
	}

	/**
	 * Create a new introspector.
	 */
	public DefaultBeanIntrospector(int lrusize)
	{
		this.beaninfos = new LRU<>(lrusize);
	}

	// -------- methods --------
	
	/**
	 * Get the bean constructor for a specific clazz.
	 */
	public MethodHandle getBeanConstructor(Class<?> clazz, boolean includemethods, boolean includefields)
	{
		return getBeanClassInfo(clazz, includemethods, includefields).getBeanConstructor();
	}

	/**
	 * Get the bean properties for a specific clazz.
	 */
	public Map<String, BeanProperty> getBeanProperties(Class<?> clazz, boolean includemethods, boolean includefields)
	{
		return getBeanClassInfo(clazz, includemethods, includefields).getProperties();
	}
	
	protected BeanClassInfo getBeanClassInfo(Class<?> clazz, boolean includemethods, boolean includefields)
	{
		BeanClassInfo ret = null;
		// includefields component of key is call based to avoid reflection calls during cache hits.
		WeakObject<Class<?>> classkey = new WeakObject<Class<?>>(clazz);
		Tuple2<Boolean, Boolean> beaninfokey = new Tuple2<Boolean, Boolean>(includemethods, includefields);
		
		try
		{
			synchronized(beaninfos)
			{
				Map<Tuple2<Boolean, Boolean>, BeanClassInfo> classmap = beaninfos.get(classkey);
				if (classmap != null)
					ret = classmap.get(beaninfokey);
			}
			
			if(ret == null)
			{
				Map<String, BeanProperty> beanprops = null;
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
				beanprops = new HashMap<String, BeanProperty>();
	
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
							beanprops.put(propname, createBeanProperty(propname, getter.getReturnType(), getter, setter, setter.getParameterTypes()[0], getter.getGenericReturnType()));
						}
					}
				}
				
				// Get all public fields.
				Field[] fields = clazz.getFields();
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if((includefields || fields[i].isAnnotationPresent(Include.class)) 
						&& fields[i].getAnnotation(Exclude.class) == null && !beanprops.containsKey(property_java_name))
					{
						beanprops.put(property_java_name, createBeanProperty(property_java_name, fields[i], false));
					}
				}

				// Get all private fields (and include if requested)
				fields = SReflect.getAllFields(clazz);
				for(int i = 0; i < fields.length; i++)
				{
					String property_java_name = fields[i].getName();
					if(((includefields && includePrivateFields)|| fields[i].isAnnotationPresent(Include.class))
							&& fields[i].getAnnotation(Exclude.class) == null && !beanprops.containsKey(property_java_name))
					{
						beanprops.put(property_java_name, createBeanProperty(property_java_name, fields[i], false));
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
							if(!beanprops.containsKey(property_java_name))
							{
								beanprops.put(property_java_name, createBeanProperty(property_java_name, fields[i], true));
							}
						}
					}
				}
				
				Constructor<?> refcon = null;
				try
				{
					refcon = clazz.getDeclaredConstructor();
					refcon.setAccessible(true);
				}
				catch (Exception e)
				{
				}
				
				MethodHandle beanconstructor = null;
				if (refcon != null)
					beanconstructor = MethodHandles.lookup().unreflectConstructor(refcon).asType(MethodType.genericMethodType(0));
				
				ret = new BeanClassInfo(beanconstructor, beanprops);
	
				// Todo: find a way to make lru keys and contents weak.
				// Update: found one, at least partially
				synchronized(beaninfos)
				{
					Map<Tuple2<Boolean, Boolean>, BeanClassInfo> classmap = beaninfos.get(classkey);
					if (classmap == null)
					{
						classmap = new HashMap<>();
						beaninfos.put(classkey, classmap);
					}
					classmap.put(beaninfokey, ret);
				}
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
		return new BeanProperty(name, type, getter, setter, settertype, !getter.isAnnotationPresent(Exclude.class), !setter.isAnnotationPresent(Exclude.class), generictype);
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
		return new BeanProperty(name, field);
	}
	
	/**
	 *  Infos about a bean class.
	 *
	 */
	protected static class BeanClassInfo
	{
		/** The bean constructor. */
		protected MethodHandle beanconstructor;
		
		/** The bean properties. */
		protected Map<String, BeanProperty> properties;
		
		/**
		 *  Creates the info.
		 */
		public BeanClassInfo(MethodHandle beanconstructor, Map<String, BeanProperty> properties)
		{
			this.beanconstructor = beanconstructor;
			this.properties = properties;
		}
		
		/**
		 *  Gets the bean constructor.
		 *  @return The bean constructor.
		 */
		public MethodHandle getBeanConstructor()
		{
			return beanconstructor;
		}
		
		/**
		 *  Gets the bean properties.
		 *  @return The bean properties.
		 */
		public Map<String, BeanProperty> getProperties()
		{
			return properties;
		}
	}
}