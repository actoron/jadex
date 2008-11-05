package jadex.commons;

import jadex.commons.collection.SCollection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

/**
 *  This class provides several useful static reflection methods.
 */
public class SReflect
{
	//-------- attributes --------
	
	/** Class not found identifier. */
	protected static final Object	NOTFOUND	= new Object();

	/** Class lookup cache (name->class). */
	protected static Map classcache;

	/** Inner class name lookup cache. */
	protected static Map innerclassnamecache	= new WeakHashMap();

	/** Method lookup cache (class->(name->method[])). */
	protected static Map methodcache	= new WeakHashMap();

	/** Field lookup cache (class->(name->field[])). */
	protected static Map fieldcache = new WeakHashMap();

	/** Mapping from basic class name -> object type(class). */
	protected static Map basictypes;

	static
	{
		basictypes	= new Hashtable();
		basictypes.put("boolean", Boolean.class);
		basictypes.put("int", Integer.class);
		basictypes.put("double", Double.class);
		basictypes.put("float", Float.class);
		basictypes.put("long", Long.class);
		basictypes.put("short", Short.class);
		basictypes.put("byte", Byte.class);
		basictypes.put("char", Character.class);
		basictypes.put(boolean.class, Boolean.class);
		basictypes.put(int.class, Integer.class);
		basictypes.put(double.class, Double.class);
		basictypes.put(float.class, Float.class);
		basictypes.put(long.class, Long.class);
		basictypes.put(short.class, Short.class);
		basictypes.put(byte.class, Byte.class);
		basictypes.put(char.class, Character.class);

		clearClassCache();	// Hack!!! Needed to add basic types.
	}

	//-------- methods --------
	
	/**
	 *	Get the wrapped type. This method converts
	 *  basic types such as boolean or int to the
	 *  object types Boolean, Integer.
	 *  @param clazz The basic class.
	 *  @return The wrapped type, return clazz when
	 *  it is no basic type.
	 */
	public	static	Class	getWrappedType(Class clazz)
	{
		if(clazz==null)
			System.out.println("wurst");
		assert clazz!=null;

		// (jls) there are the following primitive types:
		// byte, short, int, long, char, float, double, boolean

		Class	result	= (Class)basictypes.get(clazz);
		return result==null ? clazz : result;
	}

	/**
	 *  Is basic type.
	 *  @return True, if the class is a basic type.
	 */
	public static boolean isBasicType(Class clazz)
	{
		return basictypes.get(clazz)!=null;
	}

	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  Uses static cache to speed up lookup.
	 *  @param name The class name.
	 *  @return The class, or null if not found.
	 */
	public static Class	classForName0(String name, ClassLoader classloader)
	{
		return classForName0(name, true, classloader);
	}


	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  Uses static cache to speed up lookup.
	 *  @param name The class name.
	 *  @return The class, or null if not found.
	 */
	public static Class	classForName0(String name, boolean initialize, ClassLoader classloader)
	{
		if(name==null)
			throw new IllegalArgumentException("Class name must not be null.");
		
		if(classloader==null)
			classloader = SReflect.class.getClassLoader();
		
		Object ret = classcache.get(name);
			
		//		System.out.println("cFN0 cache: "+clazz);
		if(ret==null)
		{
			// For arrays get plain name and count occurrences of '['.
			String	clname	= name;
			if(clname.indexOf('[')!=-1)
			{
				int	dimension	= 0;
				for(int i=clname.indexOf('['); i!=-1; i=clname.indexOf('[', i+1))
				{
					dimension++;
				}
				clname	= clname.substring(0, clname.indexOf('['));
				Class	clazz	= classForName0(clname, initialize, classloader);
				if(clazz!=null)
				{
					// Create array class object. Hack!!! Is there a better way?
					ret	= Array.newInstance(clazz, new int[dimension]).getClass();
				}
//				else
//				{
//					ret	= NOTFOUND;
//				}
			}
			else
			{
				
				try
				{
					ret = Class.forName(name, initialize, classloader);
//					ret	= Class.forName(clname);//, true, Thread.currentThread().getContextClassLoader());
//					ret	= SUtil.getClassLoader().loadClass(clname);
//					System.out.println("cFN0: loaded "+clazz);
				}
				catch(ClassNotFoundException e)
				{
//					e.printStackTrace();
//					ret	= NOTFOUND;
				}
				// Also handled by dynamic url class loader, but not in applets/webstart.
				catch(LinkageError e)
				{
//					e.printStackTrace();
//					ret	= NOTFOUND;
				}
			}
//			classcache.put(name, ret);
		}
		
//		if(ret==NOTFOUND)
//		{
//			ret	= null;
//		}
		return (Class)ret;
	}
	
	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  Uses static cache to speed up lookup.
	 *  @param name The class name.
	 *  @return The class.
	 */
	public static Class	classForName(String name, ClassLoader classloader)
		throws ClassNotFoundException
	{
		Object	clazz	= classForName0(name, classloader);
		if(clazz==null)
		{
			throw new ClassNotFoundException("Class "+name+" not found.");
		}
		return (Class)clazz;
	}

	/**
	 *	Beautifies names of arrays (eg 'String[]' instead of '[LString;').
	 *  @return The beautified name of a class.
	 */
	public static String getClassName(Class clazz)
	{
		int dim	= 0;
		if(clazz==null)
			throw new IllegalArgumentException("Clazz must not null.");
		while(clazz.isArray())
		{
			dim++;
			clazz	= clazz.getComponentType();
		}
		String	classname	= clazz.getName();
		for(int i=0; i<dim; i++)
		{
			classname	+= "[]";
		}
		return classname;
	}

