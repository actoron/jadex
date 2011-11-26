package jadex.commons;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *  The cloner is able to clone Java objects by field copying.
 *  
 *  Extension points:
 *  - ICloneProcessors: allow perform cloning by hand for certain objects
 *  - IFilter: allow keeping objects with reference semantics (filter out objects from cloning)
 */
public class Cloner
{
	//-------- static attributes --------
	
	/** The static immutable types. */
	protected static final Set immutabletypes;
	
	/** The default cloner. */
	protected static Cloner instance;
	
	static
	{
		immutabletypes = new HashSet();
		immutabletypes.add(Boolean.class);
		immutabletypes.add(boolean.class);
		immutabletypes.add(Integer.class);
		immutabletypes.add(int.class);
		immutabletypes.add(Double.class);
		immutabletypes.add(double.class);
		immutabletypes.add(Float.class);
		immutabletypes.add(float.class);
		immutabletypes.add(Long.class);
		immutabletypes.add(long.class);
		immutabletypes.add(Short.class);
		immutabletypes.add(short.class);
		immutabletypes.add(Byte.class);
		immutabletypes.add(byte.class);
		immutabletypes.add(Character.class);
		immutabletypes.add(char.class);
		immutabletypes.add(String.class);
		immutabletypes.add(Class.class);
	}
	
	//-------- attributes --------
	
//	/** The immutable types. */
//	protected Set myimmutabletypes;

	//-------- methods --------

	/**
	 *  Get the default cloner instance.
	 */
	public static Cloner getInstance()
	{
		if(instance==null)
		{
			synchronized(Cloner.class)
			{
				if(instance==null)
				{
					instance = new Cloner();
				}
			}
		}
		return instance;
	}
	
	/**
	 *  Deep clone an object.
	 */
	public static Object deepCloneObject(Object object)
	{
		return getInstance().deepClone(object, null, new HashMap(), null, null);
	}
	
	/**
	 *  Deep clone an object.
	 */
	public static Object deepCloneObject(Object object, List processors)
	{
		return getInstance().deepClone(object, null, new HashMap(), processors, null);
	}
	
	/**
	 *  Deep clone an object.
	 */
	public static <T> T deepCloneObject(T object, List processors, IFilter filter)
	{
		return (T)getInstance().deepClone(object, null, new HashMap(), processors, filter);
	}
	
	/**
	 *  Deep clone an object.
	 */
	public Object deepClone(Object object)
	{
		return deepClone(object, null, new HashMap(), null, null);
	}
	
	/**
	 *  Deep clone an object.
	 */
	public Object deepClone(Object object, List processors)
	{
		return deepClone(object, null, new HashMap(), processors, null);
	}
	
	/**
	 *  Deep clone an object.
	 */
	public Object deepClone(Object object, List processors, IFilter filter)
	{
		return deepClone(object, null, new HashMap(), processors, filter);
	}

