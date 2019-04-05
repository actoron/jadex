package jadex.commons;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jadex.commons.SClassReader.ClassFileInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.collection.SCollection;
import jadex.commons.collection.WeakValueMap;


/**
 *  This class provides several useful static reflection methods.
 */
public class SReflect
{
	//-------- attributes --------
	
	/** Class lookup cache (classloader(weak)->Map([name, import]->class)). */
//	protected static final Map classcache	= Collections.synchronizedMap(new WeakHashMap());
	protected static final Map<Tuple2<String, Integer>, Class<?>> classcache	
		= new WeakValueMap<Tuple2<String, Integer>, Class<?>>();

	/** Inner class name lookup cache. */
	protected static final Map innerclassnamecache	= Collections.synchronizedMap(new WeakHashMap());

	/** Method lookup cache (class->(name->method[])). */
	protected static final Map methodcache	= Collections.synchronizedMap(new WeakHashMap());
	
	/** Method lookup cache (class->(name->method[])), includes non-public methods. */
	protected static final Map allmethodcache	= Collections.synchronizedMap(new WeakHashMap());

	/** Field lookup cache (class->(name->field[])). */
	protected static final Map fieldcache = Collections.synchronizedMap(new WeakHashMap());

	/** Mapping from basic class name -> basic type(class). */
	protected static final Map basictypes;

	/** Mapping from basic class -> object type(class). */
	protected static final Map wrappedtypes;

	/** String convertable types. */
	protected static final Set convertabletypes;
	
	/** This is set to true if the VM has a working GUI environment available. */
	public static final boolean HAS_GUI	= !isAndroid() && SNonAndroid.hasGui();
	
	static
	{
		basictypes = new HashMap();
		basictypes.put("boolean", boolean.class);
		basictypes.put("int", int.class);
		basictypes.put("double", double.class);
		basictypes.put("float", float.class);
		basictypes.put("long", long.class);
		basictypes.put("short", short.class);
		basictypes.put("byte", byte.class);
		basictypes.put("char", char.class);
		
		wrappedtypes = new HashMap();
		wrappedtypes.put(boolean.class, Boolean.class);
		wrappedtypes.put(int.class, Integer.class);
		wrappedtypes.put(double.class, Double.class);
		wrappedtypes.put(float.class, Float.class);
		wrappedtypes.put(long.class, Long.class);
		wrappedtypes.put(short.class, Short.class);
		wrappedtypes.put(byte.class, Byte.class);
		wrappedtypes.put(char.class, Character.class);
		wrappedtypes.put(void.class, Void.class);
		
		convertabletypes = new HashSet();
		convertabletypes.add(String.class);
		convertabletypes.add(int.class);
		convertabletypes.add(Integer.class);
		convertabletypes.add(long.class);
		convertabletypes.add(Long.class);
		convertabletypes.add(float.class);
		convertabletypes.add(Float.class);
		convertabletypes.add(double.class);
		convertabletypes.add(Double.class);
		convertabletypes.add(boolean.class);
		convertabletypes.add(Boolean.class);
		convertabletypes.add(short.class);
		convertabletypes.add(Short.class);
		convertabletypes.add(byte.class);
		convertabletypes.add(Byte.class);
		convertabletypes.add(char.class);
		convertabletypes.add(Character.class);
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
	public	static	Class<?>	getWrappedType(Class<?> clazz)
	{
		if(clazz==null)
			throw new IllegalArgumentException("Clazz must not be null");

		// (jls) there are the following primitive types:
		// byte, short, int, long, char, float, double, boolean, AND void!

		if (clazz.isPrimitive()) {
			clazz = (Class<?>) wrappedtypes.get(clazz);
		}
		return clazz;
	}
	
	/**
	 *  Unwrap a generic type.
	 *  @param type The generic type.
	 *  @return The unwrapped class.
	 */
	public static Class<?> unwrapGenericType(Type type)
	{
		return getClass(getInnerGenericType(type));
	}
	
	/**
	 *  Unwrap a generic type.
	 *  @param type The generic type.
	 *  @return The unwrapped class.
	 */
	public static Type getInnerGenericType(Type type)
	{
		Type ret = null;
		if(type instanceof ParameterizedType)
		{
			ParameterizedType pt = (ParameterizedType)type;
			Type[] pts = pt.getActualTypeArguments();
			if(pts.length>1)
				throw new RuntimeException("Cannot unwrap futurized method due to more than one generic type: "+SUtil.arrayToString(pt.getActualTypeArguments()));
			ret = pts[0];
		}
		
		return ret;
	}
	
	/**
	 *  Get the class for a type.
	 */
	public static Class<?> getClass(Type type)
	{
		Class<?> ret = null;
		if(type instanceof Class)
			ret = (Class<?>)type;
		else if(type instanceof ParameterizedType)
			ret = (Class<?>)((ParameterizedType)type).getRawType();
		else if(type!=null)
			throw new RuntimeException("Cannot unwrap: "+type);
		
		return ret;
	}
	
	/**
	 *  Test if the class is automatically convertable
	 *  from/to a string. Is valid for all basic types
	 *  and their object pendants.
	 *  @param clazz The class.
	 *  @return True, if convertable.
	 */
	public static boolean isStringConvertableType(Class clazz)
	{
		return convertabletypes.contains(clazz);
	}

	/**
	 *  Is basic type.
	 *  @return True, if the class is a basic type.
	 */
	public static boolean isBasicType(Class<?> clazz)
	{
		return wrappedtypes.get(clazz)!=null;
	}

	public static Object wrapValue(boolean val)
	{
		return val? Boolean.TRUE: Boolean.FALSE;
	}
	
	public static Object wrapValue(int val)
	{
		return Integer.valueOf(val);
	}
	
	public static Object wrapValue(long val)
	{
		return Long.valueOf(val);
	}
	
	public static Object wrapValue(byte val)
	{
		return Byte.valueOf(val);
	}
	
	public static Object wrapValue(char val)
	{
		return Character.valueOf(val);
	}
	
	public static Object wrapValue(float val)
	{
		return Float.valueOf(val);
	}
	
	public static Object wrapValue(double val)
	{
		return Double.valueOf(val);
	}
	
	public static Object wrapValue(short val)
	{
		return Short.valueOf(val);
	}
	
	public static Object wrapValue(Object val)
	{
		return val;
	}
	
	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  Uses static cache to speed up lookup.
	 *  @param name The class name.
	 *  @return The class, or null if not found.
	 */
	public static Class<?>	classForName0(String name, ClassLoader classloader)
	{
		return classForName0(name, true, classloader);
	}

//	static long	hit	= 0;
//	static long	miss	= 0;
	
	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  @param name The class name.
	 *  @return The class, or null if not found.
	 */
	public static Class<?>	classForName0(String name, boolean initialize, ClassLoader classloader)
	{
		if(name==null)
			throw new IllegalArgumentException("Class name must not be null.");
		
//		if(name.indexOf("AgentPlan")!=-1)
//			System.out.println("+++fC: "+name+" "+classloader);
		
		Object ret = basictypes.get(name);
			
		if(ret==null)
		{
			if(classloader==null)
				classloader = SReflect.class.getClassLoader();
			Integer hash = Integer.valueOf(classloader.hashCode());

			ret = classcache.get(new Tuple2<String, Integer>(name, hash));
			
			if(ret==null)
			{
//				System.out.println("cFN0 cachemiss: "+name);
				
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
					Class<?> clazz	= classForName0(clname, initialize, classloader);
					if(clazz!=null)
					{
						// Create array class object. Hack!!! Is there a better way?
						ret	= Array.newInstance(clazz, new int[dimension]).getClass();
					}
				}
				else
				{
					
					try
					{
						// Do not use ClassLoader.loadClass() due to Java bug #6434149
						ret = Class.forName(name, initialize, classloader);
	//					System.out.println("cFN0: loaded "+clazz);
					}
//					catch(ClassNotFoundException e)
//					{
//					}
//					// Also handled by dynamic url class loader, but not in applets/webstart.
//					catch(LinkageError e)
//					{
//						e.printStackTrace();
//					}
					catch(Throwable e)
					{
//						e.printStackTrace();
//						if(name.indexOf("AgentPlan")!=-1)
//							e.printStackTrace();
						// Catch anything as sometimes strange errors appear
						// E.g. (http://pastebin.com/6xkRhJqG)
						// Exception in thread "AWT-EventQueue-0" java.lang.InternalError: Unable to find plugin native libraries
				        //   at sun.plugin2.util.NativeLibLoader.load(Unknown Source)
					}
				}
				
				if(ret==null)
					ret	= NotFound.class;
				
				classcache.put(new Tuple2<String, Integer>(name, hash), (Class<?>)ret);
			}
		}
		
		return ret instanceof Class && !NotFound.class.equals(ret)? (Class<?>)ret : null;
	}
	