	/**
	 *	Get unqualified class name.
	 *  Also beautifies names of arrays (eg 'String[]' instead of '[LString;').
	 *  @return The unqualified (without package) name of a class.
	 */
	public static String	getUnqualifiedClassName(Class clazz)
	{
		String	classname	= getClassName(clazz);
		StringTokenizer	stok	= new StringTokenizer(classname,".");
		while(stok.hasMoreTokens())
		{
			classname	= stok.nextToken();
		}
		return classname;
	}

	/**
	 *	Get inner class name.
	 *  @return The inner class's name (without declaring class).
	 */
	public static String	getInnerClassName(Class clazz)
	{
		String	classname	= (String)innerclassnamecache.get(clazz);
		if(classname==null)
		{
			classname	= getUnqualifiedClassName(clazz);
			StringTokenizer	stok	= new StringTokenizer(classname,"$");
			while(stok.hasMoreTokens())
			{
				classname	= stok.nextToken();
			}
			innerclassnamecache.put(clazz, classname);
		}
		return classname;
	}

	/**
	 *	Get the package of a class.
	 *  @return The name of the package.
	 */
	public static String	getPackageName(Class clazz)
	{
		String	classname	= clazz.getName();
		StringTokenizer	stok	= new StringTokenizer(classname,".");
		String	packagename	= "";
		while(stok.countTokens()>1)
		{
			packagename	+= stok.nextToken();
			if(stok.countTokens()>1)
			{
				packagename	+=".";
			}
		}
		return packagename;
	}

	/**
	 *  Get a field of the class,
	 *  or any of it's superclasses.
	 *  Unlike {@link Class#getField(String)},
	 *  this will also return nonpublic fields
	 *  (except when running in netscape :-( ).
	 *  @param clazz	The class to search.
	 *  @param name	The name of the field to search for.
	 *  @return	The field (or null if not found).
	 */
	public static Field	getField(Class clazz, String name)
	{
		Field	field	= null;
		Class	cls	= clazz;

		while(field==null && cls!=null && !cls.equals(Object.class))
		{
			try
			{
				field	= cls.getDeclaredField(name);
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				cls	= cls.getSuperclass();
			}
		}

		// Netscape security workaround.
		// Will only find public methods :-(.
		if(field==null)
		{
			try
			{
				field	= clazz.getField(name);
			}
			catch(Exception e){}
		}

		return field;
	}

	/**
	 *  Get a cached field.
	 *  @param clazz The clazz.
	 *  @param name The name.
	 *  @return The field.
	 */
	public static Field getCachedField(Class clazz, String name) throws NoSuchFieldException
	{
		Field ret = null;
		HashMap fields = (HashMap)fieldcache.get(clazz);
		if(fields==null)
		{
			fields = new HashMap();
			fieldcache.put(clazz, fields);
		}

		Object o = fields.get(name);

		if(o instanceof Field)
		{
			ret = (Field)o;
		}
		else if(o==null)
		{
			try
			{
				//ret = getField(clazz, name);
				ret = clazz.getField(name);
				fields.put(name, ret);
			}
			catch(NoSuchFieldException e)
			{
				fields.put(name, e);
				throw e;
			}
		}
		else
		{
			throw (NoSuchFieldException)o;
		}

		return ret;
	}

	/**
	 *  Get the declared object, doesnt cumulate through superclasses.
	 *  @param o	The object.
	 *  @param fieldname	The name of the array.
	 */
	public static Object	getDeclared(Object o, String fieldname)
	{
		Class	clazz	= o.getClass();
		Object	os	= null;
		Field	field	= null;
		try
		{
			field	= clazz.getDeclaredField(fieldname);
			os	= field.get(o);
		}
		catch(Exception e)
		{
			//System.out.println("could not find: "+fieldname+" in "+o);
			//e.printStackTrace();
		}
		return os;
	}

	/**
	 *  Get a method of the class.
	 *  Unlike {@link Class#getMethod(String, Class[])},
	 *  this uses the methodcache.
	 *  @param clazz	The class to search.
	 *  @param name	The name of the method to search for.
	 *  @param types	The parameter types.
	 *  @return	The method (or null if not found).
	 */
	public static Method	getMethod(Class clazz, String name, Class[] types)
	{
		Method	meth	= null;
		Method[]	ms	= getMethods(clazz, name);
		for(int i=0; i<ms.length; i++)
		{
			Class[]	ptypes	= ms[i].getParameterTypes();
			boolean	match	= ptypes.length==types.length;
			for(int j=0; match && j<ptypes.length; j++)
			{
				match	= ptypes[j].equals(types[j]);
			}

			if(match)
			{
				meth	= ms[i];
				break;
			}
		}
		return meth;
	}

	/**
	 *  Get public method(s) of the class by name.
	 *  @param clazz	The class to search.
	 *  @param name	The name of the method to search for.
	 *  @return	The method(s).
	 */
	public static Method[]	getMethods(Class clazz, String name)
	{
		Map	map	= (Map)methodcache.get(clazz);
		if(map==null)
		{
			map	= SCollection.createHashMap();
			methodcache.put(clazz, map);
		}

		Method[]	ret	= (Method[])map.get(name);
		if(ret==null)
		{
			Method[]	ms	= clazz.getMethods();
			int	cnt	= 0;
			for(int i=0; i<ms.length; i++)
			{
				if(ms[i].getName().equals(name))
				{
					cnt++;
				}
				else
				{
					ms[i]	= null;
				}
				
			}
			ret	= new Method[cnt];
			cnt	= 0;
			for(int i=0; i<ms.length; i++)
			{
				if(ms[i]!=null)
					ret[cnt++]	= ms[i];
			}
			map.put(name, ret);
		}
		return ret;
	}


