package nuggets;


import java.util.HashMap;
import java.util.Map;

import nuggets.delegate.ADelegate;
import nuggets.delegate.DBoolean;
import nuggets.delegate.DBooleanArray;
import nuggets.delegate.DBooleanObject;
import nuggets.delegate.DBooleanObjectArray;
import nuggets.delegate.DByte;
import nuggets.delegate.DByteArray;
import nuggets.delegate.DByteObject;
import nuggets.delegate.DByteObjectArray;
import nuggets.delegate.DChar;
import nuggets.delegate.DCharArray;
import nuggets.delegate.DCharObject;
import nuggets.delegate.DCharObjectArray;
import nuggets.delegate.DClass;
import nuggets.delegate.DDouble;
import nuggets.delegate.DDoubleArray;
import nuggets.delegate.DDoubleObject;
import nuggets.delegate.DDoubleObjectArray;
import nuggets.delegate.DFloat;
import nuggets.delegate.DFloatArray;
import nuggets.delegate.DFloatObject;
import nuggets.delegate.DFloatObjectArray;
import nuggets.delegate.DInteger;
import nuggets.delegate.DIntegerArray;
import nuggets.delegate.DIntegerObject;
import nuggets.delegate.DIntegerObjectArray;
import nuggets.delegate.DLong;
import nuggets.delegate.DLongArray;
import nuggets.delegate.DLongObject;
import nuggets.delegate.DLongObjectArray;
import nuggets.delegate.DObjectArray;
import nuggets.delegate.DShort;
import nuggets.delegate.DShortArray;
import nuggets.delegate.DShortObject;
import nuggets.delegate.DShortObjectArray;
import nuggets.delegate.DString;
import nuggets.delegate.DStringBuffer;
import nuggets.util.IdentityHashMap;


/** PersistenceHelper class for Grit objects
 * @author walczak
 * @since  Dec 5, 2005
 */
public class PersistenceHelper
{
	private static final IDelegate	DEFAULT_DELEGATE	= new ADelegate();

	/** 
	 * @param flag
	 * @return "true":"false"
	 */
	public static String toString(boolean flag)
	{
		return flag ? "true" : "false";
	}

	/** 
	 * @param clazz
	 * @return the name of class
	 */
	public static String toString(Class clazz)
	{
		return clazz.getName();
	}


	/**
	 * This will test for a persistence delegate of this class or of all
	 * interfaces of this class. It will create a delegate using BeanDelegateFactory
	 * @param clazz
	 * @return the persistence delegate or null
	 */
	public static IDelegate getDelegate(final Class clazz, ClassLoader classloader)
	{
		IDelegate m = lookUpDelegate(clazz);
		if(m == null)
		{
			m = createPersistenceDelegate(clazz, classloader);
			registerDelegate(clazz, m);
		}

		return m;
	}


	/** This method looks up the delegate in the table and returns a DEFAULT_DELEGATE if not found
	 * @param clazz
	 * @return the defeault delegat for this class 
	 */
	public static IDelegate getDefaultDelegate(Class clazz)
	{
		IDelegate m = lookUpDelegate(clazz);
		if(m == null) return DEFAULT_DELEGATE;
		return m;
	}

	/** 
	 * @param clazz
	 * @return the delegate found in delegate list or ADelagate
	 */
	static IDelegate lookUpDelegate(Class clazz)
	{
		Class c = clazz;
		IDelegate m = null;

		m = (IDelegate)fast_map.get(c);
		if(m == null)
		{
			if(clazz.isArray()) return ARRAY_DELEGATE;
			// hervest the interfaces
			Class[] ints = c.getInterfaces();
			for(int i = 0; i < ints.length; i++)
			{
				m = (IDelegate)fast_map.get(ints[i]);
				if(m != null)
				{
					registerDelegate(clazz, m);
					break;
				}
			}
		}
		return m;
	}
	