	/**
	 *  Extension for Class.forName(), because primitive
	 *  types are not supported.
	 *  Uses static cache to speed up lookup.
	 *  @param name The class name.
	 *  @return The class.
	 */
	public static Class<?>	classForName(String name, ClassLoader classloader)
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
	public static String getClassName(Class<?> clazz)
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
	 *  Returns generic type name.
	 *  
	 *  @param t The type.
	 *  @param c The class, used when type is variable declaration.
	 *  @return The name of the type.
	 */
	public static String getGenericClassName(Type t, Class<?> c)
	{
		String ret = null;
		if(t instanceof Class)
		{
			ret = SReflect.getClassName(((Class<?>)t));
		}
		else if(t instanceof ParameterizedType)
		{
			// Bug in Android 2.2. see http://code.google.com/p/android/issues/detail?id=6636
			if(!SReflect.isAndroid() ||  SUtil.androidUtils().getAndroidVersion() > 8)
			{
				// Hack!!! Bug in JDK returning the owner type twice!?
//				ret = t.toString();
				
				ParameterizedType	pt	= (ParameterizedType)t;
				Type	raw	= pt.getRawType();
				Type[]	types	= pt.getActualTypeArguments();
				ret	= SReflect.getClassName(((Class<?>)raw));
				for(int i=0; i<types.length; i++)
				{
					if(i==0)
					{
						ret	+= "<";
					}
					
					ret	+= SReflect.getGenericClassName(types[i], null);
					
					if(i==types.length-1)
					{
						ret	+= ">";
					}
					else
					{
						ret	+= ", ";
					}
				}
			}
			else
			{
				ret	= "n/a";
			}
		}
		else if(t instanceof GenericArrayType)
		{
			ret	= SReflect.getGenericClassName(((GenericArrayType)t).getGenericComponentType(), null) + "[]";
		}
		else if(t instanceof WildcardType)
		{
			ret	= "?";
		}
		else if(t instanceof TypeVariable)
		{
			ret	= "?";
		}
		else if(c!=null)
		{
			ret = SReflect.getClassName(c);
		}
		else
		{
			throw new RuntimeException("Unknown type: " + t);
		}
		return ret;
	}

	/**
	 *	Get unqualified class name.
	 *  Also beautifies names of arrays (eg 'String[]' instead of '[LString;').
	 *  @return The unqualified (without package) name of a class.
	 */
	public static String getUnqualifiedClassName(Class clazz)
	{
		String	classname	= getClassName(clazz);
		return getUnqualifiedTypeName(classname);
	}
	
	/**
	 *	Get unqualified type name.
	 *  @return The unqualified (without package) name of a class.
	 */
	public static String getUnqualifiedTypeName(String name)
	{
		if(name==null)
			throw new IllegalArgumentException("Null not allowed.");
		
		int lpos = name.indexOf("<");
		if(lpos>0)
		{
			String left = name.substring(0, lpos);
			
			left = cutPackageFromClassName(makeNiceArrayNotation(left));
			
			String right = name.substring(lpos+1);
			right = getUnqualifiedTypeName(right);
			name = left+"<"+right;
		}
		else
		{
			name = cutPackageFromClassName(makeNiceArrayNotation(name));
		}
		
		int pos = name.lastIndexOf(".");
		if(pos!=-1)
			name = name.substring(pos+1);
		
		return name;
	}