	/**
	 *  Find a class.
	 *  When the class name is not fully qualified, the list of
	 *  imported packages is searched for the class.
	 *  @param clname	The class name.
	 *  @param imports	The comma separated list of imported packages.
	 *  @throws ClassNotFoundException when the class is not found in the imports.
	 */
	public static Class	findClass(String clname, String[] imports, ClassLoader classloader)
		throws ClassNotFoundException
	{
		Class	clazz	= findClass0(clname, imports, classloader);

		if(clazz==null)
		{
			throw new ClassNotFoundException("Class "+clname+" not found in imports: "+imports);
		}

		return clazz;
	}

	/**
	 *  Find a class. Also supports basic types and arrays.
	 *  When the class name is not fully qualified, the list of
	 *  imported packages is searched for the class.
	 *  @param clname	The class name.
	 *  @param imports	The comma separated list of imported packages.
	 *  @return null, when the class is not found in the imports.
	 */
	public static Class	findClass0(String clname, String[] imports, ClassLoader classloader)
	{
		Class	clazz	= null;
//		System.out.println("+++fC: "+clname+" "+imports);

		// Try to find fully qualified.
		clazz	= classForName0(clname, classloader);

		// Try to find in imports.
		if(clazz==null && imports!=null)
		{
			for(int i=-1; clazz==null && i<imports.length; i++)
			{
				// Always import java.lang.* (Hack???)
				String imp	= i==-1 ? "java.lang.*" : imports[i];	

				// Package import
				if(imp.endsWith(".*"))
				{
					clazz	= classForName0(
						imp.substring(0, imp.length()-1) + clname, classloader);
//					System.out.println("+++cFN1: "+imp.substring(0, imp.length()-1) + clname+", "+clazz);
				}
				// Class import
				else if(imports[i].endsWith(clname))
				{
					clazz	= classForName0(imp, classloader);
//					System.out.println("+++cFN2: "+imp+", "+clazz);
				}
			}
		}

		// No explicit imports, try java.lang (imported by default).
		else if(clazz==null)
		{
			clazz	= classForName0("java.lang." + clname, classloader);
		}
		
		return clazz;
	}

	/**
	 *  Match the given argument types to a set
	 *  of parameter type arrays.
	 *  The argument type array may contain null values,
	 *  when an argument type is unknown.
	 *  For convenience, the length of a parameter type array
	 *  does not have to equal the argument array length.
	 *  (although it will of course never match).
	 *  The method returns the indices of the matching
	 *  parameter type arrays. An empty array is returned,
	 *  if no match is found. The returned matches are sorted
	 *  by quality (best match first).
	 *  @param argtypes	The array of argument types.
	 *  @param paramtypes	The array of parameter type arrays.
	 *  @return The indices of the matching parameter type arrays.
	 */
	public static int[]	matchArgumentTypes(Class[] argtypes, Class[][] paramtypes)
	{
		// Store matches in array (store quality, -1 = no match).
		int[]	matches	= new int[paramtypes.length];
		int	hq	= 0;	// Highest quality value.
		int	cnt	= 0;	// Number of matches.
		for(int i=0; i<paramtypes.length; i++)
		{
			if(paramtypes[i].length==argtypes.length)
			{
				for(int j=0; j<argtypes.length && matches[i]!=-1; j++)
				{
					// Check if parameter type matches argument type.
					if(argtypes[j]!=null)
					{
						// No match.
						if(!SReflect.isSupertype(paramtypes[i][j], argtypes[j]))
						{
							matches[i]	= -1;
						}

						// Exact match.
						else if(getWrappedType(paramtypes[i][j])
							== getWrappedType(argtypes[j]))
						{
							// Increase quality.
							matches[i]++;
							if(matches[i]>hq)
								hq	= matches[i];
						}
					}
				}

				if(matches[i]!=-1)
					cnt++;
			}
			else
			{
				matches[i]	= -1;
			}
		}

		// Create result array.
		int[]	ret	= new int[cnt];
		cnt=0;
		// Insert indices by quality.
		for(;hq>=0; hq--)
		{
			for(int i=0; i<matches.length; i++)
			{
				if(matches[i]==hq)
				{
					ret[cnt++]	= i;
				}
			}
		}
		return ret;
	}

	/**
	 *  Check if a class is a supertype of, or the same as another class.
	 *  Maps basic types to wrapped types, and respects
	 *	the basic type hierarchy.
	 *  @param clazz1	The assumed supertype.
	 *  @param clazz2	The assumed subtype.
	 *  @return True, if clazz1 is a supertype of, or the same as clazz2.
	 */
	public static boolean	isSupertype(Class clazz1, Class clazz2)
	{
		// Map basic types.
		//System.out.println("a: "+clazz1.getName()+" "+clazz1.hashCode());
		//System.out.println("b: "+clazz2.getName()+" "+clazz2.hashCode());

		clazz1	= getWrappedType(clazz1);
		clazz2	= getWrappedType(clazz2);

		// Handle trivial case for speed.
		if(clazz1==clazz2)
		{
			return true;
		}

		// Check number type hierarchy.
		// Double.
		else if(clazz1==Double.class && (clazz2==Float.class 
			|| clazz2==Long.class || clazz2==Integer.class
			|| clazz2==Short.class || clazz2==Byte.class
			|| clazz2==Character.class))
		{
			return true;
		}
		// Float.
		else if(clazz1==Float.class && (clazz2==Long.class
			|| clazz2==Integer.class || clazz2==Short.class
			|| clazz2==Byte.class || clazz2==Character.class))
		{
			return true;
		}
		// Long.
		else if(clazz1==Long.class && (clazz2==Integer.class
			|| clazz2==Short.class || clazz2==Byte.class
			|| clazz2==Character.class))
		{
			return true;
		}
		// Integer.
		else if(clazz1==Integer.class && (clazz2==Short.class
			|| clazz2==Byte.class || clazz2==Character.class))
		{
			return true;
		}
		// Short.
		else if(clazz1==Short.class && clazz2==Byte.class)
		{
			return true;
		}

		// Standard case.
		else
		{
			return clazz1.isAssignableFrom(clazz2);
		}
	}