	/**
	 *  Deep clone an object.
	 */
	public Object deepClone(Object object, Class<?> clazz, Map cloned, List processors, IFilter filter)
	{
		Object ret = null;
		
		if(object!=null)
		{
			if(clazz==null || SReflect.isSupertype(clazz, object.getClass()))
				clazz = object.getClass();
		
			boolean fin = false;
			if(isImmutable(clazz) || (filter!=null && filter.filter(object)))
			{
				ret = object;
				fin = true;
			}
			else if(cloned.containsKey(object))
			{
				ret = cloned.get(object);
				fin = true;
			}
			
			if(processors!=null)
			{
				// Todo: apply all or only first matching processor!?
				Object	processed	= object;
				for(int i=0; i<processors.size()/* && !fin*/; i++)
				{
					ICloneProcessor proc = (ICloneProcessor)processors.get(i);
					if(proc.isApplicable(processed))
					{
						processed = proc.process(processed, processors);
						ret	= processed;
						fin = true;
					}
				}
			}
			
			if(!fin && object instanceof Cloneable && !object.getClass().isArray())
			{
				try
				{
					Method	clone	= clazz.getMethod("clone", new Class[0]);
					ret	= clone.invoke(object, new Object[0]);
					fin	= true;
				}
				catch(Exception e)
				{
					throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
				}
			}
				
			if(!fin)
			{
				if(clazz.isArray()) 
				{
					int length = Array.getLength(object);
					Class type = clazz.getComponentType();
					ret = Array.newInstance(type, length);
					cloned.put(object, ret);
					for(int i=0; i<length; i++) 
					{
						Object val = Array.get(object, i);
						Array.set(ret, i, deepClone(val, type, cloned, processors, filter));
					}
				}
				else if(SReflect.isSupertype(Map.class, clazz))
				{
					try
					{
						ret = clazz.newInstance();
					}
					catch(Exception e)
					{
						ret = new HashMap();
					}
					cloned.put(object, ret);
					((Map)ret).putAll((Map)object);
				}
				else if(SReflect.isSupertype(Collection.class, clazz))
				{
					try
					{
						ret = clazz.newInstance();
					}
					catch(Exception e)
					{
						if(SReflect.isSupertype(Set.class, clazz))
						{
							ret = new HashSet();
						}
						else //if(isSupertype(List.class, clazz))
						{
							ret = new ArrayList();
						}
					}
					cloned.put(object, ret);
					((Collection)ret).addAll((Collection)object);
				}
				else if(SReflect.isSupertype(Enumeration.class, clazz))
				{
					ret = new Vector();
					cloned.put(object, ret);
					Vector target = (Vector)ret;
					for(Enumeration source = (Enumeration)object; source.hasMoreElements(); )
					{
						target.add(source.nextElement());
					}
					ret = target.elements();
				}
				else if(SReflect.isSupertype(Iterator.class, clazz))
				{
					ret = new ArrayList();
					cloned.put(object, ret);
					List target = (List)ret;
					for(Iterator source=(Iterator)object; source.hasNext(); )
					{
						target.add(source.next());
					}
					ret = target.iterator();
				}
				else
				{
					try
					{
	//					System.out.println("cloned: "+object.getClass());
						ret = object.getClass().newInstance();
						cloned.put(object, ret);
						cloneFields(object, ret, cloned, processors, filter);
					}
					catch(Exception e)
					{
						throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
					}
				}
			}
		}
			
		return ret;
	}
	
	/**
	 *  Test if a clazz is immutable.
	 *  @param clazz The clazz.
	 *  @return True, if immutable.
	 */
	public boolean isImmutable(Class clazz)
	{
		boolean ret = immutabletypes.contains(clazz);
//		if(!ret)
//		{
//			synchronized(this)
//			{
//				ret = myimmutabletypes!=null && myimmutabletypes.contains(clazz);
//			}
//		}
		return ret;
	}
	
//	/**
//	 *  Add an immutable type.
//	 *  @param clazz The class.
//	 */
//	public synchronized void addImmutableType(Class clazz)
//	{
//		if(myimmutabletypes==null)
//			myimmutabletypes = new HashSet();
//		myimmutabletypes.add(clazz);
//	}
	
	/**
	 *  Clone all fields of an object.
	 */
	protected void cloneFields(Object object, Object clone, Map cloned, List processors, IFilter filter)
	{
		Class clazz = object.getClass();
		while(clazz!=null && clazz!=Object.class) 
		{
			// Get all declared fields (public, protected and private)
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++) 
			{
				if((fields[i].getModifiers() & Modifier.STATIC) != Modifier.STATIC) 
				{
					fields[i].setAccessible(true);
					Object val = null;
					try
					{
						val = fields[i].get(object);
						if(val!=null) 
						{
							fields[i].set(clone, deepClone(val, fields[i].getType(), cloned, processors, filter));
						}
					}
					catch(Exception e)
					{
						throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
					}
				}
			}
			
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		Set test = new TreeSet();
		test.add(new Integer(2));
		test.add(new Integer(1));
		test.add(new Integer(3));
		
		System.out.println("test deep cloning: "+test+" "+deepCloneObject(test));
	}
}