	/** 
	 * @param clazz
	 * @return a persistance delegate for the specified class
	 */
	private static IDelegate createPersistenceDelegate(Class clazz, ClassLoader classloader)
	{
		try
		{
			Map props = INTROSPECTOR.getBeanProperties(clazz);
			
			return GENERATOR.generateDelegate(clazz, props, classloader);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	
/*----------------------------- decoupled ----------------------------------------*/

    static final IBeanIntrospector	INTROSPECTOR			= getIntrospector(); 	
    
    static final IDelegateGenerator  GENERATOR               = getGenerator();

	/** 
	 * @return a delegate generator
	 */
	private static IDelegateGenerator getGenerator()
	{
		try 
		{
//			return(IDelegateGenerator)Class.forName("nuggets.JaninoGenerator"
//				/*, true, Thread.currentThread().getContextClassLoader()*/).newInstance();
		}
		catch(Exception e)
		{ 
			// nop
		}
		// fail safe
		return new ReflectionGenerator();
	}

	/** 
	 * @return an introspector
	 */
	private static IBeanIntrospector getIntrospector()
	{
		
		try
		{
			return (IBeanIntrospector)Class.forName("nuggets.BeanInfoIntrospector"/*, true, Thread.currentThread().getContextClassLoader()*/).newInstance();
		}
		catch(Exception e)
		{ 
			// nop
		}
		// fail safe
		return new ReflectionIntrospector();
	}

	/** This method looks up the delegate in the table and returns a DEFAULT_DELEGATE if not found
	 * @param clazz
	 * @return the defeault delegat for this class 
	 */
	public static IDelegate getDefaultDelegate(String clazz)
	{
		IDelegate m = lookUpDelegate(clazz);
		if(m == null) return DEFAULT_DELEGATE;
		return m;
	}

	/** 
	 * @param clazz
	 * @return the delegate found in delegate list or ADelagate
	 */
	static IDelegate lookUpDelegate(String clazz)
	{
		return (IDelegate)string_map.get(clazz);
	}

	final static private IDelegate			ARRAY_DELEGATE	=new DObjectArray();

	final static private IdentityHashMap	fast_map		= new IdentityHashMap();

	final static private HashMap			string_map		= new HashMap();

	static
	{
		registerDelegate(INugget.class, new nuggets.delegate.DNugget());
		// default types
		registerDelegate(Boolean.class,  new DBooleanObject());
		registerDelegate(boolean.class,  new DBoolean());
		registerDelegate(Byte.class,     new DByteObject());
		registerDelegate(byte.class,     new DByte());
		registerDelegate(Character.class,new DCharObject());
		registerDelegate(char.class,     new DChar());
		registerDelegate(Double.class,   new DDoubleObject());
		registerDelegate(double.class,   new DDouble());
		registerDelegate(Float.class,    new DFloatObject());
		registerDelegate(float.class,    new DFloat());
		registerDelegate(Integer.class,  new DIntegerObject());
		registerDelegate(int.class,      new DInteger());
		registerDelegate(Long.class,     new DLongObject());
		registerDelegate(long.class,     new DLong());
		registerDelegate(Short.class,    new DShortObject());
		registerDelegate(short.class,    new DShort());
		// arrays      
		registerDelegate(boolean[].class,new DBooleanArray());
		registerDelegate(Boolean[].class,new DBooleanObjectArray());
		registerDelegate(byte[].class,new DByteArray());
		registerDelegate(Byte[].class,new DByteObjectArray());
		registerDelegate(char[].class,new DCharArray());
		registerDelegate(Character[].class,new DCharObjectArray());
		registerDelegate(double[].class,new DDoubleArray());
		registerDelegate(Double[].class,new DDoubleObjectArray());
		registerDelegate(float[].class,new DFloatArray());
		registerDelegate(Float[].class,new DFloatObjectArray());
		registerDelegate(int[].class,new DIntegerArray());
		registerDelegate(Integer[].class,new DIntegerObjectArray());
		registerDelegate(long[].class,new DLongArray());
		registerDelegate(Long[].class,new DLongObjectArray());
		registerDelegate(short[].class,new DShortArray());
		registerDelegate(Short[].class,new DShortObjectArray());
		// lang
		registerDelegate(Class.class,new DClass());
		registerDelegate(String.class,new DString());
		registerDelegate(StringBuffer.class,new DStringBuffer());
		
		// other java utils 
		registerDelegate("java.util.BitSet","nuggets.delegate.DBitSet");
		registerDelegate("java.util.Calendar","nuggets.delegate.DCalendar");
		registerDelegate("java.util.GregorianCalendar","nuggets.delegate.DCalendar");
		registerDelegate("java.util.Collection","nuggets.delegate.DCollection");
		registerDelegate("java.util.List","nuggets.delegate.DList");
		registerDelegate("java.util.Set","nuggets.delegate.DCollection");
		registerDelegate("java.util.Date","nuggets.delegate.DDate");
		registerDelegate("java.util.Locale","nuggets.delegate.DLocale");
		registerDelegate("java.util.Map","nuggets.delegate.DMap");
		registerDelegate("java.util.Properties","nuggets.delegate.DProperties");
		registerDelegate("java.util.TimeZone","nuggets.delegate.DTimeZone");
		registerDelegate("java.util.TreeMap","nuggets.delegate.DTreeMap");
		registerDelegate("java.util.TreeSet","nuggets.delegate.DTreeSet");
		
		// other
		registerDelegate("java.io.File", "nuggets.delegate.DFile");
		registerDelegate("java.net.URL","nuggets.delegate.DURL");
		registerDelegate("java.net.InetAddress","nuggets.delegate.DInetAddress");

		registerDelegate("java.sql.Date","nuggets.delegate2.DSQLDate");
		registerDelegate("java.sql.Time","nuggets.delegate2.DSQLTime");
		registerDelegate("java.sql.Timestamp","nuggets.delegate2.DSQLTimestamp");
		registerDelegate("java.math.BigDecimal", "nuggets.delegate2.DBigDecimal");
		registerDelegate("java.math.BigInteger", "nuggets.delegate2.DBigInteger");

	}

	/** 
	 * @param clazz
	 * @param delegate
	 */
	public static void registerDelegate(Class clazz, IDelegate delegate)
	{
		try
		{
			String cn=clazz.getName();
			fast_map.put(clazz, delegate);
			string_map.put(cn, delegate);
			if(cn.startsWith("java.lang.") && cn.lastIndexOf('.') == 9)
			{
				// register java.lang classes without prefix
				string_map.put(cn.substring(10), delegate);
			}
		}
		catch(Exception e)
		{ /* the class has not been found */
			e.printStackTrace();
		}
	}

	/** 
	 * @param class_name
	 * @param delegate_name
	 */
	private static void registerDelegate(String class_name, String delegate_name)
	{
		registerDelegate(class_name, delegate_name, PersistenceHelper.class.getClassLoader());
	}
	
	/** 
	 * @param class_name
	 * @param delegate_name
	 */
	private static void registerDelegate(String class_name, String delegate_name, ClassLoader classloader)
	{
		try
		{
			registerDelegate(Class.forName(class_name, true, classloader), (IDelegate)Class.forName(delegate_name, true, classloader).newInstance());
		}
		catch(Exception e)
		{
			/* class or delegate not found */
			// e.printStackTrace();
		}
	}
}