	/**
	 *  Convert a value to the correct wrapped type.
	 *  Assumes that the conversion is possible.
	 *  @see #isSupertype(Class, Class)
	 *  @param value	The value.
	 *  @param clazz	The target clazz.
	 *  @return	The converted value.
	 */
	public static Object convertWrappedValue(Object value, Class clazz)
	{
		clazz	= getWrappedType(clazz);
		if(isSupertype(Number.class, clazz))
		{
			if(value instanceof Character)
			{
				value	= new Integer(((Character)value).charValue());
			}
			Number	num =null;
			if(value!=null)
			{
				try
				{
					num	= (Number)value;
				}
				catch(ClassCastException e)
				{
					System.out.println(":: "+value+" "+value.getClass()+" "+clazz);
				}
			
				if(clazz.equals(Double.class))
				{
					value	= new Double(num.doubleValue());
				}
				else if(clazz.equals(Float.class))
				{
					value	= new Float(num.floatValue());
				}
				else if(clazz.equals(Long.class))
				{
					value	= new Long(num.longValue());
				}
				else if(clazz.equals(Integer.class))
				{
					value	= new Integer(num.intValue());
				}
				else if(clazz.equals(Short.class))
				{
					value	= new Short(num.shortValue());
				}
			}
		}

		return value;
	}
	
	/**
	 *  Get an iterator for an arbitrary collection object.
	 *  Supports iterators, enumerations, java.util.Collections,
	 *  java.util.Maps, arrays. Null is converted to empty iterator.
	 *  @param collection	The collection object.
	 *  @return An iterator over the collection.
	 *  @throws IllegalArgumentException when argument is not
	 * 		one of (Iterator, Enumeration, Collection, Map, Array).
	 */
	public static Iterator	getIterator(Object collection)
	{
		if(collection==null)
		{
			return Collections.EMPTY_LIST.iterator();
		}
		else if(collection instanceof Iterator)
		{
			return (Iterator)collection;
		}
		else if(collection instanceof Enumeration)
		{
			// Return enumeration wrapper.
			final Enumeration eoc	= (Enumeration)collection;
			return new Iterator()
			{
				public boolean	hasNext()	{return eoc.hasMoreElements();}
				public Object	next()	{return eoc.nextElement();}
				public void	remove(){throw new UnsupportedOperationException(
					"remove() not supported for enumerations");}
			};
		}
		else if(collection instanceof Collection)
		{
			return ((Collection)collection).iterator();
		}
		else if(collection instanceof Map)
		{
			return ((Map)collection).values().iterator();
		}
		else if(collection!=null && collection.getClass().isArray())
		{
			// Return array wrapper.
			final Object array	= collection;
			return new Iterator()
			{
				int i=0;
				public boolean	hasNext()	{return i<Array.getLength(array);}
				public Object	next()	{return Array.get(array, i++);}
				public void	remove()	{throw new UnsupportedOperationException(
					"remove() not supported for arrays");}
			};
		}
		else
		{
			throw new IllegalArgumentException("Cannot iterate over "+collection);
		}
	}
	
	protected static Object[] EMPTY_ARRAY = new Object[0];
	/**
	 *  Get an array for an arbitrary collection object.
	 *  Supports iterators, enumerations, java.util.Collections,
	 *  java.util.Maps, arrays. Null is converted to empty array.
	 *  @param collection	The collection object.
	 *  @return An array over the collection.
	 *  @throws IllegalArgumentException when argument is not
	 * 		one of (Iterator, Enumeration, Collection, Map, Array).
	 */
	public static Object getArray(Object collection)
	{
		if(collection==null)
		{
			return EMPTY_ARRAY;
		}
		else if(collection instanceof Iterator)
		{
			final Iterator it = (Iterator)collection;
			List ret = new ArrayList();
			while(it.hasNext())
				ret.add(it.next());
			return ret.toArray();
		}
		else if(collection instanceof Enumeration)
		{
			final Enumeration eoc	= (Enumeration)collection;
			List ret = new ArrayList();
			while(eoc.hasMoreElements())
				ret.add(eoc.nextElement());
			return ret.toArray();
		}
		else if(collection instanceof Collection)
		{
			return ((Collection)collection).toArray();
		}
		else if(collection instanceof Map)
		{
			return ((Map)collection).values().toArray();
		}
		else if(collection!=null && collection.getClass().isArray())
		{
			return collection;
		}
		else
		{
			throw new IllegalArgumentException("Cannot iterate over "+collection);
		}
	}

	/**
	 *  Is an object instanceof a class or its superclasses.
	 *  @param o The object.
	 *  @param c The class.
	 *  @return True, when o is instance of class c.
	 */
	public static boolean instanceOf(Object o, Class c)
	{
		return isSupertype(c, o.getClass());
	}

	/**
	 *  Clear the classcache, e.g. when the classpath has changed.
	 */
	public static void clearClassCache()
	{
		classcache	= new Hashtable();
		classcache	.put("boolean", boolean.class);
		classcache	.put("int", int.class);
		classcache	.put("double", double.class);
		classcache	.put("float", float.class);
		classcache	.put("long", long.class);
		classcache	.put("short", short.class);
		classcache	.put("byte", byte.class);
		classcache	.put("char", char.class);

		methodcache.clear();
		fieldcache.clear();
	}

	//-------- process java string (obsolete) --------