	/**
	 *  Process a type name and replace array notation with nice one.
	 */
	public static String makeNiceArrayNotation(String name)
	{
		int found = 0;
		for(int i=0; i<name.length() && name.charAt(i)=='['; i++)
			found++;
		
		if(found>0)
		{
			int paren = 0;
			for(int i=1; name.charAt(name.length()-i)=='>'; i++)
				paren++;
			
			int start = name.charAt(found)=='L'? found+1: found;
			name = name.substring(start, name.length()-paren);
			
			if(name.endsWith(";"))
				name = name.substring(0, name.length()-1); // cut optionally ; from array, but not if basic array types <[I>
			for(int i=0; i<found; i++)
				name = name+"[]"; 
			for(int i=0; i<paren; i++)
				name = name+">";
		}
		return name;
	}
	
	/**
	 *  Cut package off from classname.
	 */
	public static String cutPackageFromClassName(String name)
	{
		int pos = name.lastIndexOf(".");
		if(pos!=-1)
			name = name.substring(pos+1);
		return name;
	}
	
	public static void main(String[] args)
	{
//		System.out.println(getUnqualifiedTypeName("a.b.c.D<aa.F<ab.V><a.B>>>"));
//		System.out.println(getUnqualifiedTypeName("a.b.c.D<[[Laa.F;<ab.V><a.B>>>"));
//		System.out.println(String[][].class.getName()+" "+getUnqualifiedTypeName(String[][].class.getName()));
	
	
		String a1 = Object[].class.getName();
		String a2 = String[].class.getName();
		String a3 = Integer[].class.getName();
		String a4 = int[].class.getName();
		String a5 = double[].class.getName();
		String a6 = byte[].class.getName();
		String a7 = Byte[].class.getName();
		
		System.out.println(makeNiceArrayNotation(a1));
		System.out.println(makeNiceArrayNotation(a2));
		System.out.println(makeNiceArrayNotation(a3));
		System.out.println(makeNiceArrayNotation(a4));
		System.out.println(makeNiceArrayNotation(a5));
		System.out.println(makeNiceArrayNotation(a6));
		System.out.println(makeNiceArrayNotation(a7));
	}
	
	/**
	 *	Get inner class name.
	 *  @return The inner class's name (without declaring class).
	 */
	public static String getInnerClassName(Class<?> clazz)
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
	public static String getPackageName(Class<?> clazz)
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
	 *  Select the first available resource from a choice of potential resources.
	 *  Allows, e.g. swapping alternative implementations in the classpath. 
	 */
	public static String chooseAvailableResource(String... choices)
	{
		return chooseAvailableResource(SReflect.class.getClassLoader(), choices);
	}
	