	/**
	 *  Match parameter types of constructors and methods.
	 *  @param current	The current set of types (may be null).
	 *  @param test	The new type set to test against.
	 *  @param cnt	The number of parameters required.
	 *  @return The new current type set, conatining nulls, for
	 *    all ambiguous parameters.
	 * /
	protected static Class[]	matchParameterTypes(Class[] current, Class[] test, int cnt)
	{
		// Check if length matches.
		if(test.length==cnt)
		{
			// Check if it is the first with correct length
			if(current==null)
			{
				current	= test;
			}
			else
			{
				// Adjust current array for non matching types.
				for(int i=0; i<current.length; i++)
				{
					if(current[i]!=test[i])
					{
						current[i]	= null;
					}
				}
			}
		}

		return current;
	}*/

	/**
	 *  Instantiate parameter values for constructor and method invocations.
	 *  This method also fills in missing parameter types.
	 *
	 *  @param args	The parameter descriptions.
	 *  @param ptypes	The parameter types (as known).
	 *  @param objects	Parameter objects that may be used in the description.
	 *  @param imports	The comma separated list of imported packages.
	 *
	 *  @return The instantiated parameter values.
	 *
	 *  @throws ClassNotFoundException When a declared type does not exist.
	 * /
	protected static Object[]	instantiateParameters(ExpressionTokenizer args,
		Class[] ptypes, Hashtable objects, String imports)
		throws ClassNotFoundException // Hack !!!
	{
		Object[]	pvalues	= new Object[ptypes.length];
		for(int i=0; i<pvalues.length; i++)
		{
			String arg	= args.nextToken().trim();

			// When a ":" is contained at top level,
			// the exact parameter type was specified.
			ExpressionTokenizer exto	= new ExpressionTokenizer(
				arg, ":", new String[]{"()", "\"\"", "[]", "{}"});
			if(exto.countTokens()>1)
			{
				ptypes[i]	= findClass(exto.nextToken().trim(), imports);
				arg	= exto.remainingTokens().trim();
			}
			// System.out.println("param: "+ptypes[i]+", "+arg);
			// Use wrapped type for parameters like "int: 7".
			pvalues[i]	= processJavaString(
				ptypes[i]==null ? null : getWrappedType(ptypes[i]),
				arg, objects, imports);

			// When the parameter type is still unknown,
			// use the created objects type, or java.lang.Object when null.
/*			if(ptypes[i]==null)
			{
				ptypes[i]	= pvalues[i]==null ? Object.class
					: pvalues[i].getClass();
			}
* /		}
		return pvalues;
	}*/

	/**
	 *  Invoke a method.
	 *
	 *  @param object	The object in which to invoke
	 *    (may be null for static methods).
	 *  @param clazz	The class on which to invoke.
	 *    (may be null for non static methods).
	 *  @param desc	The string description.
	 *  @param objects	Parameter objects that may be used in the description.
	 *  @param imports	The comma separated list of imported packages.
	 *
	 *  @return The return value of the method (if any).
	 *
	 *  @throws ClassNotFoundException When a declared type does not exist.
	 *  @throws InstantiationException When a parameter can not be instantiated.
	 *  @throws IllegalAccessException When a declared member can not be accessed.
	 *  @throws Throwable When an invocation target exception occurs.
	 * /
	protected static Object	invokeMethod(Object object, Class clazz,
		String desc, Hashtable objects, String imports)
		throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, Throwable
	{
		//System.out.println("invokeMethod: "+desc);
		clazz	= clazz==null ? object.getClass() : clazz;
		// Extract methodname and list of arguments.
		int	pos	= desc.indexOf("(");
		String	mname	= desc.substring(0, pos);
		ExpressionTokenizer expt	= new ExpressionTokenizer(
			desc.substring(pos+1, desc.length()-1), ",", new String[]{"()", "\"\"", "[]", "{}"});

		// Build a parameter type list.
		// When more than one method matches the number of arguments,
		// the not matching types will be set to null.
		int pcnt	= expt.countTokens();
		Class[] ptypes	= null;
		Method[] ms	= getMethods(clazz, mname);
		for(int i=0; i<ms.length; i++)
		{
			ptypes	= matchParameterTypes(ptypes, ms[i].getParameterTypes(), pcnt);
		}
		if(ptypes==null)
		{
			// No method with the required number of parameters available.
			throw new RuntimeException("No_method_available! "+clazz+"."+mname+"("+pcnt+")");
		}
		// System.out.println("here: "+SUtil.arrayToString(ptypes));

		// Instantiate parameter values, and fill in missing parameter types.
		Object[] params	= instantiateParameters(expt, ptypes, objects, imports);

		// Now all the arguments are ready.
		// Try to find a proper method.
		// Therefore not only the types of the arguments
		// are relevant but also the directly specified types.
		// Java searches only exactly matching methods.

		// Find possible supertypes for still unknown parameter types.
		for(int i=0; i<ptypes.length; i++)
		{
			if(ptypes[i]==null)
			{
				if(params[i]!=null)
				{
					//System.out.println("Trying to resolve from: "+params[i].getClass());
					for(int j=0; j<ms.length; j++)
					{
						Class[]	ctypes	= ms[j].getParameterTypes();
						if(ctypes.length==ptypes.length
							&& ctypes[i].isAssignableFrom(params[i].getClass()))
						{
							if(ptypes[i]==null)
							{
								//System.out.println("Found matching: "+ctypes[i]);
								ptypes[i]	= ctypes[i];
							}
							else if(ptypes[i]!=ctypes[i])
							{
								//System.out.println("Found different: "+ctypes[i]);
								ptypes[i]	= null;
								throw new RuntimeException("Ambiguous constructor param type: "+clazz+" "+SUtil.arrayToString(ptypes)+" "+SUtil.arrayToString(params));
							}
						}
					}
				}
				else
				{
					throw new RuntimeException("Cannot resolve constructor param type: "+clazz+" "+SUtil.arrayToString(ptypes)+" "+SUtil.arrayToString(params));
				}
			}
		}
		try
		{
			Method	m	= clazz.getMethod(mname, ptypes);
/*			System.out.println("Object: "+object.getClass());
			System.out.println("Method: "+m);
			for(int i=0; i<params.length; i++)
				System.out.println("Param"+(i+1)+": "
					+ (params[i]!=null ? params[i].getClass().toString() : " is null"));
* /			return m.invoke(object, params);
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("No method found for: "+clazz+"."+mname
				+SUtil.arrayToString(ptypes)+", "+desc);
		}
		catch(InvocationTargetException e)
		{
			throw e.getTargetException();
		}
	}*/

	/**
	 *  Invoke a constructor.
	 *
	 *  @param desc	The string description.
	 *  @param objects	Parameter objects that may be used in the description.
	 *  @param imports	The comma separated list of imported packages.
	 *
	 *  @return The instantiated object.
	 *
	 *  @throws ClassNotFoundException When a declared type does not exist.
	 *  @throws InstantiationException When a parameter can not be instantiated.
	 *  @throws IllegalAccessException When a declared member can not be accessed.
	 *  @throws Throwable When an invocation target exception occurs.
	 * /
	protected static Object	invokeConstructor(String desc, Hashtable objects,
		String imports)	throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, Throwable
	{
		// System.out.println("invokeConstructor: "+desc);
		// Extract class and list of arguments.
		int	pos	= desc.indexOf("(");
		Class	clazz	= findClass(desc.substring(0, pos), imports);
		ExpressionTokenizer expt	= new ExpressionTokenizer(
			desc.substring(pos+1, desc.length()-1), ",", new String[]{"()", "\"\"", "[]", "{}"});

		// Build a parameter type list.
		// When more than one constructor matches the number of arguments,
		// the not matching types will be set to null.
		int pcnt	= expt.countTokens();
		Class[] ptypes	= null;
		Constructor[] cs	= clazz.getConstructors();
		for(int i=0; i<cs.length; i++)
		{
			ptypes	= matchParameterTypes(ptypes, cs[i].getParameterTypes(), pcnt);
		}
		if(ptypes==null)
		{
			// No constructor with the required number of parameters available.
			throw new RuntimeException("No_constructor_available! "+pcnt+" "+clazz);
		}
		//System.out.println("param_types: "+SUtil.arrayToString(ptypes));

		// Instantiate parameter values, and fill in missing parameter types.
		Object[] params	= instantiateParameters(expt, ptypes, objects, imports);
		//System.out.println("param_values: "+SUtil.arrayToString(params));

		// Now all the arguments are ready.
		// Try to find a proper constructor.
		// Therefore not only the types of the argumnets
		// are relevant but also the directly specified
		// types. Java searches only exactly matching
		// constructors.

		// Find possible supertypes for still unknown parameter types.
		for(int i=0; i<ptypes.length; i++)
		{
			if(ptypes[i]==null)
			{
				if(params[i]!=null)
				{
					//System.out.println("Trying to resolve from: "+params[i].getClass());
					for(int j=0; j<cs.length; j++)
					{
						Class[]	ctypes	= cs[j].getParameterTypes();
						if(ctypes.length==ptypes.length
							&& ctypes[i].isAssignableFrom(params[i].getClass()))
						{
							if(ptypes[i]==null)
							{
								//System.out.println("Found matching: "+ctypes[i]);
								ptypes[i]	= ctypes[i];
							}
							else if(ptypes[i]!=ctypes[i])
							{
								//System.out.println("Found different: "+ctypes[i]);
								ptypes[i]	= null;
								throw new RuntimeException("Ambiguous constructor param type: "+clazz+" "+SUtil.arrayToString(ptypes)+" "+SUtil.arrayToString(params));
							}
						}
					}
				}
				else
				{
					throw new RuntimeException("Cannot resolve constructor param type: "+clazz+" "+SUtil.arrayToString(ptypes)+" "+SUtil.arrayToString(params));
				}
			}
		}

		try
		{
			return clazz.getConstructor(ptypes).newInstance(params);
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("No constructor found for: "+clazz
				+SUtil.arrayToString(ptypes)+", "+desc);
		}
		catch(InvocationTargetException e)
		{
			throw e.getTargetException();
		}
	}*/

	/**
	 *  Create an array.
	 *
	 *  @param desc	The string description.
	 *  @param objects	Parameter objects that may be used in the description.
	 *  @param imports	The comma separated list of imported packages.
	 *
	 *  @return The instantiated object.
	 *
	 *  @throws ClassNotFoundException When a declared type does not exist.
	 *  @throws InstantiationException When a parameter can not be instantiated.
	 *  @throws IllegalAccessException When a declared member can not be accessed.
	 *  @throws Throwable When an invocation target exception occurs.
	 * /
	protected static Object	createArray(String desc, Hashtable objects,
		String imports)	throws ClassNotFoundException, Throwable
	{
		// System.out.println("createArray: "+desc);
		// Extract class and list of contents.
		int	pos	= desc.indexOf("[");
		int	pos2	= desc.indexOf("]");
		Class	clazz	= findClass(desc.substring(0, pos), imports);
		int	length	= 0;
		Object[]	content	= new Object[0];

		// Length declaration? "Array[length]"
		if(pos2!=pos+1)
		{
			length	= ((Number)processJavaString(
				desc.substring(pos+1, pos2).trim(), objects, imports)).intValue();
		}

		// Content declaration? "Array[]{content0, content1, ...}"
		String	desc2	= desc.substring(pos2+1).trim();
		if(desc2.startsWith("{"))
		{
			if(!desc2.endsWith("}"))
			{
				throw new RuntimeException("Illegal array declaration: "+desc);
			}
			ExpressionTokenizer expt	= new ExpressionTokenizer(
				desc2.substring(1, desc2.length()-1), ",", new String[]{"()", "\"\"", "[]", "{}"});
			content	= new Object[expt.countTokens()];
			for(int i=0; i<content.length; i++)
			{
				String tok	= expt.nextToken().trim();
				if(tok.equals(""))
				{
					throw new RuntimeException("Illegal array declaration: "+desc);
				}
				content[i]	= processJavaString(tok, objects, imports);
			}
		}

		// Create array object, and fill content (if any).
		Object	array	= Array.newInstance(clazz, Math.max(length, content.length));
		for(int i=0; i<content.length; i++)
		{
			Array.set(array, i, content[i]);
		}

		return array;
	}*/