	/**
	 *  Select the first available resource from a choice of potential resources.
	 *  Allows, e.g. swapping alternative implementations in the classpath. 
	 */
	public static String chooseAvailableResource(ClassLoader cl, String... choices)
	{
		String	ret	= null;
		for(String choice: choices)
		{
			if(choice.endsWith(".class")) 
			{
				String clname = choice.startsWith("/") ? choice.substring(1): choice;
				clname =  clname.substring(0, clname.length()-6).replace("/",".");
				Class<?> clazz = classForName0(clname, cl);
				if(clazz!=null)
				{
					ret	= choice;
					break;
				}
			} 
			else 
			{
				InputStream is = SUtil.getResource0(choice, cl);
				if(is!=null)
				{
					ret	= choice;
					try 
					{
						is.close();
					} 
					catch (IOException e) 
					{
					}
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the generic signature of a method.
	 *  @param method The method.
	 *  @return The signature name.
	 */
	public static String getMethodSignature(Method method)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			Type rtype = method.getGenericReturnType();
			buf.append(getUnqualifiedTypeName(rtype.toString())).append(" ");
		}
		catch(Exception e)
		{
			buf.append("n/a ");
		}
		buf.append(method.getName()).append("(");
		try
		{
			Type[] ptypes = method.getGenericParameterTypes();
			for(int i=0; i<ptypes.length; i++)
			{
				// why unqualified?
				buf.append(getUnqualifiedTypeName(ptypes[i].toString()));
				if(i+1<ptypes.length)
					buf.append(", ");
			}
		}
		catch(Exception e)
		{
			buf.append("n/a");
		}
		buf.append(")");
		return buf.toString();
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
	public static Method	getMethod(Class<?> clazz, String name, Class<?>[] types)
	{
		Method	meth	= null;
		Method[]	ms	= getMethods(clazz, name);
		for(int i=0; i<ms.length; i++)
		{
			Class<?>[]	ptypes	= ms[i].getParameterTypes();
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
	public static Method[] getMethods(Class clazz, String name)
	{
		Map	map	= (Map)methodcache.get(clazz);
		if(map==null)
		{
			map	= SCollection.createHashMap();
			methodcache.put(clazz, map);
		}

		Method[] ret = (Method[])map.get(name);
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
//		if(name.indexOf("receive")!=-1)
//			System.out.println(Arrays.toString(ret));
		return ret;
	}
	
	/**
	 *  Get all method(s) of the class by name,
	 *  including public, protected and private methods.
	 *  
	 *  @param clazz	The class to search.
	 *  @param name	The name of the method to search for.
	 *  @return	The method(s).
	 */
	public static Method[] getAllMethods(Class<?> clazz, String name)
	{
		Map	map	= (Map)allmethodcache.get(clazz);
		if(map==null)
		{
			map	= SCollection.createHashMap();
			allmethodcache.put(clazz, map);
		}

		Method[] ret = (Method[])map.get(name);
		if(ret==null)
		{
			Method[]	ms	= getAllMethods(clazz);
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
//		if(name.indexOf("receive")!=-1)
//			System.out.println(Arrays.toString(ret));
		return ret;
	}
	
	/**
	 *  Get all methods of a class including public, protected
	 *  and private methods of the class and its superclasses. 
	 *  @return Array of all methods starting from the current
	 *  	class upwards towards Object.class.
	 */
	public static Method[]	getAllMethods(Class clazz)
	{
		List<Method>	ret	= new ArrayList<Method>();
		Class	cls	= clazz;

		while(cls!=null)
		{
			try
			{
				ret.addAll(Arrays.asList(cls.getDeclaredMethods()));
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
			cls	= cls.getSuperclass();
		}

		return ret.toArray(new Method[ret.size()]);
	}

	/**
	 *  Get all fields of a class including public, protected
	 *  and private fields of the class and its superclasses.
	 *  @return Array of all fields starting from the current
	 *  	class upwards towards Object.class.
	 */
	public static Field[]	getAllFields(Class clazz)
	{
		List<Field>	ret	= new ArrayList<Field>();
		Class	cls	= clazz;

		while(cls!=null)
		{
			try
			{
				ret.addAll(Arrays.asList(cls.getDeclaredFields()));
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
			cls	= cls.getSuperclass();
		}

		return ret.toArray(new Field[ret.size()]);
	}
	
	/**
	 *  Find a class.
	 *  When the class name is not fully qualified, the list of
	 *  imported packages is searched for the class.
	 *  @param clname	The class name.
	 *  @param imports	The comma separated list of imported packages.
	 *  @throws ClassNotFoundException when the class is not found in the imports.
	 */
	public static <T> Class<T>	findClass(String clname, String[] imports, ClassLoader classloader)
		throws ClassNotFoundException
	{
		Class	clazz	= findClass0(clname, imports, classloader);

		if(clazz==null)
		{
			throw new ClassNotFoundException("Class "+clname+" not found in imports"); //: "+SUtil.arrayToString(imports));
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
	public static <T> Class<T>	findClass0(String clname, String[] imports, ClassLoader classloader)
	{
		if(clname==null)
			throw new IllegalArgumentException("Classname must not null");
		
		clname = makeNiceArrayNotation(clname);
		
		if(classloader==null)
			classloader = SReflect.class.getClassLoader();
		
		Class<T> clazz = (Class<T>)classForName0(clname, classloader);
//		Class<T> clazz = (Class<T>)classcache.get(new Tuple2<String, Integer>(clname, hash));

		// Try to find in imports.
		if(clazz==null && imports!=null)
		{
			Integer hash = classloader.hashCode();
			for(int i=0; clazz==null && i<imports.length; i++)
			{
				Tuple2<String, Integer> key = new Tuple2<String, Integer>(imports[i]+clname, hash);
				clazz = (Class<T>)classcache.get(key);
				if(clazz==null)	
				{
					String	clwoa	=	clname;
					String	brackets	= "";
					while(clwoa.endsWith("[]"))
					{
						clwoa	= clwoa.substring(0, clwoa.length()-2);
						brackets	+= "[]";
					}
					
					// Package import
					if(imports[i].endsWith(".*"))
					{
						clazz	= (Class<T>)classForName0(imports[i].substring(0, imports[i].length()-1) + clname, classloader);
					}
					// Class import
					else if(imports[i].endsWith(clwoa))
					{
						clazz	= (Class<T>)classForName0(imports[i]+brackets, classloader);
					}
					classcache.put(key, clazz);
				}
			}
		}
		
		// Try java.lang (imported by default).
		if(clazz==null)
		{
			Integer hash = classloader.hashCode();
			Tuple2<String, Integer> key = new Tuple2<String, Integer>("java.lang.*"+clname, hash);
			clazz = (Class<T>)classcache.get(key);
			if(clazz==null)
			{
				clazz	= (Class<T>)classForName0("java.lang." + clname, classloader);
				if(clazz!=null)
				{
					classcache.put(key, clazz);
				}
			}
		}

//		if(clazz==null)
//		{
//			System.err.println("Class not found: "+clname+", "+SUtil.arrayToString(imports));
//		}
		
//		System.out.println("classcache: "+classcache.size());//+" "+classcache.keySet());
//		Map<Integer, Integer> res = new HashMap<Integer, Integer>();
//		for(Tuple2<String, Integer> tup: classcache.keySet())
//		{
//			Integer cnt = res.get(tup.getSecondEntity());
//			if(cnt==null)
//			{
//				res.put(tup.getSecondEntity(), Integer.valueOf(1));
//			}
//			else
//			{
//				res.put(tup.getSecondEntity(), Integer.valueOf(cnt.intValue()+1));
//			}
//		}
//		System.out.println("found: "+res);
		
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
			boolean	varargs	= false;
			if(paramtypes[i].length<=argtypes.length)
			{
				for(int j=0; j<paramtypes[i].length && matches[i]!=-1; j++)
				{
					// Check if parameter type matches argument type.
					if(argtypes[j]!=null)
					{
						// No match.
						if(!SReflect.isSupertype(paramtypes[i][j], argtypes[j]))
						{
							// Special case varargs: last parameter is array and remaining arguments match array base type.
							if(j==paramtypes[i].length-1 && paramtypes[i][j].isArray())
							{
								varargs	= true;
								Class<?>	basetype	= paramtypes[i][j].getComponentType();
								for(int k=j; varargs && k<argtypes.length; k++)
								{
									varargs	= argtypes[k]==null || SReflect.isSupertype(basetype, argtypes[k]);
								}
							}
							
							if(!varargs)
							{
								matches[i]	= -1;
							}
						}

						// Subtype or exact match.
						else
						{
							// Increase quality.
							matches[i]	+= getWrappedType(paramtypes[i][j])==getWrappedType(argtypes[j]) ? 2 : 1;
							if(matches[i]>hq)
								hq	= matches[i];
						}
					}
				}
				
				if(!varargs && paramtypes[i].length!=argtypes.length)
				{
					matches[i]	= -1;
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
	 *  Map arguments to parameters using varargs, if necessary.
	 */
	public static Object[]	fillArguments(Object[] args, Class<?>[] paramtypes)
	{
		// Special handling for varargs, otherwise just return original args
		if(paramtypes.length>0 && paramtypes[paramtypes.length-1].isArray() && (args.length>paramtypes.length || args[paramtypes.length-1]!=null && !SReflect.isSupertype(paramtypes[paramtypes.length-1], args[paramtypes.length-1].getClass())))
		{
			Class<?> basetype	= paramtypes[paramtypes.length-1].getComponentType();
			if(args.length>paramtypes.length || args[paramtypes.length-1]!=null && SReflect.isSupertype(basetype, args[paramtypes.length-1].getClass()))
			{
				Object	varargs	= Array.newInstance(basetype, args.length-(paramtypes.length-1));
				for(int i=0; i<Array.getLength(varargs); i++)
				{
					Array.set(varargs, i, args[i+(paramtypes.length-1)]);
				}
				
				if(args.length!=paramtypes.length)
				{
					Object[]	tmp	= new Object[paramtypes.length];
					System.arraycopy(args, 0, tmp, 0, paramtypes.length-1);
					args	= tmp;
				}
				args[paramtypes.length-1]	= varargs;
			}
		}
		
		return args;
	}
	
	/**
	 *  Check if a class is a supertype of, or the same as another class.
	 *  Maps basic types to wrapped types, and respects
	 *	the basic type hierarchy.
	 *  @param clazz1	The assumed supertype.
	 *  @param clazz2	The assumed subtype.
	 *  @return True, if clazz1 is a supertype of, or the same as clazz2.
	 */
	public static boolean	isSupertype(Class<?> clazz1, Class<?> clazz2)
	{
		if(clazz1==null || clazz2==null)
			return false;
		
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
				value	= Integer.valueOf(((Character)value).charValue());
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
					value	= Double.valueOf(num.doubleValue());
				}
				else if(clazz.equals(Float.class))
				{
					value	= Float.valueOf(num.floatValue());
				}
				else if(clazz.equals(Long.class))
				{
					value	= Long.valueOf(num.longValue());
				}
				else if(clazz.equals(Integer.class))
				{
					value	= Integer.valueOf(num.intValue());
				}
				else if(clazz.equals(Short.class))
				{
					value	= Short.valueOf(num.shortValue());
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
	public static <T> Iterable<T> getIterable(Object collection)
	{
		return new IterableIteratorWrapper<T>((Iterator<T>)getIterator(collection));
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
	public static <T> Iterator<T> getIterator(Object collection)
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
				
				public boolean	hasNext()
				{
					return i<Array.getLength(array);
				}
				
				public Object	next()
				{
					if(Array.getLength(array)>i)
					{
						return Array.get(array, i++);
					}
					else
					{
						throw new NoSuchElementException();
					}
				}
				
				public void	remove()
				{
					throw new UnsupportedOperationException("remove() not supported for arrays");
				}
			};
		}
		else
		{
			throw new IllegalArgumentException("Cannot iterate over "+collection);
		}
	}
	
	/**
	 *  Test if object is some kind of collection.
	 *  @param obj The object.
	 *  @return True if is iterable.
	 */
	public static boolean isIterable(Object obj)
	{
		return obj instanceof Iterator 
			|| obj instanceof Enumeration
			|| obj instanceof Collection 
			|| obj instanceof Map
			|| obj!=null && obj.getClass().isArray();
	}
	
	/**
	 *  Test if class is some kind of collection.
	 *  @param clazz The class.
	 *  @return True if is iterable.
	 */
	public static boolean isIterableClass(Class clazz)
	{
		return Iterator.class.isAssignableFrom(clazz)
			|| Enumeration.class.isAssignableFrom(clazz)
			|| Collection.class.isAssignableFrom(clazz) 
			|| Map.class.isAssignableFrom(clazz)
			|| clazz.isArray();
	}
	
	/**
	 *  Get the component type of a class that is some kind of collection.
	 *  @param clazz The class.
	 *  @return The component type, i.e. type of contained elements as defined in collection class.
	 */
	public static Class getIterableComponentType(Type type)
	{
		Class	ret	= null;
		if(type instanceof Class<?> && ((Class<?>)type).isArray())
		{
			ret	= ((Class<?>)type).getComponentType();
		}
		else if (type instanceof ParameterizedType)
		{
			Type[]	args	= ((ParameterizedType)type).getActualTypeArguments();
			if(args.length==1 && args[0] instanceof Class)	// Iterator, Enumeration, Collection
			{
				ret	= (Class)args[0];
			}
			else if(args.length==2 && args[1] instanceof Class)	// Map (values)
			{
				ret	= (Class)args[1];
			}
		}
		return ret;
	}

	/**
	 *  Create an fill an object of a class that is some kind of collection.
	 *  @param clazz The class.
	 *  @param values The values.
	 */
	public static Object createComposite(Type type, Collection<?> values)
	{
		Type	tmp	= type;
		while(tmp instanceof ParameterizedType)
		{
			tmp	= ((ParameterizedType)tmp).getRawType();
		}
		Class<?>	clazz	= (Class<?>)tmp;
		Object	ret	= null;
		if(clazz.isArray())
		{
			ret	= Array.newInstance(getIterableComponentType(type), values.size());
			Iterator<?>	it	= values.iterator();
			for(int i=0; i<values.size(); i++)
			{
				Array.set(ret, i, it.next());
			}
		}
		else if(isSupertype(Collection.class, clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
		{
			try
			{
				ret	= clazz.newInstance();
				((Collection)ret).addAll(values);
			}
			catch(Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		else if(isSupertype(Set.class, clazz))
		{
			ret	= new LinkedHashSet(values);
		}
		else if(isSupertype(List.class, clazz))
		{
			ret	= new ArrayList(values);
		}
		else
		{
			throw new IllegalArgumentException("Unsupported composite type: "+type);
		}
		return ret;
	}

	protected static final Object[] EMPTY_ARRAY = new Object[0];
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
	 *  Get the super interfaces.
	 *  @param interfaces The interfaces
	 *  @return All super interfaces.
	 */
	public static Class[] getSuperInterfaces(Class[] interfaces) 
	{
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for(int i=0; i<interfaces.length; i++) 
		{
			ret.add(interfaces[i]);
		}
		
		for(int i=0; i<ret.size(); i++)
		{
			Class<?>[]	ifs	= ret.get(i).getInterfaces();
			for(int j=0; j<ifs.length; j++) 
			{
				ret.add(ifs[j]);
			}
		}
			
		return (Class[])ret.toArray(new Class[ret.size()]);
	}
	
	/**
	 *  Get default value for basic types.
	 */
	public static Object getDefaultValue(Class clazz)
	{
		Object ret = null;
		
		if(clazz!=null && SReflect.isBasicType(clazz))
			// changed *.class to *.TYPE due to javaflow bug
		{
			if(clazz==Boolean.TYPE)
				ret	= Boolean.FALSE;
			else if(clazz==Byte.TYPE)
				ret	= Byte.valueOf((byte)0);
			else if(clazz==Character.TYPE)
				ret	= Character.valueOf((char)0);
			else if(clazz==Short.TYPE)
				ret	= Short.valueOf((short)0);
			else if(clazz==Double.TYPE)
				ret	= Double.valueOf(0);
			else if(clazz==Float.TYPE)
				ret	= Float.valueOf(0);
			else if(clazz==Long.TYPE)
				ret	= Long.valueOf(0);
			else if(clazz==Integer.TYPE)
				ret	= Integer.valueOf(0);
		}
		
		return ret;
	}
	
	/** Reflective access to Class.getModule(). */
	protected static final MethodHandle GET_MODULE;
	
	/** Reflective access to Module.isExported(). */
	protected static final MethodHandle IS_EXPORTED;
	static
	{
		MethodHandle getmodulemh = null;
		MethodHandle isexportedmh = null;
		try
		{
			Method getmodule = Class.class.getMethod("getModule");
			getmodulemh = MethodHandles.lookup().unreflect(getmodule);
			Class<?> moduleclazz = Class.forName("java.lang.Module");
			Method isexported = moduleclazz.getMethod("isExported", String.class);
			isexportedmh = MethodHandles.lookup().unreflect(isexported);
		}
		catch (Exception e)
		{
		}
		GET_MODULE = getmodulemh;
		IS_EXPORTED = isexportedmh;
	}
	
	/**
	 *  Gets a method that is exported by the module.
	 *  
	 *  @param clazz Class with the method.
	 *  @param name Name of the method.
	 *  @param params Method parameters.
	 *  @return The method or null if not found.
	 */
	public static final Method getExportedMethod(Class<?> clazz, String name, Class<?>... params)
	{
		params = params == null ? new Class<?>[0] : params;
		Method[] methods = SReflect.getAllMethods(clazz);
		for (Method m : methods)
		{
			if (m.getName().equals(name) && Arrays.equals(params, m.getParameterTypes()))
			{
				if (isExported(m.getDeclaringClass()))
					return m;
			}
		}
		return null;
	}
	
	/**
	 *  Tests if the class is part of a package that the  containing module has exported.
	 *  @param clazz The class.
	 *  @return True, if exported.
	 */
	public static final boolean isExported(Class<?> clazz)
	{
		if (GET_MODULE != null)
		{
			try
			{
				Object module = GET_MODULE.invoke(clazz);
				return (boolean) IS_EXPORTED.invoke(module, clazz.getPackage().getName());
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				throw SUtil.throwUnchecked(t);
			}
		}
		else
		{
			// Java 8 has no modules so everything is exported.
			return true;
		}
	}
	
	/**
	 *  Get the current method name from the caller.
	 *  @return The method name. 
	 */
	public static String getMethodName() 
	{
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
	
	/**
	 *  Scan for classes that fulfill certain criteria as specified by the file and classfilters.
	 */
	public static Class<?>[] scanForClasses(ClassLoader classloader, IFilter filefilter, IFilter classfilter, boolean includebootpath)
	{
		return scanForClasses(SUtil.getClasspathURLs(classloader, includebootpath).toArray(new URL[0]), classloader, filefilter, classfilter);
	}
	
	/**
	 *  Scan for classes that fulfill certain criteria as specified by the file and classfilters.
	 */
	public static Class<?>[] scanForClasses(URL[] urls, ClassLoader classloader, IFilter filefilter, IFilter classfilter)
	{
		Set<Class<?>>	ret	= new HashSet<Class<?>>();
		String[] facs = scanForFiles(urls, filefilter);
		try
		{
			for(int i=0; i<facs.length; i++)
			{
				try
				{
					String	clname	= facs[i].substring(0, facs[i].length()-6).replace('/', '.');
	//				System.out.println("Found candidate: "+clname);
					Class<?>	fac	= SReflect.findClass0(clname, null, classloader);
					
					if(fac!=null && classfilter.filter(fac))
					{
						ret.add(fac);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println(facs[i]);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret.toArray(new Class[ret.size()]);
	}

	/**
	 *  Scan for files in a given list of urls.
	 */
	public static String[] scanForFiles(URL[] urls, IFilter<Object> filter)
	{
		Set<String>	ret	= new HashSet<String>();
		for(int i=0; i<urls.length; i++)
		{
//			System.out.println("Scanning: "+entry);
			try
			{
//				System.out.println("url: "+urls[i].toURI());
				File f = new File(urls[i].toURI());
				if(f.getName().endsWith(".jar"))
				{
					JarFile	jar = null;
					try
					{
						jar	= new JarFile(f);
						for(Enumeration<JarEntry> e=jar.entries(); e.hasMoreElements(); )
						{
							JarEntry je	= e.nextElement();
							if(filter.filter(je))	
							{
								ret.add(je.getName());
							}
						}
						jar.close();
					}
					catch(Exception e)
					{
//						System.out.println("Error opening jar: "+urls[i]+" "+e.getMessage());
					}
					finally
					{
						if(jar!=null)
						{
							jar.close();
						}
					}
				}
				else if(f.isDirectory())
				{
					scanDir(urls, f, filter, ret, new ArrayList<String>());
//					throw new UnsupportedOperationException("Currently only jar files supported: "+f);
				}
			}
			catch(Exception e)
			{
				System.out.println("scan problem with: "+urls[i]);
//				e.printStackTrace();
			}
		}
		
		return ret.toArray(new String[ret.size()]);
	}
	
	// This cache cannot really work due to a key with plain objects like filters (other filter object = new entry)
//	protected static Map<Tuple3<Set<URL>, IFilter<Object>, IFilter<ClassInfo>>, Set<ClassInfo>> CICACHE	= Collections.synchronizedMap(new LinkedHashMap<>());
	
	/**
	 *  Scan for component classes in the classpath.
	 */
	public static Set<ClassInfo> scanForClassInfos(URL[] urls, IFilter<Object> filefilter, IFilter<ClassInfo> classfilter)
	{
//		Tuple3<Set<URL>, IFilter<Object>, IFilter<ClassInfo>>	key
//			= new Tuple3<>(new HashSet<>(Arrays.asList(urls)), filefilter, classfilter);
		
		Set<ClassInfo> ret = null;//CICACHE.get(key);
		if(ret==null)
		{
			ret	= new LinkedHashSet<>();
			
			if(filefilter==null)
				filefilter = new jadex.commons.FileFilter("$", false, ".class");
			Map<String, Set<String>> files = SReflect.scanForFiles2(urls, filefilter);
			
			//int cnt = 0;
			for(Map.Entry<String, Set<String>> entry: files.entrySet())
			{
				String jarname = entry.getKey();
				if(jarname!=null)
				{
					try(JarFile jar	= new JarFile(jarname))
					{
						for(String jename: entry.getValue())
						{
							JarEntry je = jar.getJarEntry(jename);
							InputStream is = jar.getInputStream(je);
							ClassInfo ci = SClassReader.getClassInfo(jarname+jename, is, new Date(je.getLastModifiedTime().toMillis()));
							if(classfilter.filter(ci))
							{
								ret.add(ci);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					for(String filename: entry.getValue())
					{
						File inputfile = new File(filename);
						try(FileInputStream is = new FileInputStream(inputfile))
						{
							ClassInfo ci = SClassReader.getClassInfo(filename, is, new Date(inputfile.lastModified()));
							if(classfilter.filter(ci))
							{
								ret.add(ci);
							}
	//						System.out.println(cnt++);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
//			CICACHE.put(key, ret);
//			System.out.println("scanned class infos cache size: "+CICACHE.size());
		}
		
		return ret;
	}
	
	/**
	 *  Scan for component classes in the classpath.
	 */
	public static Set<ClassFileInfo> scanForClassFileInfos(URL[] urls, IFilter<Object> filefilter, IFilter<ClassFileInfo> classfilter)
	{
//		Tuple3<Set<URL>, IFilter<Object>, IFilter<ClassInfo>>	key
//			= new Tuple3<>(new HashSet<>(Arrays.asList(urls)), filefilter, classfilter);
		
//		Set<ClassInfo> ret = CICACHE.get(key);
		
		Set<ClassFileInfo> ret = null;
		
		if(ret==null)
		{
			ret	= new LinkedHashSet<>();
			
			if(filefilter==null)
				filefilter = new jadex.commons.FileFilter("$", false, ".class");
			Map<String, Set<String>> files = SReflect.scanForFiles2(urls, filefilter);
			
			//int cnt = 0;
			for(Map.Entry<String, Set<String>> entry: files.entrySet())
			{
				String jarname = entry.getKey();
				if(jarname!=null)
				{
					try(JarFile jar	= new JarFile(jarname))
					{
						for(String jename: entry.getValue())
						{
							JarEntry je = jar.getJarEntry(jename);
							InputStream is = jar.getInputStream(je);
							ClassInfo ci = SClassReader.getClassInfo(jarname+jename, is, new Date(je.getLastModifiedTime().toMillis()));
							ClassFileInfo cfi = new ClassFileInfo(ci, jarname+jename);
							if(classfilter.filter(cfi))
							{
								ret.add(cfi);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					for(String filename: entry.getValue())
					{
						File inputfile = new File(filename);
						try(FileInputStream is = new FileInputStream(inputfile))
						{
							ClassInfo ci = SClassReader.getClassInfo(filename, is, new Date(inputfile.lastModified()));
							ClassFileInfo cfi = new ClassFileInfo(ci, filename);
							if(classfilter.filter(cfi))
							{
								ret.add(new ClassFileInfo(ci, filename));
							}
	//						System.out.println(cnt++);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
//			CICACHE.put(key, ret);
//			System.out.println("scanned class infos cache size: "+CICACHE.size());
		}
		
		return ret;
	}
	
	/**
	 *  Scan for files in a given list of urls.
	 */
	public static Map<String, Set<String>> scanForFiles2(URL[] urls, IFilter<Object> filter)
	{
		Map<String, Set<String>> ret = new HashMap<>();
		Set<String>	topset = new HashSet<String>();
		
		for(int i=0; i<urls.length; i++)
		{
//			System.out.println("Scanning: "+entry);
			try
			{
//				System.out.println("url: "+urls[i].toURI());
				File f = new File(urls[i].toURI());
				if(f.getName().endsWith(".jar"))
				{
					JarFile	jar = null;
					try
					{
						jar	= new JarFile(f);
						Set<String>	set	= new HashSet<String>();
						
						for(Enumeration<JarEntry> e=jar.entries(); e.hasMoreElements(); )
						{
							JarEntry je	= e.nextElement();
							if(filter.filter(je))	
							{
								set.add(je.getName());
							}
						}
						jar.close();
					
						if(set.size()>0)
							ret.put(f.getAbsolutePath(), set);
					}
					catch(Exception e)
					{
//						System.out.println("Error opening jar: "+urls[i]+" "+e.getMessage());
					}
					finally
					{
						if(jar!=null)
						{
							jar.close();
						}
					}
				}
				else if(f.isDirectory())
				{
					scanDir2(urls, f, filter, topset, new ArrayList<String>());
//					throw new UnsupportedOperationException("Currently only jar files supported: "+f);
				}
			}
			catch(Exception e)
			{
				System.out.println("scan problem with: "+urls[i]);
//				e.printStackTrace();
			}
		}
		
		if(topset.size()>0)
			ret.put(null, topset);
		
		return ret;
	}
	
	/**
	 *  Scan directories.
	 */
	public static void scanDir(URL[] urls, File file, IFilter<Object> filter, Collection<String> results, List<String> donedirs)
	{
		File[] files = file.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				return !f.isDirectory();
			}
		});
		for(File fi: files)
		{
			if(fi.getName().endsWith(".class") && filter.filter(fi))
			{
				String fn = SUtil.convertPathToPackage(fi.getAbsolutePath(), urls);
//				System.out.println("fn: "+fi.getName());
				results.add(fn+"."+fi.getName());
			}
		}
		
		if(file.isDirectory())
		{
			donedirs.add(file.getAbsolutePath());
			File[] sudirs = file.listFiles(new FileFilter()
			{
				public boolean accept(File f)
				{
					return f.isDirectory();
				}
			});
			
			for(File dir: sudirs)
			{
				if(!donedirs.contains(dir.getAbsolutePath()))
				{
					scanDir(urls, dir, filter, results, donedirs);
				}
			}
		}
	}
	
	/**
	 *  Scan directories.
	 */
	public static void scanDir2(URL[] urls, File file, IFilter<Object> filter, Collection<String> results, List<String> donedirs)
	{
		File[] files = file.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				return !f.isDirectory();
			}
		});
		for(File fi: files)
		{
			if(filter.filter(fi))
			{
				//String fn = SUtil.convertPathToPackage(fi.getAbsolutePath(), urls);
//				System.out.println("fn: "+fi.getName());
				results.add(fi.getAbsolutePath());
			}
		}
		
		if(file.isDirectory())
		{
			donedirs.add(file.getAbsolutePath());
			File[] sudirs = file.listFiles(new FileFilter()
			{
				public boolean accept(File f)
				{
					return f.isDirectory();
				}
			});
			
			for(File dir: sudirs)
			{
				if(!donedirs.contains(dir.getAbsolutePath()))
				{
					scanDir2(urls, dir, filter, results, donedirs);
				}
			}
		}
	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
////		System.out.println(getMethodName());
//		
//		IFilter<Object> filefilter = new IFilter<Object>()
//		{
//			public boolean filter(Object obj)
//			{
//				String	fn	= "";
//				if(obj instanceof File)
//				{
//					File	f	= (File)obj;
//					fn	= f.getName();
//				}
//				else if(obj instanceof JarEntry)
//				{
//					JarEntry	je	= (JarEntry)obj;
//					fn	= je.getName();
//				}
//				return fn.indexOf("$")==-1;// && fn.endsWith(".class");
//			}
//		};
//		final int[] cnt = new int[1]; 
//		IFilter<Class<?>> classfilter = new IFilter<Class<?>>()
//		{
//			public boolean filter(Class<?> clazz)
//			{
////				System.out.println("testing class: "+(cnt[0]++)+clazz);
//						
////							Class<?> cl = (Class<?>)obj;
//				boolean ret = SReflect.getInnerClassName(clazz).startsWith("Mouse");
////							boolean ret = SReflect.isSupertype(IControlCenterPlugin.class, cl) && !(cl.isInterface() || Modifier.isAbstract(cl.getModifiers()));
//				
//				return ret;
//			}
//		};
//		
//		asyncScanForClasses(null, filefilter, classfilter, 5, true).addResultListener(new IIntermediateResultListener<Class<?>>()
//		{
//			public void intermediateResultAvailable(Class<?> result)
//			{
//				System.out.println("found: "+result);
//			}
//			public void finished()
//			{
//				System.out.println("fini");
//			}
//			public void resultAvailable(Collection<Class<?>> result)
//			{
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//			}
//		});
//	}
	
	/** Cached flag for android check. */
	protected static volatile Boolean isAndroid;

	/** Flag set by testcases that indicates we're testing android projects in a desktop environment. **/
	protected static Boolean isAndroidTesting;

	/** private setter that can be made accessible for robolectric testcases. **/
	protected static void setAndroid(boolean isAndroidFlag, boolean isAndroidTestingFlag) {
		synchronized (SReflect.class) {
			isAndroid = isAndroidFlag;
			isAndroidTesting = isAndroidTestingFlag;
		}
	}

	public static Boolean isAndroidTesting() {
		return isAndroidTesting;
	}

	/**
	 *  Test if running on android.
	 */
	public static boolean	isAndroid()
	{
		if(isAndroid==null)
		{
			synchronized (SReflect.class)
			{
				if (isAndroid==null)
				{
					String vmName = System.getProperty("java.vm.name");
					String vmVendor = System.getProperty("java.vm.vendor");
					String vendorUrl = System.getProperty("java.vendor.url");

					isAndroid =
							("Dalvik".equalsIgnoreCase(vmName) ||
							"The Android Project".equalsIgnoreCase(vmVendor) ||
							"http://www.android.com".equalsIgnoreCase(vendorUrl));
				}
			}
		}
		return isAndroid.booleanValue();
	}
	
	private class NotFound
	{
	}
}