	/**
	 *  Create a collection or map.
	 *
	 *  @param desc	The string description.
	 *  @param objects	Parameter objects that may be used in the description.
	 *  @param imports	The comma separated list of imported packages.
	 *
	 *  @return The instantiated object.
	 *
	 *  @throws ClassNotFoundException When a declared type does not exist.
	 *  @throws InstantiationException When a parameter can not be instantiated.
	 *  @throws IllegalAccessException When a declared member can not be accessed.
	 *  @throws Throwable When an invocation target exception occurs.
	 * /
	protected static Object	createCollection(String desc, Hashtable objects,
		String imports)	throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, Throwable
	{
		// System.out.println("createCollection: "+desc);
		// Extract class and list of contents.
		int	pos	= desc.indexOf("{");
		Class	clazz	= findClass(desc.substring(0, pos).trim(), imports);
		Object	collection	= clazz.newInstance();

		// Fill in collection values (comma separated).
		ExpressionTokenizer exto	= new ExpressionTokenizer(
			desc.substring(pos+1, desc.length()-1), ",", new String[]{"()", "\"\"", "[]", "{}"});
		while(exto.hasMoreTokens())
		{
			String tok	= exto.nextToken().trim();
			// Is key value pair? "key=value"
			ExpressionTokenizer exto2	= new ExpressionTokenizer(
				tok, "=", new String[]{"()", "\"\"", "[]", "{}"});
			if(exto2.countTokens()>1)
			{
				// key value pair
				Object	key	= processJavaString(exto2.nextToken().trim(), objects, imports);
				Object	value	= processJavaString(exto2.nextToken().trim(), objects, imports);
				((Map)collection).put(key, value);
			}
			else
			{
				// single value
				((Collection)collection).add(processJavaString(tok, objects, imports));
			}
		}

		return collection;
	}*/

	/**
	 *  Allow the invocation of a java method on an object.
	 *  E.g. $widget.setLayout(BorderLayout())
	 * /
	public static Object processJavaString(String desc)
	{
		return processJavaString(null, desc, null, null);
	}*/

	/**
	 *  Allow the invocation of a java method on an object.
	 *  E.g. $widget.setLayout(BorderLayout())
	 * /
	public static Object processJavaString(String desc, String imports)
	{
		return processJavaString(null, desc, null, imports);
	}*/

	/**
	 *  Allow the invocation of a java method on an object.
	 *  E.g. $widget.setLayout(BorderLayout())
	 * /
	public static Object processJavaString(String desc, Hashtable params,
		String imports)
	{
		return processJavaString(null, desc, params, imports);
	}*/

	/**
	 *  Allow the invocation of a java method on an object.
	 *  E.g. $widget.setLayout(BorderLayout())
	 * /
	public static Object processJavaString(Class clazz, String desc,
		Hashtable params, String imports)
	{
		// System.out.println("PJS: "+clazz+", "+desc);

		Object obj	= null;
		try
		{
			// Use expression tokenizer to tokenize in dot-separated parts,
			// which may include dots in brackets and quotes:
			// E.g. (test.me), "test.me"
			ExpressionTokenizer exto	= new ExpressionTokenizer(
				desc, ".", new String[]{"()", "\"\"", "[]", "{}"});
			String tok	= exto.nextToken();

			//System.out.println("Token: "+tok);
			// Get object for first token(s). May be one of:
			// $object, "String", null, true, false, int,
			//   constructor, static field, static method, array, collection
			if(tok.startsWith("$"))
			{
				obj	= params.get(tok.substring(1));
				// System.out.println("Params: "+params);
			}
			else if(tok.startsWith("\"") && tok.endsWith("\""))
			{
				obj	= tok.substring(1, tok.length()-1);
			}
			else if(tok.toLowerCase().equals("null"))
			{
				obj	= null;
			}
			else if(tok.toLowerCase().equals("true"))
			{
				obj	= new Boolean(true);
			}
			else if(tok.toLowerCase().equals("false"))
			{
				obj	= new Boolean(false);
			}
			else if(Character.isDigit(tok.charAt(0)))
			{
				obj	= new Integer(tok);
			}
			else
			{
				// May be constructor, static field, static method, array, collection
				Class clazz0	= null;
				String start	= tok;
				int bra	= -1;	// position of brace
				int squ	= -1;	// position of square brace
				int cur	= -1;	// position of curly brace
				int pos	= -1;	// position of first brace
				boolean	hasbrace	= false;

				// Concat tokens until class is found,
				// or a method token (with braces) is found,
				// or no more tokens would be available.
				while(clazz0==null)
				{
					try
					{
						bra	= start.indexOf("(");
						squ	= start.indexOf("[");
						cur	= start.indexOf("{");
						hasbrace	= bra!=-1 || squ!=-1 || cur!=-1;
						if(!hasbrace)
						{
							// No braces, check for class.
							clazz0	= findClass(start, imports);
						}
						else
						{
							// Braces found: Must be constructor, array, collection
							// Find first brace: If both !=-1 use min, else the !=-1
							pos	= (bra!=-1 && squ!=-1) ? Math.min(bra, squ)
								: ((bra!=-1) ? bra : squ);
							pos	= (pos!=-1 && cur!=-1) ? Math.min(pos, cur)
								: ((pos!=-1) ? pos : cur);
							clazz0	= findClass(start.substring(0,pos), imports);
						}
					}
					catch(ClassNotFoundException e)
					{
						if(!hasbrace && exto.hasMoreTokens())
						{
							// Continue search.
							tok	=  exto.nextToken();
							start	+= "." + tok;
						}
						else
						{
							// Class not found. Rethrow Exception.
							throw e;
						}
					}
				}

				// When class is found, try to create object.
				if(clazz0!=null)
				{
					if(hasbrace && pos==bra)
					{
						// brace -> is constructor
						obj	= invokeConstructor(start, params, imports);
					}
					else if(hasbrace && pos==squ)
					{
						// square brace -> is array
						obj	= createArray(start, params, imports);
					}
					else if(hasbrace && pos==cur)
					{
						// curly brace -> is collection
						obj	= createCollection(start, params, imports);
					}
					else
					{
						// Next token is static field or static method.
						tok	=  exto.nextToken();
						if(tok.indexOf("(")==-1)
						{
							// No braces: static field.
							Field	field	= SReflect.getField(clazz0, tok);
							if(field==null && tok.equals("class"))
							{
								// Field not found but name of field is "class"
								// Evaluate to Class object.
								obj	= clazz0;
							}
							else if(field==null)
							{
								// Field not found. Remember Exception.
								throw new RuntimeException("No field named "+tok+" in "+clazz);
							}
							else
							{
								obj	= field.get(null);
							}
						}
						else
						{
							obj	= invokeMethod(null, clazz0, tok, params, imports);
						}
					}
				}
			}

			//System.out.println("created: "+obj+" for: "+desc);

			// Perform method invocations on created object.
			// ??? Should allow access to fields also ?
			while(exto.hasMoreTokens())
			{
				tok	= exto.nextToken();
				//System.out.println("Token: "+tok);
				if(obj instanceof Number && Character.isDigit(tok.charAt(0)))
				{
					// Create double and add the fraction value.
					obj	= new Double(((Number)obj).doubleValue()
						+ Double.parseDouble("0."+tok));
				}
				else
				{
					obj	= invokeMethod(obj, null, tok, params, imports);
				}
			}
		}
		catch(Throwable t)
		{
			//System.out.println("Failed to create "+desc);
			// In case of error, try fallback: String parameter constructor.
			if(!(t instanceof InstantiationException)
				&& !(t instanceof IllegalAccessException)
				&& !(t instanceof InvocationTargetException))
			{
				if(clazz==String.class)
				{
					// For speed treat String special.
					obj	= desc;
					t	= null;
				}
				else if(clazz!=null)
				{
					try
					{
						Constructor	con
							= clazz.getConstructor(new Class[]{String.class});
						obj	= con.newInstance(new Object[]{desc});
						t	= null;
					}
					catch(Exception e)
					{
						System.out.println(e);
					}
				}
			}

			// Throw exception, if any.
			if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else if(t!=null)
			{
				t.printStackTrace();
				throw new RuntimeException(t.toString());
			}
			else
			{
				System.out.println("WARNING: Could not evaluate: "
					+desc+". Creating String \""+obj+"\" as fallback.");
			}
		}

		return obj;
	}*/

	//-------- main for testing --------
/*
	public static void main(String[] args)
	{
		if(args.length!=0)
		{
			String	arg	= args[0];
			for(int i=1; i<args.length; i++)
			{
				arg	= arg+" "+args[i];
			}
			System.out.println(SReflect.processJavaString(arg));
		}
		else
		{
			String	imports	= "java.awt, java.lang, java.text, java.util, util";
			Hashtable params	= new Hashtable();
			params.put("a", new Integer(7));
			String[]	test	= new String[]
			{
				"$a",
				"\"abc\"",
				"null",
				"true",
				"false",
				"42",
				"42.42",
				"Color(int: 0, int: 255, int: 0)",
				"SReflect.a",
				"Integer.parseInt(\"42\")",
				"DateFormat.getDateInstance().format(Date: 09/09/1999)",
				"boolean[Integer.parseInt(\"42\")]",
//				"String(byte[]{Byte(32), Byte(33), Byte(34)}, byte: 0)",
				"String[] {DateFormat.getDateInstance().format(Date: 09/09/1999)}",
				"Hashtable{\"name\"=\"value\", true=false, false=42.42}",
				"TreeSet {132, 17, Integer.parseInt(\"42\"), 28, 3}",
				"String(\"test: this\")",
				"String(String: test: this)",
				"SReflect()",
				"SReflect(1)"
			};

			for(int i=0; i<test.length; i++)
			{
				try
				{
					System.out.print(test[i] + ":\t");
					Object	obj = SReflect.processJavaString(test[i], params, imports);
					System.out.println(
						((obj instanceof Object[]) ? SUtil.arrayToString(obj) : (""+obj))
						+ " (" + (obj!=null ? obj.getClass().getName() : null) + ")");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/** Test field. * /
	public static final String	a	= "a is great and beautiful";

	/** Test field 2. * /
	public static final int	i	= 42;

	/** Test field 3. * /
	public static final int	ALL	= 42;

	/** Test constructor. * /
	public SReflect()
	{
		//throw new RuntimeException("test exception");
	}

	/** Test constructor. * /
	public SReflect(int i)	throws Exception
	{
		throw new Exception("test exception");
	}*/

}


