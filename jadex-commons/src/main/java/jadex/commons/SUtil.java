package jadex.commons;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HttpsURLConnection;

import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.random.FastThreadedRandom;


/**
 * This class provides several useful static util methods.
 */
public class SUtil
{
	/** Directory were jadex stores files generated during runtime, to be used for later runs. */
	public static final String	JADEXDIR	= "./.jadex/";
	
	/** Line separator. */
	public static final String LF = System.getProperty("line.separator");
	
	/** Units for representing byte values. */
	public static final String[]	BYTE_UNITS	= new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};	// captures up to Long.MAX_VALUE (= 7.99 EB)
	
	/** The byte formatter for one predecimal digit. */
	public static final DecimalFormat	BYTEFORMATTER1	= new DecimalFormat("0.00");

	/** The byte formatter for two predecimal digits. */
	public static final DecimalFormat	BYTEFORMATTER2	= new DecimalFormat("00.0");

	/** The byte formatter for three predecimal digits. */
	public static final DecimalFormat	BYTEFORMATTER3	= new DecimalFormat("000");

	/** Constant that indicates a conversion of all known characters. */
	public static final int			CONVERT_ALL				= 1;

	/** Constant that indicates a conversion of all known characters except &. */
	public static final int			CONVERT_ALL_EXCEPT_AMP	= 2;

	/** Constant that indicates a conversion of no characters. */
	public static final int			CONVERT_NONE			= 3;

	/** A Null value. */
	public static final String		NULL					= "NULL";
	
	/** ASCII charset. */
	public static final Charset ASCII = Charset.forName("US-ASCII");
	
	/** UTF-8 charset. */
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	/** ISO-8859-1 charset. */
	public static final Charset ISO8859_1 = Charset.forName("ISO-8859-1");
	
	/** Access to non-secure fast random source. */
	public static final Random FAST_RANDOM = new FastThreadedRandom();
	
	/** Access to secure random source. */
	public static final SecureRandom SECURE_RANDOM;
	static
	{
		SecureRandom secrand = null;
		try
		{
			Class<?> ssecurity = Class.forName("jadex.commons.security.SSecurity");
			Method getSecureRandom = ssecurity.getDeclaredMethod("getSecureRandom", new Class[0]);
			secrand = (SecureRandom) getSecureRandom.invoke(null, (Object[]) null);
		}
		catch (Exception e)
		{
			secrand = new SecureRandom();
		}
		SECURE_RANDOM = secrand;
		
	}
	
	/** The mime types. */
	protected volatile static Map<String, String> MIMETYPES;

	// Thread local gives best multithread performance for date format access:
	// http://www.javacodegeeks.com/2010/07/java-best-practices-dateformat-in.html
	
	/** Simple date format. */
	public static final ThreadLocal<DateFormat>	SDF	= new ThreadLocal<DateFormat>()
	{
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("dd.MM.yyyy HH:mm");
		}
	};
	public static final ThreadLocal<DateFormat>	SDF2	= new ThreadLocal<DateFormat>()
	{
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("dd.MM.yyyy");
		}
	};
	public static final ThreadLocal<DateFormat>	SDF3	= new ThreadLocal<DateFormat>()
	{
		protected DateFormat initialValue()
		{
			return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		}
	};
	
	/** ISO8601 fallbacks assuming UTC. */
	public static final String[] ISO8601UTCFALLBACKS;
	static
	{
		ISO8601UTCFALLBACKS = new String[]
		{
			"yyyy-MM-dd'T'HH:mm:ss'Z'",
			"yyyy-MM-dd' 'HH:mm:ss'+00:00'",
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
			"yyyy-MM-dd'T'HH:mm:ss.SSS",
			"yyyy-MM-dd'T'HH:mm:ss"
		};
	}
	
	/** ISO8601 fallbacks with included timezone. */
	public static final String[] ISO8601ZONEDFALLBACKS;
	static
	{
		ISO8601ZONEDFALLBACKS = new String[]
		{
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSX",
			"yyyy-MM-dd'T'HH:mm:ssZ",
			"yyyy-MM-dd'T'HH:mm:ssX"
		};
	}
	
	/** Application directory, current working dir under normal Java, special with Android. */
	protected static volatile File appdir = null;
	
	/**
	 * Mapping from single characters to encoded version for displaying on
	 * xml-style interfaces.
	 */
	protected static final Map<String, String>			htmlwraps;
	
	/** Cached AndroidUtils */
	protected static volatile AndroidUtils androidutils;

	/** Holds the single characters. */
	protected static final String			seps;
	

	/** An empty enumeration. */
	public static final Enumeration	EMPTY_ENUMERATION	= new Enumeration()
	{
		public boolean hasMoreElements()
		{
			return false;
		}

		public Object nextElement()
		{
			return null;
		}
	};
	
	/** An empty string array. */
	public static final String[] EMPTY_STRING_ARRAY	= new String[0];

	/** An empty class array. */
	public static final Class[]	EMPTY_CLASS_ARRAY = new Class[0];
	
	/** An empty class array. */
	public static final Object[] EMPTY_OBJECT_ARRAY	= new Class[0];

	
	protected static final IResultCommand<ResourceInfo, URLConnection>[]	RESOURCEINFO_MAPPERS;

	static
	{
		htmlwraps = new Hashtable<String, String>();
		htmlwraps.put("\\u0022", "&quot;");
		htmlwraps.put("\u0026", "&amp;"); // Hmm???
		htmlwraps.put("\u0027", "&apos;");
		htmlwraps.put("\u003C", "&lt;");
		htmlwraps.put("\u003E", "&gt;");
		htmlwraps.put("\u00E4", "&auml;");
		htmlwraps.put("\u00C4", "&Auml;");
		htmlwraps.put("\u00FC", "&uuml;");
		htmlwraps.put("\u00DC", "&Uuml;");
		htmlwraps.put("\u00F6", "&ouml;");
		htmlwraps.put("\u00D6", "&Ouml;");

		htmlwraps.put("\u00B4", "&acute;");
		htmlwraps.put("\u00E1", "&aacute;");
		htmlwraps.put("\u00C1", "&Aacute;");
		htmlwraps.put("\u00E0", "&agrave;");
		htmlwraps.put("\u00C0", "&Agrave;");
		htmlwraps.put("\u00E5", "&aring;");
		htmlwraps.put("\u00C5", "&Aring;l");
		htmlwraps.put("\u00E2", "&acirc;");
		htmlwraps.put("\u00C2", "&Acirc;");

		htmlwraps.put("\u00E9", "&eacute;");
		htmlwraps.put("\u00C9", "&Eacute;");
		htmlwraps.put("\u00E8", "&egrave;");
		htmlwraps.put("\u00C8", "&Egrave;");
		htmlwraps.put("\u00EA", "&ecirc;");
		htmlwraps.put("\u00CA", "&Ecirc;");

		htmlwraps.put("\u00ED", "&iacute;");
		htmlwraps.put("\u00CD", "&Iacute;");
		htmlwraps.put("\u00EC", "&igrave;");
		htmlwraps.put("\u00CC", "&Igrave;");
		htmlwraps.put("\u00EE", "&icirc;");
		htmlwraps.put("\u00CE", "&Icirc;");

		htmlwraps.put("\u00F3", "&oacute;");
		htmlwraps.put("\u00D3", "&Oacute;");
		htmlwraps.put("\u00F2", "&ograve;");
		htmlwraps.put("\u00D2", "&Ograve;");
		htmlwraps.put("\u00F4", "&ocirc;");
		htmlwraps.put("\u00D4", "&Ocirc;");
		htmlwraps.put("\u00F5", "&otilde;");
		htmlwraps.put("\u00D5", "&Otilde;");

		htmlwraps.put("\u00FA", "&uacute;");
		htmlwraps.put("\u00DA", "&Uacute;");
		htmlwraps.put("\u00F9", "&ugrave;");
		htmlwraps.put("\u00D9", "&Ugrave;");
		htmlwraps.put("\u00FB", "&ucirc;");
		htmlwraps.put("\u00DB", "&Ucirc;");

		htmlwraps.put("\u00E7", "&ccedil;");
		htmlwraps.put("\u00C7", "&Ccedil;");
		htmlwraps.put("+", "%2b");

		String	tmp = "";
		Iterator<String> it = htmlwraps.keySet().iterator();
		while(it.hasNext())
			tmp += it.next();
		seps	= tmp;
		
		List<IResultCommand<ResourceInfo, URLConnection>>	mappers	= new ArrayList<IResultCommand<ResourceInfo, URLConnection>>();
		String	custommappers	= System.getProperty("jadex.resourcemappers");
		if(custommappers!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(custommappers, ",");
			while(stok.hasMoreTokens())
			{
				String	mapper	= stok.nextToken().trim();
				try
				{
					Class<IResultCommand<ResourceInfo, URLConnection>> clazz = (Class<IResultCommand<ResourceInfo, URLConnection>>)SReflect.classForName(mapper, SUtil.class.getClassLoader());
					mappers.add((IResultCommand<ResourceInfo, URLConnection>)clazz.newInstance());
				}
				catch(Exception e)
				{
					System.err.println("Error loading custom resource mapper: "+mapper);
					throw new RuntimeException(e);
				}
			}
		}
		
		// ResourceInfo mapper for Jar URL connection
		mappers.add(new IResultCommand<ResourceInfo, URLConnection>()
		{
			public ResourceInfo execute(URLConnection con)
			{
				ResourceInfo	ret	= null;
				if(con instanceof JarURLConnection)
				{
					try
					{
						long modified = 0;
						String	filename	= con.getURL().getFile();
						JarURLConnection juc = (JarURLConnection)con;
						// System.out.println("Jar file:     "+juc.getJarFile());
						// System.out.println("Jar file url: "+juc.getJarFileURL());
						// System.out.println("Jar entry:    "+juc.getJarEntry());
						// System.out.println("Entry name:   "+juc.getEntryName());
	
						// Add jar protocol to file (hack???).
						if(!filename.startsWith("jar:"))
							filename = "jar:" + filename;
	
						// Set modified date to time of entry (if
						// specified).
						if(juc.getJarEntry().getTime() != -1)
							modified = juc.getJarEntry().getTime();
	
						try
						{
							ret = new ResourceInfo(filename, con.getInputStream(), modified);
						}
						catch(NullPointerException e)
						{
							// Workaround for Java bug #5093378 !?
							// Maybe this is only a race condition???
							String jarfilename = juc.getJarFile().getName();
							ret = new ResourceInfo(filename, new JarFile(jarfilename)
								.getInputStream(juc.getJarEntry()), modified);
							// System.err.println("loaded with workaround: "+url);
						}
	
						// todo: what about jar directories?!
					}
					catch(IOException e)
					{
					}
				}
				return ret;
			}
		});
		// Eclipse OSGI resource bundle.
		mappers.add(new IResultCommand<ResourceInfo, URLConnection>()
		{
			public ResourceInfo execute(URLConnection con)
			{
				ResourceInfo ret = null;
				long modified = con.getLastModified();
				if(con.getClass().getName().equals("org.eclipse.osgi.framework.internal.core.BundleURLConnection"))
				{
					try
					{
						Method	m	= con.getClass().getMethod("getLocalURL", new Class<?>[0]);
						ret = new ResourceInfo(m.invoke(con, new Object[0]).toString(), con.getInputStream(), modified);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				return ret;
			}
		});
		// Fallback resource info.
		mappers.add(new IResultCommand<ResourceInfo, URLConnection>()
		{
			public ResourceInfo execute(URLConnection con)
			{
				ResourceInfo	ret	= null;
				try
				{
					long modified = con.getLastModified();
					String	filename	= URLDecoder.decode(con.getURL().getFile(), "UTF-8");
					ret	= new ResourceInfo(filename, con.getInputStream(), modified);
				}
				catch(IOException e)
				{
				}
				return ret;
			}
		});
		RESOURCEINFO_MAPPERS	= mappers.toArray(new IResultCommand[mappers.size()]);
	}

	/**
	 * Get a string array of properties that are separated by commas.
	 * 
	 * @param key The key.
	 * @param props The properties.
	 * @return The strings.
	 */
	public static String[] getStringArray(String key, Properties props)
	{
		String[] ret = new String[0];
		String os = props.getProperty(key);
		if(os != null)
		{
			StringTokenizer stok = new StringTokenizer(os, ",");
			ret = new String[stok.countTokens()];
			for(int i = 0; stok.hasMoreTokens(); i++)
			{
				ret[i] = stok.nextToken().trim();
			}
		}
		return ret;
	}

	/**
	 * Joins two arrays of the same type. Creates a new array containing the
	 * values of the first array followed by the values of the second.
	 * 
	 * @param a1 The first array.
	 * @param a2 The second array.
	 * @return The joined array.
	 */
	public static Object joinArrays(Object a1, Object a2)
	{
		if(a1==null)
		{
			return a2;
		}
		else if(a2==null)
		{
			return a1;
		}
		else
		{
			int l1 = Array.getLength(a1);
			int l2 = Array.getLength(a2);
			Object res = Array.newInstance(a1.getClass().getComponentType(), l1+ l2);
			System.arraycopy(a1, 0, res, 0, l1);
			System.arraycopy(a2, 0, res, l1, l2);
			return res;
		}
	}
	
	/**
	 * Joins any arrays of (possibly) different type. todo: Does not support
	 * basic types yet. Problem basic type array and object arrays cannot be
	 * mapped (except they are mapped).
	 * 
	 * @param as The array of arrays to join..
	 * @return The joined array.
	 */
	public static <T> T[] joinArbitraryArrays(Object[] as)
	{
		int lsum = 0;
		for(int i = 0; i < as.length; i++)
			lsum += Array.getLength(as[i]);
		T[] ret = (T[])new Object[lsum];
		// Object ret = Array.newInstance(Object.class, lsum);

		int start = 0;
		for(int i = 0; i < as.length; i++)
		{
			int length = Array.getLength(as[i]);
			System.arraycopy(as[i], 0, ret, start, length);
			start += length;
		}

		return ret;
	}

	/**
	 * Cut two arrays.
	 * 
	 * @param a1 The first array.
	 * @param a2 The second array.
	 * @return The cutted array.
	 */
	public static Object cutArrays(Object a1, Object a2)
	{
		List<Object> ar1 = arrayToList(a1);
		List<Object> ar2 = arrayToList(a2);
		List<Object> ret = new ArrayList<Object>();
		Object tmp;

		for(int i = 0; i < ar1.size(); i++)
		{
			tmp = ar1.get(i);
			if(ar2.contains(tmp))
			{
				ret.add(tmp);
			}
		}
		return ret.toArray((Object[])Array.newInstance(a1.getClass()
			.getComponentType(), ret.size()));
	}

	/**
	 * First array minus second array.
	 * 
	 * @param a1 The first array.
	 * @param a2 The second array.
	 * @return The substracted array.
	 */
	public static Object substractArrays(Object a1, Object a2)
	{
		List<Object> ar1 = arrayToList(a1);
		List<Object> ar2 = arrayToList(a2);
		Object tmp;

		for(int i = 0; i < ar2.size(); i++)
		{
			tmp = ar2.get(i);
			if(ar1.contains(tmp))
			{
				ar1.remove(tmp);
			}
		}
		return ar1.toArray((Object[])Array.newInstance(a1.getClass()
			.getComponentType(), ar1.size()));
	}

	/**
	 * Transform an array to a vector.
	 * 
	 * @param a The array.
	 * @return The vector for the array.
	 */
	public static <T> List<T> arrayToList(Object a)
	{
		ArrayList<T> ret = null;
		if(a!=null)
		{
			int l = Array.getLength(a);
			ret = SCollection.createArrayList();
			for(int i = 0; i < l; i++)
			{
				ret.add((T)Array.get(a, i));
			}
		}
		return ret;
	}

	/**
	 * Transform an array to a vector.
	 * 
	 * @param a The array.
	 * @return The vector for the array.
	 */
	public static <T> Set<T> arrayToSet(Object a)
	{
		int l = Array.getLength(a);
		Set<T> ret = SCollection.createHashSet();
		for(int i = 0; i < l; i++)
		{
			ret.add((T)Array.get(a, i));
		}
		return ret;
	}

	/**
	 * Join two sets.
	 * 
	 * @param a The first set.
	 * @param b The second set.
	 * @return A set with elements from a and b. / public static Set
	 *         joinSets(Set a, Set b) { Set ret = new HashSet(); ret.addAll(a);
	 *         ret.addAll(b); return ret; }
	 */

	/**
	 * Transform an iterator to a list.
	 */
	public static <T> List<T> iteratorToList(Iterator<T> it)
	{
		List<T> ret = new ArrayList<T>();
		while(it.hasNext())
			ret.add(it.next());
		return ret;
	}

	/**
	 * Transform an iterator to a list.
	 */
	public static <T> List<T> iteratorToList(Iterator<T> it, List<T> ret)
	{
		if(ret == null)
			ret = new ArrayList<T>();
		while(it.hasNext())
			ret.add(it.next());
		return ret;
	}

	/**
	 * Transform an iterator to an array.
	 */
	public static <T> Object[] iteratorToArray(Iterator<T> it, Class<T> clazz)
	{
		List<T> list = iteratorToList(it);
		return list.toArray((Object[])Array.newInstance(clazz, list.size()));
	}

	/**
	 * Check if an element is contained in an array.
	 * 
	 * @param array The array.
	 * @param value The value.
	 */
	public static boolean arrayContains(Object array, Object value)
	{
		int l = Array.getLength(array);
		boolean ret = false;
		for(int i = 0; !ret && i < l; i++)
		{
			ret = equals(Array.get(array, i), value);
		}
		return ret;
	}

	/**
	 *  Get the array dimension.
	 *  @param array The array.
	 *  @return The number of dimensions.
	 */
	public static int getArrayDimension(Object array) 
	{
		int ret = 0;
		Class<?> arrayClass = array.getClass();
		while(arrayClass.isArray()) 
		{
			ret++;
			arrayClass = arrayClass.getComponentType();
		}
		return ret;
	}
	
//	/**
//	 * Get the dimension of an array.
//	 * 
//	 * @param array
//	 * @return The array dimension.
//	 */
//	public static int[] getArrayLengths(Object array)
//	{
//		List lens = new ArrayList();
//		Class cls = array.getClass();
//
//		while(cls.isArray())
//		{
//			lens.add(Integer.valueOf(Array.getLength(array)));
//			cls = cls.getComponentType();
//		}
//
//		int[] ret = new int[lens.size()];
//		for(int i = 0; i < lens.size(); i++)
//			ret[i] = ((Integer)lens.get(i)).intValue();
//
//		return ret;
//	}

	/*
	 * Test if two values are equal or both null.
	 * @param val1 The first value.
	 * @param val2 The second value.
	 * @return True when the values are equal.
	 */
	public static boolean equals(Object val1, Object val2)
	{
		// Should try comparable first, for consistency???
		return val1 == val2 || val1 != null && val1.equals(val2);
	}

	/**
	 * Test if two arrays are content equal or both null.
	 * 
	 * @param array1 The first array.
	 * @param array2 The second array.
	 * @return True when the arrays are content equal.
	 */
	public static boolean arrayEquals(Object array1, Object array2)
	{
		boolean ret = array1 == null && array2 == null;
		if(!ret && array1 != null && array2 != null)
		{
			int l1 = Array.getLength(array1);
			int l2 = Array.getLength(array2);
			if(l1 == l2)
			{
				ret = true;
				for(int i = 0; i < l1 && ret; i++)
				{
					if(!Array.get(array1, i).equals(Array.get(array2, i)))
						ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 * Calculate a hash code for an array.
	 */
	public static int arrayHashCode(Object a)
	{
		int ret = 1;

		for(int i = 0; i < Array.getLength(a); i++)
		{
			Object val = Array.get(a, i);
			ret = 31 * ret + (val != null ? val.hashCode() : 0);
		}

		return ret;
	}

	/**
	 * Get a string representation for an array.
	 * 
	 * @param array The array.
	 * @return formatted string.
	 */
	public static String arrayToString(Object array)
	{
		StringBuffer str = new StringBuffer();

		if(array != null && array.getClass().getComponentType() != null)
		{
			// inside arrays.
			str.append("[");
			for(int i = 0; i < Array.getLength(array); i++)
			{
				str.append(arrayToString(Array.get(array, i)));
				if(i < Array.getLength(array) - 1)
				{
					str.append(", ");
				}
			}
			str.append("]");
		}
		else
		{
			// simple type
			str.append(array);
		}
		return str.toString();
	}

	/**
	 * Get the singular of a word in plural. Does NOT find all correct singular.
	 * 
	 * @param s The plural word.
	 * @return The singular of this word.
	 */
	public static String getSingular(String s)
	{
		String sing = s;
		if(s.endsWith("shes") || s.endsWith("ches") || s.endsWith("xes")
			|| s.endsWith("ses"))
		{
			sing = s.substring(0, s.length() - 2);
		}
		else if(s.endsWith("ies"))
		{
			sing = s.substring(0, s.length() - 3) + "y";
		}
		else if(s.endsWith("s"))
		{
			sing = s.substring(0, s.length() - 1);
		}

		return sing;
	}

	/**
	 * Get the plural of a word in singular. Does NOT find all correct plurals.
	 * 
	 * @param s The word.
	 * @return The plural of this word.
	 */
	public static String getPlural(String s)
	{
		String plu = s;
		if(s.endsWith("y"))
		{
			plu = s.substring(0, s.length() - 1) + "ies";
		}
		else if(s.endsWith("s"))
		{
			plu = s + "es";
		}
		else
		{
			plu = s + "s";
		}
		return plu;
	}

	/**
	 * Compares two strings, ignoring case.
	 * 
	 * @param a The first string.
	 * @param b The second string.
	 * @return a<b => <0
	 */
	public static int compareTo(String a, String b)
	{
		return a.toUpperCase().toLowerCase()
				.compareTo(b.toUpperCase().toLowerCase());
	}

	/**
	 * Test if the date is in the range. Start or end may null and will so not
	 * be checked.
	 * 
	 * @param date The date.
	 * @param start The start.
	 * @param end The end.
	 * @return True, if date is in range.
	 */
	public static boolean isInRange(Date date, Date start, Date end)
	{
		boolean ret = true;
		if(start != null && date.before(start))
		{
			ret = false;
		}
		if(ret && end != null && date.after(end))
		{
			ret = false;
		}
		return ret;
	}

	/**
	 * Remove file extension.
	 * 
	 * @param fn The filename..
	 * @return filename without extension.
	 */
	public static String removeExtension(String fn)
	{
		int index = fn.lastIndexOf(".");
		if(index > -1)
		{
			fn = fn.substring(0, index);
		}
		return fn;
	}

	/**
	 * Wrap a text at a given line length. Doesn't to word wrap, just inserts
	 * linebreaks every nth character. If the string already contains
	 * linebreaks, these are handled properly (extra linebreaks will only be
	 * inserted when needed).
	 * 
	 * @param text The text to wrap.
	 */
	public static String wrapText(String text)
	{
		return wrapText(text, 80);
	}

	/**
	 * Wrap a text at a given line length. Doesn't to word wrap, just inserts
	 * linebreaks every nth character. If the string already contains
	 * linebreaks, these are handled properly (extra linebreaks will only be
	 * inserted when needed).
	 * 
	 * @param text The text to wrap.
	 * @param wrap The column width.
	 */
	public static String wrapText(String text, int wrap)
	{
		StringBuffer buf = new StringBuffer(text);
		int i = 0;

		// Insert line breaks, while more than <wrap> characters to go.
		while(buf.length() > i + wrap)
		{
			// Find next line break.
			// int next = buf.indexOf("\n", i); // works for 1.4 only.
			int next = buf.substring(i).indexOf("\n");

			// Skip line break, when in sight.
			if(next != -1 && next - i <= wrap)
			{
				i = i + next + 1;
			}

			// Otherwise, insert line break.
			else
			{
				buf.insert(i + wrap, '\n');
				i = i + wrap + 1;
			}
		}

		return buf.toString();
	}

//	/** Constant for sorting up. */
//	public static final int	SORT_UP		= 0;
//
//	/** Constant for sorting down. */
//	public static final int	SORT_DOWN	= 1;
//
//	/**
//	 * Remove the least element form a collection.
//	 */
//	protected static int getExtremeElementIndex(Vector source, int direction)
//	{
//		String ret = (String)source.elementAt(0);
//		int retidx = 0;
//		int size = source.size();
//		for(int i = 0; i < size; i++)
//		{
//			String tmp = (String)source.elementAt(i);
//			int res = tmp.compareTo(ret);
//			if((res < 0 && direction == SORT_UP)
//					|| (res > 0 && direction == SORT_DOWN))
//			{
//				ret = tmp;
//				retidx = i;
//			}
//		}
//		return retidx;
//	}

	/**
	 * Convert an output to html/wml conform presentation.
	 * 
	 * @param input The input string.
	 * @return The converted output string.
	 */
	public static String makeConform(String input)
	{
		return makeConform(input, CONVERT_ALL);
	}

	/**
	 * Convert an output to html/wml conform presentation.
	 * 
	 * @param input The input string.
	 * @param flag CONVERT_ALL, CONVERT_NONE, CONVERT_ALL_EXCEPT_AMP;
	 * @return The converted output string.
	 */
	public static String makeConform(String input, int flag)
	{
		String res = input;
		if(flag != CONVERT_NONE)
		{
			StringTokenizer stok = new StringTokenizer(input, seps, true);
			res = "";
			while(stok.hasMoreTokens())
			{
				String tmp = stok.nextToken();
				String rep = null;
				if(!(tmp.equals("&") && flag == CONVERT_ALL_EXCEPT_AMP))
					rep = htmlwraps.get(tmp);
				if(rep != null)
					res += rep;
				else
					res += tmp;
			}
		}
		return res;
	}

	/**
	 * Strip tags (e.g. html) from a string, leaving only the text content.
	 */
	public static String stripTags(String source)
	{
		int start, end;
		while((start = source.indexOf("<")) != -1
				&& (end = source.indexOf(">")) > start)
		{
			if(end == source.length() - 1)
			{
				source = source.substring(0, start);
			}
			else
			{
				source = source.substring(0, start) + source.substring(end + 1);
			}
		}
		return source;
	}

	/**
	 * Convert an output readable in english. Therefore remove all &auml;s,
	 * &ouml;s, &uuml;s etc.
	 * 
	 * @param input The input string.
	 * @return The converted output string.
	 */
	public static String makeEnglishConform(String input)
	{
		StringTokenizer stok = new StringTokenizer(input, seps, true);
		String res = "";
		while(stok.hasMoreTokens())
		{
			String tmp = stok.nextToken();
			if(htmlwraps.get(tmp) == null)
				res += tmp;
		}
		return res;
	}
	
	/**
	 *  Convert CamelCase to snake_case.
	 */
	public static String	camelToSnakeCase(String camel)
	{
        String snake	= camel.replaceAll("([^A-Z])([A-Z]+)", "$1_$2").toLowerCase();
        return snake;
	}

	/**
	 *  Convert snake_case to CamelCase.
	 */
	public static String	snakeToCamelCase(String snake)
	{
		// Match any number of underscores followed by a single non-underscore (group 1).
	    Matcher	msnake	= Pattern.compile("_+([^_])").matcher(snake);
	    StringBuilder camel	= new StringBuilder();
	    int	end	= 0;
	    while(msnake.find())
	    {
	    	// Add skipped characters
	    	if(msnake.start()>end)
	    	{
	    		camel.append(snake.substring(end, msnake.start()));
	    	}
	    	
	    	// Convert character to uppercase.
	    	camel.append(msnake.group(1).toUpperCase());
	    	
	    	// Remember end index of last match. 
	    	end	= msnake.end();
	    }
	    
		// Add non-matched rest of string
	    camel.append(snake.substring(end));
	    
        return camel.toString();
	}

	/**
	 * Extract the values out of an sl message.
	 * 
	 * @param message The sl message.
	 * @return The extracted properties. / // obsolete ??? public static
	 *         Properties parseSLToPropertiesFast(String message) { Properties
	 *         props = new Properties(); int index = message.indexOf(':');
	 *         while(index!=-1) { // Hack !!! Assume space separated slots. int
	 *         index2 = message.indexOf(' ', index); String name =
	 *         message.substring(index+1, index2); index = message.indexOf('"',
	 *         index2); index2 = message.indexOf('"', index+1); String value =
	 *         message.substring(index+1, index2); props.setProperty(name,
	 *         value); index = message.indexOf(':', index2); } return props; }
	 */
	/**
	 * Extract the value(s) out of an sl message.
	 * 
	 * @param message The sl message.
	 * @return The extracted value(s) as string, index map or array list.
	 * @see #toSLString(Object) / public static Object fromSLString(String
	 *      message) { Object ret; // Parse map. if(message.startsWith("(Map ")
	 *      && message.endsWith(")")) { message = message.substring(5,
	 *      message.length()-1); ExpressionTokenizer exto = new
	 *      ExpressionTokenizer(message, " \t\r\n", new String[]{"\"\"", "()"});
	 *      Map map = new IndexMap().getAsMap(); // Hack???
	 *      while(exto.hasMoreTokens()) { // Check for ":" as start of slot
	 *      name. String slot = exto.nextToken(); if(!slot.startsWith(":") ||
	 *      !exto.hasMoreTokens()) throw new
	 *      RuntimeException("Invalid SL: "+message); slot = slot.substring(1);
	 *      //if(slot.equals("2")) // System.out.println("Da1!"); map.put(slot,
	 *      fromSLString(exto.nextToken())); } ret = map; } // Parse sequence to
	 *      collection object. else if(message.startsWith("(sequence ") &&
	 *      message.endsWith(")")) { message = message.substring(10,
	 *      message.length()-1); ExpressionTokenizer exto2 = new
	 *      ExpressionTokenizer(message, " \t\r\n", new String[]{"\"\"", "()"});
	 *      List list = new ArrayList(); while(exto2.hasMoreTokens()) {
	 *      list.add(fromSLString(exto2.nextToken())); } ret = list; } // Simple
	 *      slot message. else { // Remove quotes from message.
	 *      if(message.startsWith("\"") && message.endsWith("\"")) message =
	 *      message.substring(1, message.length()-1); // Replace escaped quotes.
	 *      message = SUtil.replace(message, "\\\"", "\""); ret = message; }
	 *      return ret; }
	 */

	/**
	 * Convert an object to an SL string. When the value is of type
	 * java.util.Map the key value pairs are extracted as slots. Keys must be
	 * valid slot names. Values of type java.util.Collection are stored as
	 * sequence.
	 * 
	 * @return A string representation in SL. / public static String
	 *         toSLString(Object o) { StringBuffer sbuf = new StringBuffer();
	 *         toSLString(o, sbuf); return sbuf.toString(); }
	 */

	/**
	 * Convert an object to an SL string. When the value is of type
	 * java.util.Map the key value pairs are extracted as slots. Keys must be
	 * valid slot names. Values of type java.util.Collection are stored as
	 * sequence.
	 * 
	 * @param o The object to convert to SL.
	 * @param sbuf The buffer to convert into. / public static void
	 *        toSLString(Object o, StringBuffer sbuf) { // Get mapo from
	 *        encodable object. /*if(o instanceof IEncodable) { o =
	 *        ((IEncodable)o).getEncodableRepresentation(); }* / // Write
	 *        contents as slot value pairs. if(o instanceof Map) { Map contents
	 *        = (Map)o; sbuf.append("(Map "); for(Iterator
	 *        i=contents.keySet().iterator(); i.hasNext();) { Object key =
	 *        i.next(); Object val = contents.get(key); if(val!=null &&
	 *        !"null".equals(val)) // Hack ??? { // Check if key is valid slot
	 *        identifier. String keyval = key.toString();
	 *        if(keyval.indexOf(' ')!=-1 || keyval.indexOf('\t')!=-1 ||
	 *        keyval.indexOf('\r')!=-1 || keyval.indexOf('\n')!=-1) { throw new
	 *        RuntimeException("Encoding error: Invalid slot name "+keyval); }
	 *        sbuf.append(" :"); sbuf.append(keyval); sbuf.append(" ");
	 *        toSLString(val, sbuf); } } sbuf.append(")"); } // Write collection
	 *        value as sequence. else if(o instanceof Collection) { Collection
	 *        coll = (Collection)o; sbuf.append(" (sequence "); for(Iterator
	 *        j=coll.iterator(); j.hasNext(); ) { sbuf.append(" ");
	 *        toSLString(j.next(), sbuf); } sbuf.append(")"); } // Write normal
	 *        slot value as string. else { sbuf.append("\""); // Escape quotes
	 *        (directly writes to string buffer). SUtil.replace(""+o, sbuf,
	 *        "\"", "\\\""); sbuf.append("\""); } }
	 */

	/**
	 * Parse a source string replacing occurrences and storing the result in the
	 * given string buffer. This is a fast alternative to String.replaceAll(),
	 * because it does not use regular expressions.
	 * 
	 * @param source The source string.
	 * @param dest The destination string buffer.
	 * @param old The string to replace.
	 * @param newstring The string to use as replacement.
	 */
	public static void replace(String source, StringBuffer dest, String old, String newstring)
	{
		int last = 0;
		int index;
		while((index = source.indexOf(old, last)) != -1)
		{
			dest.append(source.substring(last, index));
			dest.append(newstring);
			last = index + old.length();
		}
		dest.append(source.substring(last));
	}

	/**
	 * Parse a source string replacing occurrences and returning the result.
	 * This is a fast alternative to String.replaceAll(), because it does not
	 * use regular expressions.
	 * 
	 * @param source The source string.
	 * @param old The string to replace.
	 * @param newstring The string to use as replacement.
	 */
	public static String replace(String source, String old, String newstring)
	{
		StringBuffer sbuf = new StringBuffer();
		replace(source, sbuf, old, newstring);
		return sbuf.toString();
	}

	/**
	 * Get an input stream for whatever provided. 1. It is tried to load the
	 * resource as file. 2. It is tried to load the resource via the
	 * ClassLoader. 3. It is tried to load the resource as URL.
	 * 
	 * @param name The resource description.
	 * @return The input stream for the resource.
	 * @throws IOException when the resource was not found.
	 */
	public static InputStream getResource(String name, ClassLoader classloader) throws IOException
	{
		InputStream is = getResource0(name, classloader);
		if(is == null)
			throw new IOException("Could not load file: " + name);

		return is;
	}

	/**
	 * Get an input stream for whatever provided. 1. It is tried to load the
	 * resource as file. 2. It is tried to load the resource via the
	 * ClassLoader. 3. It is tried to load the resource as URL.
	 * 
	 * @param name The resource description.
	 * @return The input stream for the resource or null when the resource was
	 *         not found.
	 */
	public static InputStream getResource0(String name, ClassLoader classloader)
	{
		InputStream is = null;
		File file;

		if(classloader == null)
			classloader = SUtil.class.getClassLoader();

		// File...
		// Hack!!! Might throw exception in applet / webstart.
		try
		{
			file = new File(name);
			if(file.exists())
			{
				try
				{
					is = new FileInputStream(file);
				}
				catch(FileNotFoundException e)
				{
					// File is directory, or maybe locked...
				}
			}
		}
		catch(SecurityException e)
		{
		}

		// Classpath...
		if(is == null)
		{
			// is = getClassLoader().getResourceAsStream(name.startsWith("/") ?
			// name.substring(1) : name);
			is = classloader.getResourceAsStream(name.startsWith("/") ? name
					.substring(1) : name);
		}

		// URL...
		if(is == null)
		{
			try
			{
				is = new URL(name).openStream();
			}
			catch(IOException le)
			{
			}
		}

		return is;
	}

	/**
	 * Get an input stream for whatever provided. 1. It is tried to load the
	 * resource as file. 2. It is tried to load the resource via the
	 * ClassLoader. 3. It is tried to load the resource as URL.
	 * 
	 * @param name The resource description.
	 * @return The info object for the resource or null when the resource was
	 *         not found.
	 */
	public static ResourceInfo getResourceInfo0(String name, ClassLoader classloader)
	{
		ResourceInfo ret = null;
		File file;

		if(classloader == null)
			classloader = SUtil.class.getClassLoader();

		// File...
		// Hack!!! Might throw exception in applet / webstart.
		try
		{
			file = new File(name);
			if(file.exists())
			{
				if(file.isDirectory())
				{
					try
					{
						ret = new ResourceInfo(file.getCanonicalPath(), null, file.lastModified());
					}
					catch(IOException e)
					{
						// shouldn't happen
						e.printStackTrace();
					}
				}
				else
				{
					try
					{
						ret = new ResourceInfo(file.getCanonicalPath(),
							new FileInputStream(file), file.lastModified());
					}
					catch(FileNotFoundException e)
					{
						// File is directory, or maybe locked...
					}
					catch(IOException e)
					{
						// shouldn't happen
						e.printStackTrace();
					}
				}
			}
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
		}

		// Classpath...
		if(ret == null)
		{
			URL url = classloader.getResource(name.startsWith("/") ? name.substring(1) : name);
			// System.out.println("Classloader: "+classloader+" "+name+" "+url+" "+classloader.getParent());
			// if(classloader instanceof URLClassLoader)
			// System.out.println("URLs: "+SUtil.arrayToString(((URLClassLoader)classloader).getURLs()));

			if(url != null)
			{
				// Local file from classpath.
				// Hack!!! Needed because of last-modified precision (argl).
				if(url.getProtocol().equals("file"))
				{
					// Find out default encoding (might fail in applets).
					String encoding = "ISO-8859-1";
					try
					{
						encoding = System.getProperty("file.encoding");
					}
					catch(SecurityException e)
					{
					}

					try
					{
						file = new File(URLDecoder.decode(url.getFile(), encoding)); // does only work since 1.4.
						// file = new File(URLDecoder.decode(url.getFile())); //
						// problem decode is deprecated.
						if(file.exists())
						{
							if(file.isDirectory())
							{
								try
								{
									ret = new ResourceInfo(file.getCanonicalPath(),
										null, file.lastModified());
								}
								catch(IOException e)
								{
									// shouldn't happen
									e.printStackTrace();
								}
							}
							else
							{
								try
								{
									ret = new ResourceInfo(file.getCanonicalPath(),
										new FileInputStream(file), file.lastModified());
								}
								catch(FileNotFoundException fnfe)
								{
									// File is directory, or maybe locked...
								}
								catch(IOException e)
								{
									// shouldn't happen
									e.printStackTrace();
								}
							}
						}
					}
					catch(UnsupportedEncodingException e)
					{
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						throw new RuntimeException(sw.toString());
					}
				}

				// Remote or jar file...
				else
				{
					try
					{
						url = classloader.getResource(name.startsWith("/")
							? name.substring(1) : name);
						URLConnection con = url.openConnection();
						con.setDefaultUseCaches(false); // See Java Bug ID 4386865
						synchronized(SUtil.class)
						{
							for(int i=0; ret==null && i<RESOURCEINFO_MAPPERS.length; i++)
							{
								ret	= RESOURCEINFO_MAPPERS[i].execute(con);
							}
						}
					}
					catch(IOException e)
					{
						// System.err.println("Error loading: "+url);
						// e.printStackTrace();
					}
				}
			}
		}

		// URL...
		if(ret == null)
		{
			try
			{
				URL url = new URL(name);
				URLConnection con = url.openConnection();
				ret = new ResourceInfo(name, con.getInputStream(), con.getLastModified());
			}
			catch(IOException le)
			{
			}
		}

		return ret;
	}

	/**
	 *  Copy all data from input to output stream.
	 */
	public static void copyStream(InputStream is, OutputStream os) 
	{
		try
		{
	        byte[] buf = new byte[10 * 1024];
	        int len = 0;
	        while((len = is.read(buf)) != -1) 
	        {
	            os.write(buf, 0, len);
	        }
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Encode an object into XML.
	 * 
	 * @param ob The object.
	 * @return The xml representation. / // Hack!!! has to be synchronized due
	 *         to bug in JDK 1.4 java.beans.Statement public synchronized static
	 *         String encodeXML(Object ob) { ByteArrayOutputStream bs = new
	 *         ByteArrayOutputStream(); XMLEncoder e = new XMLEncoder(bs);
	 *         e.setExceptionListener(new ExceptionListener() { public void
	 *         exceptionThrown(Exception e) {
	 *         System.out.println("XML encoding ERROR: "); e.printStackTrace();
	 *         } }); e.writeObject(ob); e.close(); return bs.toString(); }
	 */

	/**
	 * Decode an object from XML.
	 * 
	 * @param xml_ob The xml representation.
	 * @return The object. / // Hack!!! has to be synchronized due to bug in JDK
	 *         1.4 java.beans.Statement public synchronized static Object
	 *         decodeXML(final String xml_ob) { ByteArrayInputStream bs = new
	 *         ByteArrayInputStream(xml_ob.getBytes()); XMLDecoder d = new
	 *         XMLDecoder(bs, null, new ExceptionListener() { public void
	 *         exceptionThrown(Exception e) {
	 *         System.out.println("XML decoding ERROR: "+xml_ob);
	 *         e.printStackTrace(); } }); Object ob = d.readObject(); d.close();
	 *         return ob; }
	 */

	/**
	 * Get a string representation for a duration.
	 * 
	 * @param ms The duration in ms.
	 * @return The string representation.
	 */
	public static String getDurationHMS(long ms)
	{
		long h = ms / 3600000;
		ms = ms - h * 3600000;
		long m = ms / 60000;
		ms = ms - m * 60000;
		long s = ms / 1000;
		ms = ms - s * 1000;
		StringBuffer ret = new StringBuffer("");
		if(h > 0)
		{
			ret.append(h);
			ret.append("h ");
		}
		if(m > 0)
		{
			ret.append(m);
			ret.append("min ");
		}
		// if(s>0)
		// {
		ret.append(s);
		ret.append(",");
		ret.append(ms / 100);
		ret.append("s ");
		/*
		 * } if(h==0 && m==0 && s==0) { ret.append(ms); ret.append("ms"); }
		 */
		return ret.toString();
	}

	/**
	 * Find a package name from a path. Searches the most specific classpath and
	 * uses the rest of the pathname as package name.
	 * 
	 * @param path The directory.
	 * @return The package.
	 */
	public static String convertPathToPackage(String path, URL[] urls)
	{
		String ret = null;
		File fpath = new File(path);
		if(!fpath.isDirectory())
			path = fpath.getParent();

		List<String> toks = SCollection.createArrayList();
		StringTokenizer stok = new StringTokenizer(path, File.separator);
		while(stok.hasMoreTokens())
			toks.add(stok.nextToken());

		int quality = 0;
		for(int i = 0; i<urls.length; i++)
		{
			String cp = null;
			try
			{
				cp = URLDecoder.decode(urls[i].getFile(), "UTF-8");
			}
			catch(Exception e)
			{
				rethrowAsUnchecked(e);
			}
			
			stok = new StringTokenizer(cp, "/!"); // Exclamation mark to support
													// jar files.
			int cplen = stok.countTokens();
			if(cplen <= toks.size())
			{
				int j = 0;
				for(; stok.hasMoreTokens(); j++)
				{
					if(!stok.nextToken().equals(toks.get(j)))
						break;
				}

				if(j == cplen && cplen > quality)
				{
					ret = "";
					for(int k = j; k < toks.size(); k++)
					{
						if(k > j && k < toks.size())
							ret += ".";
						ret += "" + toks.get(k);
					}
					quality = cplen;
				}
			}
		}
		return ret;
	}
	
//	/**
//	 * Find a package name from a path. Searches the most specific classpath and
//	 * uses the rest of the pathname as package name.
//	 * 
//	 * @param path The directory.
//	 * @return The package.
//	 */
//	public static String convertPathToPackage(String path,
//			ClassLoader classloader)
//	{
//		String ret = null;
//		File fpath = new File(path);
//		if(!fpath.isDirectory())
//			path = fpath.getParent();
//
//		List cps = getClasspathURLs(classloader);
//
//		java.util.List toks = SCollection.createArrayList();
//		StringTokenizer stok = new StringTokenizer(path, File.separator);
//		while(stok.hasMoreTokens())
//			toks.add(stok.nextToken());
//
//		int quality = 0;
//		for(int i = 0; i < cps.size(); i++)
//		{
//			String cp = ((URL)cps.get(i)).getFile();
//			stok = new StringTokenizer(cp, "/!"); // Exclamation mark to support
//													// jar files.
//
//			int cplen = stok.countTokens();
//			if(cplen <= toks.size())
//			{
//				int j = 0;
//				for(; stok.hasMoreTokens(); j++)
//				{
//					if(!stok.nextToken().equals(toks.get(j)))
//						break;
//				}
//
//				if(j == cplen && cplen > quality)
//				{
//					ret = "";
//					for(int k = j; k < toks.size(); k++)
//					{
//						if(k > j && k < toks.size())
//							ret += ".";
//						ret += "" + toks.get(k);
//					}
//					quality = cplen;
//				}
//			}
//		}
//		return ret;
//	}

	// /**
	// * Get the classloader.
	// * Uses the context class loader, if available.
	// */
	// public static ClassLoader getClassLoader()
	// {
	// ClassLoader ret = Thread.currentThread().getContextClassLoader();
	// if(ret==null)
	// {
	// ret = SReflect.class.getClassLoader();
	// }
	// return ret;
	// }

	/**
	 * Get the current classpath as a list of URLs
	 */
	public static List<URL> getClasspathURLs(ClassLoader classloader, boolean includebootpath)
	{
		if(classloader == null)
			classloader = SUtil.class.getClassLoader();

		Set<URL> cps = new LinkedHashSet<URL>(); 
	
		if(SReflect.isAndroid()) 
		{
			cps.addAll(androidUtils().collectDexPathUrls(classloader));
		} 
		else 
		{
			StringTokenizer stok = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
			while(stok.hasMoreTokens())
			{
				try
				{
					String entry = stok.nextToken();
					File file = new File(entry);
					cps.add(file.getCanonicalFile().toURI().toURL());
					
					// Code below does not work for paths with spaces in it.
					// Todo: is above code correct in all cases? (relative/absolute, local/remote, jar/directory)
	//				if(file.isDirectory()
	//						&& !entry
	//								.endsWith(System.getProperty("file.separator")))
	//				{
	//					// Normalize, that directories end with "/".
	//					entry += System.getProperty("file.separator");
	//				}
	//				cps.add(new URL("file:///" + entry));
				}
				catch(MalformedURLException e)
				{
					// Maybe invalid classpath entries --> just ignore.
					// Hack!!! Print warning?
					// e.printStackTrace();
				}
				catch(IOException e)
				{
				}
			}
			
			if(includebootpath && System.getProperty("sun.boot.class.path")!=null)
			{
				stok = new StringTokenizer(System.getProperty("sun.boot.class.path"), System.getProperty("path.separator"));
				while(stok.hasMoreTokens())
				{
					try
					{
						String entry = stok.nextToken();
						File file = new File(entry);
						cps.add(file.getCanonicalFile().toURI().toURL());
						
						// Code below does not work for paths with spaces in it.
						// Todo: is above code correct in all cases? (relative/absolute, local/remote, jar/directory)
		//				if(file.isDirectory()
		//						&& !entry
		//								.endsWith(System.getProperty("file.separator")))
		//				{
		//					// Normalize, that directories end with "/".
		//					entry += System.getProperty("file.separator");
		//				}
		//				cps.add(new URL("file:///" + entry));
					}
					catch(MalformedURLException e)
					{
						// Maybe invalid classpath entries --> just ignore.
						// Hack!!! Print warning?
						// e.printStackTrace();
					}
					catch(IOException e)
					{
					}
				}
			}
//			if(classloader instanceof URLClassLoader)
//			{
//				URL[] urls = ((URLClassLoader)classloader).getURLs();
//				for(int i = 0; i < urls.length; i++)
//					cps.add(urls[i]);
//			}
			cps.addAll(collectClasspathURLs(classloader));
		}
		
		return new ArrayList<URL>(cps);
	}
	
	/**
	 *  Get other contained (but not directly managed) urls from parent classloaders.
	 *  @return The set of urls.
	 */
	public static Set<URL>	collectClasspathURLs(ClassLoader baseloader)
	{
		Set<URL> ret = new LinkedHashSet<URL>();
		SUtil.collectClasspathURLs(baseloader, ret, new HashSet<String>());
		return ret;
	}
	
	/**
	 *  Collect all URLs belonging to a class loader.
	 */
	protected static void	collectClasspathURLs(ClassLoader classloader, Set<URL> set, Set<String> jarnames)
	{
		assert classloader!=null;
		
		if(classloader.getParent()!=null)
		{
			collectClasspathURLs(classloader.getParent(), set, jarnames);
		}
		
		if(classloader instanceof URLClassLoader)
		{
			URL[] urls = ((URLClassLoader)classloader).getURLs();
			for(int i=0; i<urls.length; i++)
			{
				String	name	= SUtil.getFile(urls[i]).getName();
				if(name.endsWith(".jar"))
				{
					String jarname	= getJarName(name);
					jarnames.add(jarname);
				}
			}
			
			for(int i = 0; i < urls.length; i++)
			{
				set.add(urls[i]);
				collectManifestURLs(urls[i], set, jarnames);
			}
		}
		
//		else
//		{
//			try
//			{
//				// Hack for java 9 -> Doesn't work -> not accessible :(
//				Field	ucpf	= SReflect.getField(classloader.getClass(), "ucp");
//				ucpf.setAccessible(true);
//				Object	ucp	=	ucpf.get(classloader);
//				Field	pathf	= SReflect.getField(ucp.getClass(), "path");
//				pathf.setAccessible(true);
//				@SuppressWarnings("unchecked")
//				List<File>	path	= (List<File>)pathf.get(ucp);
//				for(File f: path)
//				{
//					String	name	= f.getName();
//					if(name.endsWith(".jar"))
//					{
//						String jarname	= getJarName(name);
//						jarnames.add(jarname);
//					}
//				}
//				
//				for(File f: path)
//				{
//					set.add(f.toURI().toURL());
//					collectManifestURLs(f.toURI().toURL(), set, jarnames);
//				}
//
//			}
//			catch(Throwable t)
//			{
//				t.printStackTrace();
//			}
//		}
	}
	
	/**
	 *  Get the name of a jar file without extension and version info.
	 */
	public static String	getJarName(String filename)
	{
		String	ret	= filename;
		int	slash	= filename.lastIndexOf('/');
		if(slash!=-1)
		{
			ret	= ret.substring(slash+1);
		}
		Scanner	s	= new Scanner(ret);
		s.findWithinHorizon("(.*?)(-[0-9]+\\.|\\.jar)", 0);
		ret	= s.match().group(1);
//		System.out.println("jar: "+filename+" is "+ret);
		s.close();
		return ret;
	}
	
	/**
	 *  Collect all URLs as specified in a manifest.
	 */
	public static void	collectManifestURLs(URL url, Set<URL> set, Set<String> jarnames)
	{
		File	file	= SUtil.urlToFile(url.toString());
		if(file!=null && file.exists() && !file.isDirectory())	// Todo: load manifest also from directories!?
		{
			JarFile jarfile = null;
	        try 
	        {
	            jarfile	= new JarFile(file);
	            Manifest manifest = jarfile.getManifest();
	            if(manifest!=null)
	            {
	                String	classpath	= manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
	                if(classpath!=null)
	                {
	                	StringTokenizer	tok	= new StringTokenizer(classpath, " ");
	            		while(tok.hasMoreElements())
	            		{
	            			String path = tok.nextToken();
	            			File	urlfile;
	            			
	            			// Search in directory of original jar (todo: also search in local dir!?)
	            			urlfile = new File(file.getParentFile(), path);
	            			
	            			// Try as absolute path
	            			if(!urlfile.exists())
	            			{
	            				urlfile	= new File(path);
	            			}
	            			
	            			// Try as url
	            			if(!urlfile.exists())
	            			{
	            				urlfile	= SUtil.urlToFile(path);
	            			}
	
	            			if(urlfile!=null && urlfile.exists())
	            			{
		            			try
			                	{
		            				if(urlfile.getName().endsWith(".jar"))
		            				{
		            					String jarname	= getJarName(urlfile.getName());
		            					jarnames.add(jarname);
		            				}
		            				URL depurl = urlfile.toURI().toURL();
		            				if(set.add(depurl))
		            				{
		            					collectManifestURLs(depurl, set, jarnames);
		            				}
		            			}
		                    	catch (Exception e)
		                    	{
		                    		//component.getLogger().warning("Error collecting manifest URLs for "+urlfile+": "+e);
		                    	}
	                    	}
	            			else if(!path.endsWith(".jar") || !jarnames.contains(getJarName(path)))
	            			{
	            				//component.getLogger().warning("Jar not found: "+file+", "+path);
	            			}
	               		}
	                }
	            }
		    }
		    catch(Exception e)
		    {
				//component.getLogger().warning("Error collecting manifest URLs for "+url+": "+e);
		    }
	        finally
	        {
	        	try
	        	{
	        		if(jarfile!=null)
	        			jarfile.close();
	        	}
	        	catch(Exception e)
	        	{
	        	}
	        }
		}
	}

	/**
	 * Calculate the cartesian product of parameters. Example: names = {"a",
	 * "b"}, values = {{"1", "2"}, {"3", "4"}} result = {{"a"="1", "b"="3"},
	 * {"a"="2", "b"="3"}, {"a"="1", "b"="4"}, {"a=2", b="4"}}
	 * 
	 * @param names The names.
	 * @param values The values (must be some form of collection, i.e. array,
	 *        list, iterator etc.)
	 */
	public static <T, E> List<Map<T, E>> calculateCartesianProduct(T[] names, E[] values)
	{
		ArrayList<Map<T, E>> ret = SCollection.createArrayList();
		if(names == null || values == null)
			return ret;
		if(names.length != values.length)
			throw new IllegalArgumentException("Must have same length: "
					+ names.length + " " + values.length);

		HashMap<T, E> binding = SCollection.createHashMap();
		Iterator<E>[] iters = new Iterator[values.length];

		for(int i = 0; i < values.length; i++)
		{
			// When one collection is empty -> no binding at all.
			// First binding consists of all first elements.
			iters[i] = SReflect.getIterator(values[i]);
			if(!iters[i].hasNext())
			{
				return ret;
			}
			else
			{
				binding.put(names[i], iters[i].next());
			}
		}
		ret.add(binding);

		// Iterate through binding sets for subsequent bindings.
		while(true)
		{
			// Calculate next binding.
			// Copy old binding and change one value.
			binding = (HashMap<T, E>)binding.clone();
			int i = 0;
			for(; i < values.length && !iters[i].hasNext(); i++)
			{
				// Overflow: Re-init iterator.
				iters[i] = SReflect.getIterator(values[i]);
				binding.put(names[i], iters[i].next());
			}
			if(i < iters.length)
			{
				binding.put(names[i], iters[i].next());
			}
			else
			{
				// Overflow in last iterator: done.
				// Hack: Unnecessarily re-inits all iterators before break ?
				break;
			}
			ret.add(binding);
		}

		return ret;
	}

	/**
	 * Test if a file is a Java source file.
	 * 
	 * @param filename The filename.
	 * @return True, if it is a Java source file.
	 */
	public static boolean isJavaSourceFilename(String filename)
	{
		return filename != null && filename.toLowerCase().endsWith(".java");
	}

	/**
	 * Create a hash map from keys and values.
	 * 
	 * @param keys The keys.
	 * @param values The values.
	 * @return The map.
	 */
	public static <K,T>	Map<K, T> createHashMap(K[] keys, T[] values)
	{
		HashMap<K, T> ret = new HashMap<K, T>();
		for(int i = 0; i < keys.length; i++)
		{
			ret.put(keys[i], values[i]);
		}
		return ret;
	}

	/**
	 * Create a hash set from values.
	 * 
	 * @param values The values.
	 * @return The map.
	 */
	public static <T> Set<T> createHashSet(T[] values)
	{
		Set<T> ret = new HashSet<T>();
		for(int i = 0; i < values.length; i++)
		{
			ret.add(values[i]);
		}
		return ret;
	}

	/**
	 * Create an array list from values.
	 * 
	 * @param values The values.
	 * @return The map.
	 */
	public static <T> List<T> createArrayList(T[] values)
	{
		List<T> ret = new ArrayList<T>();
		for(int i = 0; i < values.length; i++)
		{
			ret.add(values[i]);
		}
		return ret;
	}

//	protected static long convidcnt;

	/** The counter for conversation ids. */
	protected static AtomicLong convidcnt = new AtomicLong();
	
	/**
	 * Create a globally unique conversation id.
	 * @return The conversation id.
	 */
	public static String createUniqueId()
	{
		return createUniqueId(null);
	}
	
	/**
	 * Create a globally unique conversation id.
	 * @return The conversation id.
	 */
	public static String createUniqueId(String name)
	{
		char[] nchars = name == null ? new char[0] : name.toCharArray();
		char[] chars = new char[nchars.length + 45];
		System.arraycopy(nchars, 0, chars, 0, nchars.length);
		int o = nchars.length;
//		int o = 0;
		chars[o++] = '_';
		
		byte[] precached = new byte[32];
		SECURE_RANDOM.nextBytes(precached);
		
		long rndlong = SUtil.bytesToLong(precached, 0);
		for (int i = 0; i < 11; ++i)
			chars[i + o] = ID_CHARS[(int) (rndlong >>> ((i << 2) + (i << 1)) & 0x3F)];
		o += 11;
		rndlong = SUtil.bytesToLong(precached, 8);
		for (int i = 0; i < 11; ++i)
			chars[i + o] = ID_CHARS[(int) (rndlong >>> ((i << 2) + (i << 1)) & 0x3F)];
		o += 11;
		rndlong = SUtil.bytesToLong(precached, 16);
		for (int i = 0; i < 11; ++i)
			chars[i + o] = ID_CHARS[(int) (rndlong >>> ((i << 2) + (i << 1)) & 0x3F)];
		o += 11;
		rndlong = SUtil.bytesToLong(precached, 24);
		for (int i = 0; i < 11; ++i)
			chars[i + o] = ID_CHARS[(int) (rndlong >>> ((i << 2) + (i << 1)) & 0x3F)];

//		long rndlong = SUtil.bytesToLong(precached, 0);
//		for (int i = 0; i < 11; ++i)
//			chars[i + o] = (char) ((rndlong >>> ((i << 2) + (i << 1)) & 0x3F) + 0x30);
//		o += 11;
//		rndlong = SUtil.bytesToLong(precached, 8);
//		for (int i = 0; i < 11; ++i)
//			chars[i + o] = (char) ((rndlong >>> ((i << 2) + (i << 1)) & 0x3F) + 0x30);
//		o += 11;
//		rndlong = SUtil.bytesToLong(precached, 16);
//		for (int i = 0; i < 11; ++i)
//			chars[i + o] = (char) ((rndlong >>> ((i << 2) + (i << 1)) & 0x3F) + 0x30);
//		o += 11;
//		rndlong = SUtil.bytesToLong(precached, 24);
//		for (int i = 0; i < 11; ++i)
//			chars[i + o] = (char) ((rndlong >>> ((i << 2) + (i << 1)) & 0x3F) + 0x30);
		
		String ret = new String(chars);
		return ret;
	}
	
	/**
	 * Create a globally unique conversation id.
	 * 
	 * @return The conversation id.
	 */
//	public static String createUniqueId(String name)
//	{
////		synchronized(SUtil.class)
////		{
//			// return
//			// "id_"+name+"_"+System.currentTimeMillis()+"_"+Math.random()+"_"+(++convidcnt);
//			// return "id_"+name+"_"+Math.random()+"_"+(++convidcnt);
//			return name + "_" + Math.random() + "_" + (convidcnt.incrementAndGet());
////		}
//	}
	
	/**
	 * Create a random id with only alphanumeric chars.
	 * 
	 * @return The id.
	 */
	public static String createPlainRandomId(String name, int length)
	{
//		String rand = createUniqueId(name);
//		return rand.substring(0, name.length() + length + 1);

//		double	rnd	= Math.random();
//		double rnd = FAST_RANDOM.nextDouble();
//		rnd	= rnd * Math.pow(36, length);
//		String rand = Integer.toString((int)rnd, 36);
//		return name+"_";//+rand;
		
		char[] retchars = new char[length + 1];
		int offset = 0;
		retchars[offset] = '_';
		byte[] random = new byte[length];
		FAST_RANDOM.nextBytes(random);
		for (int i = 0; i < random.length; ++i)
		{
			retchars[++offset] = ID_CHARS[(random[i] & 0xFF) % 36];
		}
		return name+new String(retchars);
	}
	
	/**
	 * 
	 */
	protected static void testIntByteConversion()
	{
		Random	rnd	= new Random(123);	
		for(int i=1; i<10000000; i++)
		{
			int	val	= rnd.nextInt(Integer.MAX_VALUE);
			if(i%2==0)	// Test negative values too.
			{
				val	= -val;
			}
			byte[]	bytes	= intToBytes(val);
			int	val2	= bytesToInt(bytes);
			if(val!=val2)
			{
				throw new RuntimeException("Failed: "+val+", "+val2+", "+arrayToString(bytes));
			}
//			System.out.println("Converted: "+val+", "+arrayToString(bytes));
		}
	}

	/**
	 * Convert an absolute path to a relative path based on the current user
	 * directory.
	 */
	public static String convertPathToRelative(String absolute)
	{
		// Special treatment for files in jar file -> just make jar file name relative and keep inner name 
		if(absolute.startsWith("jar:file:") && absolute.indexOf("!")!=-1)
		{
			String	jarname	= absolute.substring(4, absolute.indexOf("!"));
			String	filename	= absolute.substring(absolute.indexOf("!"));
			jarname	= convertPathToRelative(jarname);
			return "jar:"+jarname+filename;
		}
		// Special treatment for file urls 
		if(absolute.startsWith("file:"))
		{
			String	filename	= absolute.substring(5);
			if(File.separatorChar=='\\')
			{
				filename	= filename.replace("/", "\\");
			}
			
			filename	= convertPathToRelative(filename);
			
			if(File.separatorChar=='\\')
			{
				filename	= filename.replace("\\", "/");
			}
			
			return "file:"+filename;
		}
		
		// Build path as list of files (directories).
		File basedir = new File(System.getProperty("user.dir"));
		List<File> basedirs = new ArrayList<File>();
		while(basedir != null)
		{
			basedirs.add(0, basedir);
			basedir = basedir.getParentFile();
		}

		// Build path as list of files (directories).
		File target = new File(absolute);
		List<File> targets = new ArrayList<File>();
		while(target != null)
		{
			targets.add(0, target);
			target = target.getParentFile();
		}

		// Find common path prefix
		int index = 0;
		while(index < basedirs.size() && index < targets.size()
				&& basedirs.get(index).equals(targets.get(index)))
		{
			index++;
		}
		
		String ret;
		// Relative if common directory on drive exists.
		if(index>1)
		{
			StringBuffer buf = new StringBuffer();
			for(int i = index; i < basedirs.size(); i++)
			{
				buf.append("..");
				buf.append(File.separatorChar);
			}
			for(int i = index; i < targets.size(); i++)
			{
				buf.append(targets.get(i).getName());
				if(i != targets.size() - 1)
					buf.append(File.separatorChar);
			}
			ret = buf.toString();
		}
		else
		{
			ret = absolute;
		}
		
//		System.out.println("CPtR: "+ret+", "+absolute);

		return ret;
	}
	
	/**
	 *  Convert a file/string/url array.
	 *  @param urls The url strings.
	 *  @return The urls.
	 */
	public static URL[] toURLs(Object[] urls)
	{
		if(urls==null)
			return null;
		
		List<URL> res = new ArrayList<URL>();
		for(int i=0; i<urls.length; i++)
		{
			try
			{
				res.add(toURL(urls[i]));
			}
			catch(Exception e)
			{
				System.out.println("Warning, invalid URL found: "+urls[i]);
			}
		}
		return res.toArray(new URL[res.size()]);
	}
		
	/**
	 *  Convert a file/string/url.
	 */
	public static URL toURL(Object url)
	{
//		System.out.println(url);
//		long start = System.currentTimeMillis();
		
		URL	ret	= null;
		boolean	jar	= false;
		if(url instanceof String)
		{
			String	string	= (String) url;
			if(string.startsWith("file:") || string.startsWith("jar:file:"))
			{
				try
				{
					string = URLDecoder.decode(string, "UTF-8");
				}
				catch(UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
			
			jar	= string.startsWith("jar:file:");
			url	= jar ? new File(string.substring(9))
				: string.startsWith("file:") ? new File(string.substring(5)) : null;
			
			if(url==null)
			{
				File file = new File(string);
				if(file.exists())
				{
					url	= file;
				}
				else
				{
					file = new File(System.getProperty("user.dir"), string);
					if(file.exists())
					{
						url	= file;
					}
					else
					{
						try
						{
							url	= new URL(string);
						}
						catch (MalformedURLException e)
						{
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		
		if(url instanceof URL)
		{
			ret	= (URL)url;
		}
		else if(url instanceof File)
		{
			try
			{
				String	abs	= ((File)url).getAbsolutePath();
				String	rel	= SUtil.convertPathToRelative(abs);
				
				if(abs.equals(rel))
				{
					if(abs.contains(".."))
					{
						ret = new File(abs).getCanonicalFile().toURI().toURL();
					}
					else
					{
						ret = new File(abs).getAbsoluteFile().toURI().toURL();
					}
				}
				else
				{
					File basedir = new File(System.getProperty("user.dir"));
					
					while(true)
					{
						int cut = 0;
						if(rel.startsWith(".."))
						{
							cut = 2;
						}
						else if(rel.startsWith("/..") || rel.startsWith("\\.."))
						{
							cut = 3;
						}
						else if(rel.startsWith("\\\\.."))
						{
							cut = 4;
						}
						
						if(cut>0)
						{
							basedir = basedir.getParentFile();
							rel = rel.substring(cut);
						}
						else
						{
							break;
						}
					}
					
					if(rel.contains(".."))
					{
						ret = new File(basedir, rel).getCanonicalFile().toURI().toURL();
					}
					else
					{
						ret = new File(basedir, rel).getAbsoluteFile().toURI().toURL();
					}
					
				}
				
//				ret	= abs.equals(rel) ? new File(abs).getCanonicalFile().toURI().toURL()
//					: new File(System.getProperty("user.dir"), rel).getCanonicalFile().toURI().toURL();
				if(jar)
				{
					if(ret.toString().endsWith("!"))
						ret	= new URL("jar:"+ret.toString()+"/");	// Add missing slash in jar url.
					else
						ret	= new URL("jar:"+ret.toString());						
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(url instanceof URI)
		{
			try
			{
				ret = ((URI)url).toURL();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
//		long dur = System.currentTimeMillis()-start;
		
		return ret;
	}
	
	
	
	
	/**
	 *  Convert a URI to a URL but ignore exceptions
	 */
	public static URL toURL0(URI uri)
	{
		URL ret = null;
		try
		{
			ret = uri.toURL();
		}
		catch(Exception e)
		{
			System.out.println("Problem with url conversion: "+uri);
		}
		return ret;
	}
	
	/**
	 *  Convert a URL to a URI but ignore exceptions
	 */
	public static URI toURI0(URL url)
	{
		if(url==null)
			return null;
		
		URI ret = null;
		try
		{
			ret = url.toURI();
		}
		catch(Exception e)
		{
			System.out.println("Problem with url conversion: "+url);
		}
		return ret;
	}
	
	/**
	 *  Convert a URL to a URI but ignore exceptions
	 */
	public static URI toURI(URL url)
	{
		if(url==null)
			return null;
		
		URI ret = null;
		try
		{
			ret = url.toURI();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}


	/**
	 * Convert a scheme, InetAdress and port to a valid URI or throw.
	 * @param scheme
	 * @param address
	 * @param port
	 * @return URI
	 */
	public static URI toURI(String scheme, InetAddress address, int port) {
		if (scheme.endsWith("://")) {
			scheme = scheme.substring(0, scheme.length()-3);
		}
		try {
			return new URI(scheme, null, address.getHostAddress(), port, null, null, null);
		} catch (URISyntaxException e) {
//			e.printStackTrace();
			rethrowAsUnchecked(e);
		}
		return null;
	}


	/**
	 * Convert stringified URI (as used in transports for some weird reason) to URI.
	 * @param transporturi Transport-style URI (tcp-mtp://hostpart:port or tcp-mtp://[h:o:s:t%scope]:port for ipv6)
	 * @return URI
	 */
	public static URI toURI(String transporturi) {
		URI ret = null;
		try {
			// by default, transporturis should be valid URIs.
			ret = new URI(transporturi);
			if (ret.getHost() == null) { // URI may not throw, but instead use the whole string as "authority" :(
				throw new URISyntaxException(transporturi, "No hostname found while converting to URI");
			}
		} catch (URISyntaxException e) {
			// for backword compatibility, handle wrongly formatted IPv6 transport "addresses"
			// see https://www.ietf.org/rfc/rfc2732.txt for correct format.
			if (transporturi.contains("%") && !transporturi.contains("[")) {
//				tcp-mtp://fe80:0:0:0:8cf:5aff:feeb:f199%eth0:42716
				int schemaend = transporturi.indexOf("://");
				int portdiv = transporturi.lastIndexOf(':');
				int scopediv = transporturi.lastIndexOf('%');

				String hostname = transporturi.substring(schemaend+3, portdiv);
				String scheme = transporturi.substring(0, schemaend);
				Integer port = null;
				if(portdiv>scopediv) // has port
				{
					port = Integer.parseInt(transporturi.substring(portdiv+1));
				}
				try {
					ret =  new URI(scheme, null, hostname, port, null, null, null);
//					System.out.println("silently converted wrongly formatted URI: " + transporturi);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
					rethrowAsUnchecked(e);
				}
			} else {
				rethrowAsUnchecked(e);
			}
		}
		return ret;
	}
	
	/**
	 *  Sleep the current thread, ignore exceptions.
	 *  @param  millis Time to sleep in milliseconds
	 */
	public static final void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
		}
	}
	
	/**
	 *  Converts an exception to RuntimeException, returns original
	 *  if already a RuntimeException.
	 *  
	 *  @param e The throwable to be returned as RuntimeException
	 *  @return RuntimeException.
	 */
	public static final RuntimeException convertToRuntimeException(Throwable e)
	{
		if (e instanceof RuntimeException)
		{
			return ((RuntimeException) e);
		}
		return new RuntimeException(e);
	}
	
	/**
	 *  Re-throws a throwable as a RuntimeException.
	 *  If the throwable is already a RuntimeException,
	 *  the exception itself is thrown, otherwise it is
	 *  wrapped.
	 *  
	 *  @param e The throwable to be thrown as RuntimeException
	 */
	public static final void rethrowAsUnchecked(Throwable e)
	{
		if (e instanceof Error)
			throw ((Error)e);
		throw convertToRuntimeException(e);
	}

	/**
	 * Main method for testing. / public static void main(String[] args) {
	 * String res1 = getRelativePath("c:/a/b/c", "c:/a/d"); String res2 =
	 * getRelativePath("c:/a/b/c", "c:/a/b/c"); //String res2 =
	 * getRelativePath("c:/a/b/c", "d:/a/d"); String res3 =
	 * getRelativePath("c:/a/b/c", "c:/a/b/c/d/e"); //String tst =
	 * "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww"
	 * ; //System.out.println(tst); //System.out.println(SUtil.wrapText(tst));
	 * /*String[] a = new String[]{"a1", "a2", "a3"}; Integer[] b = new
	 * Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)};
	 * System.out.println(arrayToString(joinArbitraryArrays(new Object[]{a,
	 * b})));
	 */

	/*
	 * try { URL target = new URL("file:///C:/projects/jadex/lib/examples.jar");
	 * DynamicURLClassLoader loader = new DynamicURLClassLoader(new URL[0]); try
	 * { Class clazz = loader.loadClass("jadex.examples.ping.PingPlan");
	 * System.out.println("Loaded class: "+clazz); }
	 * catch(ClassNotFoundException e){System.out.println(e);}
	 * loader.addURL(target); try { Class clazz =
	 * loader.loadClass("jadex.examples.ping.PingPlan");
	 * System.out.println("Loaded class: "+clazz); }
	 * catch(ClassNotFoundException e){System.out.println(e);} } catch(Exception
	 * e) { System.out.println(e); } }
	 */
	
	public static void main(String[] args)
	{
//		String res = SUtil.makeConform("uniique-dialogservice.de/ues4/rc?f=https://plus.google.com/+targobank?koop_id=mar_vermoegen18");
//		System.out.println(res);
		
//		String tst = "jar:file:/C:/projects/jadexgit/jadex-platform-standalone-launch/../../inno/vemaproda-eventsystem/target/vemaproda-eventsystem-0.5-SNAPSHOT.jar!/org/codehaus/plexus/context/ContextMapAdapter.class";
		String tst = "C:\\Users\\Lars\\bpmntutorial2\\bpmntutorial\\target\\classes\\B1_simple.bpmn2";
		
		long start = System.currentTimeMillis();
		
		for(int i=0; i<30000; i++)
		{
			SUtil.toURL(tst);
		}
		
		System.out.println("needed: "+(System.currentTimeMillis()-start)/1000);
	}
	
	/**
	 *  Converts a number of bytes
	 *  into a human-friendly binary prefix unit string
	 *  (kiB, MiB, GiB, ...).
	 *  
	 *  @param bytes Number of bytes.
	 */
	public static String formatByteSize(long bytes)
	{
		String ret = "";
		if (bytes >= 10995116277760L)
		{
			double tmpbyte = (double) bytes / 1099511627776.0;
			ret = Math.round(tmpbyte) + "TiB";
		}
		else if (bytes >= 10737418240L)
		{
			double tmpbytes = (double) bytes / 1073741824.0;
			ret = Math.round(tmpbytes) + "GiB";
		}
		else if (bytes >= 10485760L)
		{
			double tmpbytes = (double) bytes / 1048576.0;
			ret = Math.round(tmpbytes) + "MiB";
		}
		else if (bytes >= 10240L)
		{
			double tmpbytes = (double) bytes / 1024.0;
			ret = Math.round(tmpbytes) + "KiB";
		}
		else
		{
			ret = bytes + "B";
		}
		
		return ret;
	}
	
	/**
	 *  Convert bytes to a short.
	 */
	public static short bytesToShort(byte[] buffer)
	{
		assert buffer.length == 2;
		
		return bytesToShort(buffer, 0);
	}
	
	/**
	 *  Convert bytes to a short.
	 */
	public static short bytesToShort(byte[] buffer, int offset)
	{
		short value = (short)((0xFF & buffer[offset]) << 8);
		value |= (0xFF & buffer[offset + 1]);

		return value;
	}

	/**
	 *  Convert a short to bytes.
	 */
	public static byte[] shortToBytes(int val)
	{
		byte[] buffer = new byte[2];

		shortIntoBytes(val, buffer, 0);

		return buffer;
	}
	
	/**
	 *  Convert a short into byte array.
	 */
	public static void shortIntoBytes(int val, byte[] buffer, int offset)
	{
		buffer[offset] = (byte)((val >>> 8) & 0xFF);
		buffer[offset+1] = (byte)(val & 0xFF);
	}

	/**
	 *  Convert bytes to an integer.
	 */
	public static int bytesToInt(byte[] buffer)
	{
		assert buffer.length == 4;
//		if(buffer.length != 4)
//		{
//			throw new IllegalArgumentException("buffer length must be 4 bytes!");
//		}
		
		return bytesToInt(buffer, 0);
	}
	
	/**
	 *  Convert bytes to an integer.
	 */
	public static int bytesToInt(byte[] buffer, int offset)
	{
		int value = (0xFF & buffer[offset]) << 24;
		value |= (0xFF & buffer[offset+1]) << 16;
		value |= (0xFF & buffer[offset+2]) << 8;
		value |= (0xFF & buffer[offset+3]);
		
		return value;
	}

	/**
	 *  Convert an integer to bytes.
	 */
	public static byte[] intToBytes(int val)
	{
		byte[] buffer = new byte[4];
		
		intIntoBytes(val, buffer, 0);

		return buffer;
	}
	
	/**
	 *  Convert a long to bytes.
	 */
	public static void intIntoBytes(int val, byte[] buffer, int offset)
	{
		buffer[offset++] = (byte)((val >>> 24) & 0xFF);
		buffer[offset++] = (byte)((val >>> 16) & 0xFF);
		buffer[offset++] = (byte)((val >>> 8) & 0xFF);
		buffer[offset++] = (byte)(val & 0xFF);
	}
	
//	/**
//	 *  Convert an ip to a long.
//	 *  @param ip The ip address.
//	 *  @return The long.
//	 */
//	public static long ipToLong(InetAddress ip)
//	{
//		byte[] octets = ip.getAddress();
//		long result = 0;
//		for(byte octet : octets)
//		{
//			result <<= 8;
//			result |= octet & 0xff;
//		}
//		return result;
//	}

	/**
	 *  Convert bytes to a long.
	 */
	public static long bytesToLong(byte[] buffer)
	{
		assert buffer.length == 8;

		long value = (0xFFL & buffer[0]) << 56L;
		value |= (0xFFL & buffer[1]) << 48L;
		value |= (0xFFL & buffer[2]) << 40L;
		value |= (0xFFL & buffer[3]) << 32L;
		value |= (0xFFL & buffer[4]) << 24L;
		value |= (0xFFL & buffer[5]) << 16L;
		value |= (0xFFL & buffer[6]) << 8L;
		value |= (0xFFL & buffer[7]);

		return value;
	}
	
	/**
	 *  Convert bytes to a long.
	 */
	public static long bytesToLong(byte[] buffer, int offset)
	{
		long value = (0xFFL & buffer[offset++]) << 56L;
		value |= (0xFFL & buffer[offset++]) << 48L;
		value |= (0xFFL & buffer[offset++]) << 40L;
		value |= (0xFFL & buffer[offset++]) << 32L;
		value |= (0xFFL & buffer[offset++]) << 24L;
		value |= (0xFFL & buffer[offset++]) << 16L;
		value |= (0xFFL & buffer[offset++]) << 8L;
		value |= (0xFFL & buffer[offset++]);

		return value;
	}

	/**
	 *  Convert a long to bytes.
	 */
	public static byte[] longToBytes(long val)
	{
		byte[] buffer = new byte[8];

		buffer[0] = (byte)((val >>> 56) & 0xFF);
		buffer[1] = (byte)((val >>> 48) & 0xFF);
		buffer[2] = (byte)((val >>> 40) & 0xFF);
		buffer[3] = (byte)((val >>> 32) & 0xFF);
		buffer[4] = (byte)((val >>> 24) & 0xFF);
		buffer[5] = (byte)((val >>> 16) & 0xFF);
		buffer[6] = (byte)((val >>> 8) & 0xFF);
		buffer[7] = (byte)(val & 0xFF);

		return buffer;
	}
	
	/**
	 *  Convert a long to bytes.
	 */
	public static void longIntoBytes(long val, byte[] buffer)
	{
		longIntoBytes(val, buffer, 0);
	}
	
	/**
	 *  Convert a long to bytes.
	 */
	public static void longIntoBytes(long val, byte[] buffer, int offset)
	{
		buffer[offset++] = (byte)((val >>> 56) & 0xFF);
		buffer[offset++] = (byte)((val >>> 48) & 0xFF);
		buffer[offset++] = (byte)((val >>> 40) & 0xFF);
		buffer[offset++] = (byte)((val >>> 32) & 0xFF);
		buffer[offset++] = (byte)((val >>> 24) & 0xFF);
		buffer[offset++] = (byte)((val >>> 16) & 0xFF);
		buffer[offset++] = (byte)((val >>> 8) & 0xFF);
		buffer[offset++] = (byte)(val & 0xFF);
	}
	
	/**
	 *  Get the network ip for an internet address and the prefix length.
	 *  Example: ip: 134.100.33.22 / prefixlen: 24 (c class) -> 134.100.33.0
	 *  @param addr The internet address.
	 *  @param prefixlen The prefix length.
	 *  @return The net address.
	 */
	public static InetAddress getNetworkIp(InetAddress addr, short prefixlen)
	{
		InetAddress ret = null;
		try
		{
			if(addr instanceof Inet4Address)
			{
				int ad = SUtil.bytesToInt(addr.getAddress());
				ad >>>= 32-prefixlen;
				ad <<= 32-prefixlen;
				ret = InetAddress.getByAddress(SUtil.intToBytes(ad));
			}
			else if(addr instanceof Inet6Address)
			{
				// Use only first 64 bit of IPv6 address.
				byte[]	baddr	= new byte[8];
				System.arraycopy(addr.getAddress(), 0, baddr, 0, 8);
				long ad = SUtil.bytesToLong(baddr);
				System.arraycopy(SUtil.longToBytes(ad), 0, baddr, 0, 8);
				ad >>>= 8;
				System.arraycopy(SUtil.longToBytes(ad), 0, baddr, 0, 8);
				ad <<= 8;
				baddr	= new byte[16];
				System.arraycopy(SUtil.longToBytes(ad), 0, baddr, 0, 8);
				ret = InetAddress.getByAddress(baddr);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Get bytes as human readable string.
	 */
	public static String	bytesToString(long bytes)
	{
		String ret;
		if(bytes>0)
		{
		    int	unit	= (int)(Math.log10(bytes)/Math.log10(1024));	// 1=bytes, 2=kBytes, ...
		    double	value	= bytes/Math.pow(1024, unit);	// value between 1.0 .. 1023.999...
		    ret	= value>=100 ? BYTEFORMATTER3.format(value)
		    	: value>=10 ? BYTEFORMATTER2.format(value)
		    	: BYTEFORMATTER1.format(value);
		    ret	+= " "+BYTE_UNITS[unit];
		}
		else
		{
			ret = bytes + BYTE_UNITS[0];
		}
	    return ret;
		
//		String	ret;
//		if(bytes>=1024*1024*1024*100L)
//		{
//			ret	= bytes/(1024*1024*1024) + " GB"; 
//		}
//		else if(bytes>=1024*1024*1024*10L)
//		{
//			ret	= (bytes*10/(1024*1024*1024))/10.0 + " GB"; 
//		}
//		else if(bytes>=1024*1024*1024)
//		{
//			ret	= (bytes*100/(1024*1024*1024))/100.0 + " GB"; 
//		}
//		else if(bytes>=1024*1024*100)
//		{
//			ret	= bytes/(1024*1024) + " MB"; 
//		}
//		else if(bytes>=1024*1024*10)
//		{
//			ret	= (bytes*10/(1024*1024))/10.0 + " MB"; 
//		}
//		else if(bytes>=1024*1024)
//		{
//			ret	= (bytes*100/(1024*1024))/100.0 + " MB"; 
//		}
//		else if(bytes>=1024*100)
//		{
//			ret	= bytes/1024 + " KB"; 
//		}
//		else if(bytes>=1024*10)
//		{
//			ret	= (bytes*10/1024)/10.0 + " KB"; 
//		}
//		else if(bytes>=1024)
//		{
//			ret	= (bytes*100/1024)/100.0 + " KB"; 
//		}
//		else
//		{
//			ret	= Long.toString(bytes)+ " B"; 
//		}
//		return ret;
	}


	/**
	 *  Convert an URL to a local file name.
	 *  @param url The url.
	 *  @return The absolute path to the url resource.
	 */
	public static String convertURLToString(URL url)
	{
		File f;
		try
		{
			f	= new File(url.toURI());
		}
		catch(Exception e)
		{
			f	= new File(url.getPath());
		}
		
		// Hack!!! Above code doesnt handle relative url paths. 
		if(!f.exists())
		{
			File newfile = new File(new File("."), url.getPath());
			if(newfile.exists())
			{
				f = newfile;
			}
		}
		
		return f.getAbsolutePath();
	}
	
	/**
	 *  Test if a file name is contained.
	 */
	public static int indexOfFilename(String url, List<String> urlstrings)
	{
		int ret = -1;
		try
		{
			File file = urlToFile(url);
			if(file==null)
				file	= new File(url);
			for(int i=0; file!=null && i<urlstrings.size() && ret==-1; i++)
			{
				String	totest	= (String)urlstrings.get(i);
				File test = urlToFile(totest);
				if(test==null)
					test	= new File(totest);
				if(test!=null && file.getCanonicalPath().equals(test.getCanonicalPath()))
					ret = i;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 *  Convert an URL to a file.
	 *  @return null, if the URL is neither 'file:' nor 'jar:file:' URL and no path point to an existing file.
	 */
	public static File urlToFile(String url)
	{
		File	file	= null;
		if(url.startsWith("file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(5));
		}
		else if(url.startsWith("jar:file:"))
		{
			try
			{
				url	= URLDecoder.decode(url, "UTF-8");
			}
			catch(UnsupportedEncodingException uee)
			{
			}
			file	= new File(url.substring(9));
		}
		else
		{
			file	= new File(url);
			if(!file.exists())
			{
				file	= null;
			}
		}
		return file;
	}
	
	/**
	 *  Creates an ISO 8601-compliant string out of a java Date object.
	 *  
	 *  @param date The date object.
	 *  @return ISO 8601-compliant string.
	 */
	public static String dateToIso8601(Date date)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(date);
	}
	
	/**
	 *  Attempts to create a date object in Java from an ISO 8601 string.
	 *  
	 *  @param isostring The ISO string, must contain enough data for the date object.
	 *  @return Date object.
	 */
	public static Date dateFromIso8601(String isostring)
	{
		Date ret = null;
		try
		{
			Class<?> datetimeformatter = Class.forName("java.time.format.DateTimeFormatter");
			Method parse = datetimeformatter.getMethod("parse", CharSequence.class);
			Object[] formatters = new Object[]
			{
				datetimeformatter.getField("ISO_INSTANT").get(null),
				datetimeformatter.getField("ISO_DATE_TIME").get(null)
			};
			
			Class<?> instantclass = Class.forName("java.time.Instant");
			Method fromi = Date.class.getMethod("from", instantclass);
			Class<?> temporalaccessorclass = Class.forName("java.time.temporal.TemporalAccessor");
			Method fromta = instantclass.getMethod("from", temporalaccessorclass);
			
			for (int i = 0; i < formatters.length && ret == null; ++i)
			{
				try
				{
					Object temporalaccessor = parse.invoke(formatters[i], isostring);
					Object instant = fromta.invoke(null, temporalaccessor);
					ret = (Date) fromi.invoke(null, instant);
				}
				catch (Exception e)
				{
				}
			}
		}
		catch (Exception e)
		{
		}
		
		if (ret == null)
		{
			for (int i = 0; i < ISO8601UTCFALLBACKS.length && ret == null; ++i)
			{
				String fstr = ISO8601UTCFALLBACKS[i];
				SimpleDateFormat df = new SimpleDateFormat(fstr);
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				try
				{
					ret = df.parse(isostring);
				}
				catch (Exception e)
				{
				}
			}
		}
		
		if (ret == null)
		{
			for (int i = 0; i < ISO8601ZONEDFALLBACKS.length && ret == null; ++i)
			{
				String fstr = ISO8601ZONEDFALLBACKS[i];
				SimpleDateFormat df = new SimpleDateFormat(fstr);
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				try
				{
					ret = df.parse(isostring);
				}
				catch (Exception e)
				{
				}
			}
		}
		
		if (ret == null)
			throw new RuntimeException(new ParseException("Failed to parse ISO 8601 date string: " + isostring, 0));
		
		return ret;
	}

	/**
	 *  Add a listener to System.out.
	 */
	public static synchronized void	addSystemOutListener(IChangeListener listener)
	{
		if(!(System.out instanceof AccessiblePrintStream)
			|| !(((AccessiblePrintStream)System.out).out instanceof ListenableStream))
		{
			System.setOut(new AccessiblePrintStream(new ListenableStream(System.out, "out")));
		}
		((ListenableStream)((AccessiblePrintStream)System.out).out).addLineListener(listener);
	}
	
	/**
	 *  Remove a listener from System.out.
	 */
	public static synchronized void	removeSystemOutListener(IChangeListener listener)
	{
		if(System.out instanceof AccessiblePrintStream
			&& ((AccessiblePrintStream)System.out).out instanceof ListenableStream)
		{
			((ListenableStream)((AccessiblePrintStream)System.out).out).removeLineListener(listener);
		}
	}
	
	/**
	 *  Add a listener to System.err.
	 */
	public static synchronized void	addSystemErrListener(IChangeListener listener)
	{
		if(!(System.err instanceof AccessiblePrintStream)
			|| !(((AccessiblePrintStream)System.err).out instanceof ListenableStream))
		{
			System.setErr(new AccessiblePrintStream(new ListenableStream(System.err, "err")));
		}
		((ListenableStream)((AccessiblePrintStream)System.err).out).addLineListener(listener);
	}
	
	/**
	 *  Remove a listener from System.err.
	 */
	public static synchronized void	removeSystemErrListener(IChangeListener listener)
	{
		if(System.err instanceof AccessiblePrintStream
			&& ((AccessiblePrintStream)System.err).out instanceof ListenableStream)
		{
			((ListenableStream)((AccessiblePrintStream)System.err).out).removeLineListener(listener);
		}
	}
	
	protected static OutputStream	OUT_FOR_SYSTEM_IN;
	
	/**
	 *  Get an output stream that is automatically fed into the new System.in,
	 *  i.e. this method replaces System.in and delivers an output stream to
	 *  which can be written.
	 *  
	 *  Note that writing to the output stream may block when no one reads from
	 *  system in (default buffer size is 1024 characters).
	 */
	public static synchronized OutputStream getOutForSystemIn() throws IOException
	{
		if(OUT_FOR_SYSTEM_IN==null)
		{
			final PipedInputStream	snk	= new PipedInputStream();
			OUT_FOR_SYSTEM_IN	= new PipedOutputStream(snk);
			
			final InputStream	sysin	= System.in;
			InputStream	in	= new InputStream()
			{
				public int available() throws IOException
				{
					return sysin.available() + snk.available();
				}
				
				public int read() throws IOException
				{
					while(sysin.available()==0 && snk.available()==0)
					{
						try
						{
							Thread.sleep(500);
						}
						catch(InterruptedException e)
						{
						}
					}
					return snk.available()!=0 ? snk.read() : sysin.read();
				}
				
			    public int read(byte b[], int off, int len) throws IOException
			    {
			        if(b==null)
			        {
			            throw new NullPointerException();
			        }
			        else if(off<0 || len<0 || len>b.length-off)
			        {
			            throw new IndexOutOfBoundsException();
			        }
			        else if(len==0)
			        {
			            return 0;
			        }
	
			        int	c = read();
			        if(c==-1)
			        {
			            return -1;
			        }
			        b[off]	= (byte)c;
	
			        int i = 1;
			        try
			        {
			            for(; available()>0 && i<len; i++)
			            {
			                c	= read();
			                if(c==-1)
			                {
			                    break;
			                }
			                b[off+i]	= (byte)c;
			            }
			        }
			        catch (IOException ee)
			        {
			        }
			        return i;
			    }
	
			};
			System.setIn(in);
		}
		
		return OUT_FOR_SYSTEM_IN;
	}
	
	/**
	 *  Get a IPV4 address of the local host.
	 *  Ignores loopback address and V6 addresses.
	 *  @return First found IPV4 address.
	 */
	public static InetAddress getInet4Address()
	{
		InetAddress ret = null;
		
		try
		{
			for(NetworkInterface ni: getNetworkInterfaces())
			{
				Enumeration e2 = ni.getInetAddresses();
				while(e2.hasMoreElements() && ret==null)
				{
					InetAddress tmp = (InetAddress)e2.nextElement();
					if(tmp instanceof Inet4Address && !tmp.isLoopbackAddress())
						ret = (InetAddress)tmp;
				}
			}
			
			if(ret==null)
			{
				InetAddress tmp = InetAddress.getLocalHost();
				if(tmp instanceof Inet4Address && !tmp.isLoopbackAddress())
					ret = (InetAddress)tmp;
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Get a IPV4 address of the local host.
	 *  Ignores loopback address and V4 addresses.
	 *  @return First found IPV4 address.
	 */
	public static InetAddress getInet6Address()
	{
		InetAddress ret = null;
		
		try
		{
			for(NetworkInterface ni: getNetworkInterfaces())
			{
				Enumeration e2 = ni.getInetAddresses();
				while(e2.hasMoreElements() && ret==null)
				{
					InetAddress tmp = (InetAddress)e2.nextElement();
					if(tmp instanceof Inet6Address && !tmp.isLoopbackAddress())
						ret = (InetAddress)tmp;
				}
			}
			
			if(ret==null)
			{
				InetAddress tmp = InetAddress.getLocalHost();
				if(tmp instanceof Inet6Address && !tmp.isLoopbackAddress())
					ret = (InetAddress)tmp;
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Get an address of the local host.
	 *  Tries to get a IPV4 address and if not available 
	 *  tries to get a IPV6 address.
	 *  @return First found IPV4 or IPV6 address.
	 */
	public static InetAddress getInetAddress()
	{
		InetAddress ret = getInet4Address();
		if(ret==null)
			ret = getInet6Address();
		return ret;
	}
	
	/**
	 *  Get the network prefix length for IPV4 address
	 *  24=C, 16=B, 8=A classes. 
	 *  Returns -1 in case of V6 address.
	 *  @param iadr The address.
	 *  @return The length of the prefix.
	 */
	public static short getNetworkPrefixLength(InetAddress iadr)
	{
		short ret = -1;
		if(!SReflect.isAndroid() || androidUtils().getAndroidVersion() > 8)
		{
			ret	= SNonAndroid.getNetworkPrefixLength(iadr);
		}
		
		return ret;
	}
	
	/**
	 *  Copy an array.
	 */
	public static <T> T[] copyArray(T[] original)
	{
		// Arrays.copyOf() not present in android 2.2 (version 8).
		T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), original.length);
        System.arraycopy(original, 0, copy, 0, original.length);
        
        return copy;
	}

	
	/**
	 *  Get the source code base using a packagename and a filename.
	 *  Looks at the filename and subtracts the package name.
	 *  @param filename The filename.
	 *  @param pck The package name.
	 *  @return The source base. 
	 */
	public static String getCodeSource(String filename, String pck)
	{
		// Use unix separator for file or jar URLs.
		char	sep	= filename.startsWith("file:") || filename.startsWith("jar:file:") ? '/' : File.separatorChar;
		int occ = pck!=null? countOccurrences(pck, '.')+2: 1;
		String ret = filename;
		for(int i=0; i<occ; i++)
		{
			int idx = ret.lastIndexOf(sep);
			if(idx>0)
			{
				ret = ret.substring(0, idx);
			}
			else
			{
				// Try other variant before fail
				if('/'!=File.separatorChar)
				{
					if('/'==sep)
						sep = File.separatorChar;
					else
						sep = '/';
					idx = ret.lastIndexOf(sep);
					if(idx>0)
						ret = ret.substring(0, idx);
					else
						throw new RuntimeException("Corrupt filename: "+filename);
				}
				else
				{
					throw new RuntimeException("Corrupt filename: "+filename);
				}
			}
		}
		
		if(ret.startsWith("jar:file:") && ret.endsWith("!"))
		{
			// Strip 'jar:' and '!', because java.net.URL doesn't like jar URLs without...
			ret	= ret.substring(4, ret.length()-1);
		}
		
		return ret;
	}
	
	/**
	 *  Count the occurrences of a char in a string.
	 *  @param string The string.
	 *  @param find The char to find.
	 *  @return The number of occurrences.
	 */
	public static int countOccurrences(String string, char find)
	{
	    int count = 0;
	    for(int i=0; i < string.length(); i++)
	    {
	        if(string.charAt(i) == find)
	        {
	             count++;
	        }
	    }
	    return count;
	}

	/** The cached network interfaces. */
	protected static volatile List<NetworkInterface>	NIS;
	
	/** The time of the last caching of network interfaces. */
	protected static long	NISTIME;
	
	/**
	 *  Get the network interfaces.
	 *  The result is cached for a short time period to speed things up.
	 */
	public static List<NetworkInterface> getNetworkInterfaces()	throws SocketException
	{
		if(NIS==null || (System.currentTimeMillis()-NISTIME)>30000)
		{
			Enumeration<NetworkInterface>	nis	= NetworkInterface.getNetworkInterfaces();
			if(nis!=null)
			{
				NIS = Collections.list(nis);
			}
			else
			{
				NIS	= Collections.emptyList();
			}
			NISTIME	= System.currentTimeMillis();
		}
		return NIS;
	}
	
	/**
	 *  Get the addresses to be used for transports.
	 */
	public static InetAddress[]	getNetworkAddresses() throws SocketException
	{
		// Determine useful transport addresses.
		Set<InetAddress>	addresses	= new HashSet<InetAddress>();	// global network addresses (uses all)
		Set<InetAddress>	sitelocal	= new HashSet<InetAddress>();	// local network addresses e.g. 192.168.x.x (use one v4 and one v6 if no global)
		Set<InetAddress>	linklocal	= new HashSet<InetAddress>();	// link-local fallback addresses e.g. 169.254.x.x (use one v4 and one v6 if no global or local)
		Set<InetAddress>	loopback	= new HashSet<InetAddress>();	// loopback addresses e.g. 127.0.0.1 (use one v4 and one v6 if no global or local or link-local)
		
		boolean	v4	= false;	// true when one v4 address was added.
		boolean	v6	= false;	// true when one v6 address was added.
		
		for(NetworkInterface ni: getNetworkInterfaces())
		{
			for(Enumeration<InetAddress> iadrs = ni.getInetAddresses(); iadrs.hasMoreElements(); )
			{
				InetAddress addr = iadrs.nextElement();
//				System.out.println("addr: "+addr+" "+addr.isAnyLocalAddress()+" "+addr.isLinkLocalAddress()+" "+addr.isLoopbackAddress()+" "+addr.isSiteLocalAddress()+", "+ni.getDisplayName());
				if(addr.isLoopbackAddress())
				{
					loopback.add(addr);
				}
				else if(addr.isLinkLocalAddress())
				{
					linklocal.add(addr);
				}
				else if(addr.isSiteLocalAddress())
				{
					sitelocal.add(addr);
				}
				else
				{
					v4	= v4 || addr instanceof Inet4Address;
					v6	= v6 || addr instanceof Inet6Address;
					if (addr instanceof  Inet6Address) {
						try {
							addr = Inet6Address.getByAddress(addr.getHostAddress(), addr.getAddress());
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
					}
					addresses.add(addr);
				}
			}
		}

		boolean	tmpv4	= v4;
		boolean	tmpv6	= v6;
		for(Iterator<InetAddress> it=sitelocal.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				if (addr instanceof  Inet6Address) {
					try {
						addr = Inet6Address.getByAddress(addr.getHostAddress(), addr.getAddress());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
				addresses.add(addr);
			}
		}
		
		tmpv4	= v4;
		tmpv6	= v6;
		for(Iterator<InetAddress> it=linklocal.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				if (addr instanceof  Inet6Address) {
					try {
						addr = Inet6Address.getByAddress(addr.getHostAddress(), addr.getAddress());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
				addresses.add(addr);
			}
		}
		
		tmpv4	= v4;
		tmpv6	= v6;
		for(Iterator<InetAddress> it=loopback.iterator(); it.hasNext(); )
		{
			InetAddress	addr	= it.next();
			if(!tmpv4 && addr instanceof Inet4Address || !tmpv6 && addr instanceof Inet6Address)
			{
				v4	= v4 || addr instanceof Inet4Address;
				v6	= v6 || addr instanceof Inet6Address;
				if (addr instanceof  Inet6Address) {
					try {
						addr = Inet6Address.getByAddress(addr.getHostAddress(), addr.getAddress());
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
				addresses.add(addr);
			}
		}
		
//		InetAddress iaddr = InetAddress.getLocalHost();
//		String lhostname = iaddr.getCanonicalHostName();
//		InetAddress[] laddrs = InetAddress.getAllByName(lhostname);
//
//		addrs.add(getAddress(iaddr.getHostAddress(), this.port));
//		// Get the ip addresses
//		for(int i=0; i<laddrs.length; i++)
//		{
//			String hostname = laddrs[i].getHostName().toLowerCase();
//			String ip_addr = laddrs[i].getHostAddress();
//			addrs.add(getAddress(ip_addr, this.port));
//			if(!ip_addr.equals(hostname))
//			{
//				// We have a fully qualified domain name.
//				addrs.add(getAddress(hostname, this.port));
//			}
//		}
		
//		System.out.println("addresses: "+addresses);

		return addresses.toArray(new InetAddress[addresses.size()]);
	}
	
	/**
	 *  Unzip a file into a specific dir.
	 *  @param zip The zip file.
	 *  @param dir The target dir.
	 */
	public static void unzip(ZipFile zip, File dir)
	{
		Enumeration<? extends ZipEntry> files = zip.entries();
		FileOutputStream fos = null;
		InputStream is = null;
		
		for(ZipEntry entry=files.nextElement(); files.hasMoreElements(); entry=files.nextElement())
		{
			try
			{
				is = zip.getInputStream(entry);
				byte[] buffer = new byte[8192];
				int bytesRead = 0;

				File f = new File(dir.getAbsolutePath()+ File.separator + entry.getName());

				if(entry.isDirectory())
				{
					f.mkdirs();
					continue;
				}
				else
				{
					f.getParentFile().mkdirs();
					f.createNewFile();
				}

				fos = new FileOutputStream(f);

				while((bytesRead = is.read(buffer))!= -1)
				{
					fos.write(buffer, 0, bytesRead);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(fos!=null)
				{
					try
					{
						fos.close();
					}
					catch(IOException e)
					{
					}
				}
			}
		}
		if(is!=null)
		{
			try
			{
				is.close();
			}
			catch(IOException e)
			{
			}
		}
		try
		{
			zip.close();
		}
		catch(IOException e)
		{
		}
	}
	
	/**
	 *  Delete a directory completely (including all subdirs and files).
	 *  @param dir The dir to delete. 
	 *  @return True, if was successfully deleted.
	 */
	static public boolean deleteDirectory(File dir)
	{
		if(dir.exists() && dir.isDirectory())
		{
			File[] files = dir.listFiles();
			if(files!=null)
			{
				for(int i=0; i<files.length; i++)
				{
					if(files[i].isDirectory())
					{
						deleteDirectory(files[i]);
					}
					else
					{
						files[i].delete();
					}
				}
			}
		}
		return dir.delete();
	}

	/**
	 *  An subclass of print stream to allow accessing the underlying stream.
	 */
	public static class AccessiblePrintStream	extends PrintStream
	{
		//-------- attributes --------
		
		/** The underlying output stream. */
		protected OutputStream	out;
		
		//-------- constructors --------
		
		/**
		 *  Create an accessible output stream.
		 */
		public AccessiblePrintStream(OutputStream out)
		{
			super(out);
			this.out	= out;
		}
	}

	//-------- abstractions for Android --------
	
	/**
	 *  Get the home directory.
	 */
	public static File	getHomeDirectory()
	{
		return SReflect.isAndroid() ? new File(System.getProperty("user.home")) : SNonAndroid.getHomeDirectory();		
	}
	
	/**
	 *  Get the home directory.
	 */
	public static File	getDefaultDirectory()
	{
		// Todo: default directory on android?
		return SReflect.isAndroid() ? new File(System.getProperty("user.home")) : SNonAndroid.getDefaultDirectory();		
	}
	
	/**
	 *  Get the parent directory.
	 */
	public static File	getParentDirectory(File file)
	{
		// Todo: parent directory on android?
		return SReflect.isAndroid() ? file.getParentFile() : SNonAndroid.getParentDirectory(file);		
	}

	/**
	 *  Get the files of a directory.
	 */
	public static File[]	getFiles(File file, boolean hiding)
	{
		// Todo: hidden files on android?
		return SReflect.isAndroid() ? file.listFiles() : SNonAndroid.getFiles(file, hiding);		
	}
	
	/**
	 *  Get the prefix length of a file.
	 */
	public static int getPrefixLength(File file)
	{
		int	ret	= 0;
		try
		{
			Method m = File.class.getDeclaredMethod("getPrefixLength", new Class[0]);
			m.setAccessible(true);
			ret	= ((Integer)m.invoke(file, new Object[0])).intValue();
		}
		catch(Exception e)
		{
			// Hack!!! assume unix as default
			String	path	= file.getPath();
			if(path.startsWith("~"))	// '~/' or '~user/'
			{
				ret	= path.indexOf('/');
			}
			else if(path.startsWith("/"))
			{
				ret	= 1;
			}
		}
		
		return ret;
	}

	/**
	 *  Check if a file represents a floppy.
	 *  Returns false on android.
	 */
	public static boolean isFloppyDrive(File file)
	{
		return SReflect.isAndroid() ? false : SNonAndroid.isFloppyDrive(file);
	}

	/**
	 *  Get the display name (e.g. of a system drive).
	 *  Returns null on android.
	 */
	public static String getDisplayName(File file)
	{
		return SReflect.isAndroid() ? null : SNonAndroid.getDisplayName(file);
	}

	/**
	 *  Test if a call is running on a gui (e.g. Swing or Android UI) thread.
	 *  Currently returns false on android.
	 */
	public static boolean isGuiThread()
	{
		// Todo: ask android helper for android UI thread.
		return SReflect.isAndroid() ? false : SNonAndroid.isGuiThread();
	}
	
	/**
	 *  Escape a java string.
	 *  @param str The string to escape.
	 *  @return The escaped string.
	 */
	public static String escapeString(String str) 
	{
		if(str == null)
			return null;
		
		StringWriter writer = new StringWriter(str.length() * 2);
		
		boolean essq = true;
		boolean esfs = false;
		
		int sz;
		sz = str.length();
		for(int i = 0; i < sz; i++)
		{
			char ch = str.charAt(i);

			if(ch > 0xfff)
			{
				writer.write("\\u" + hex(ch));
			}
			else if(ch > 0xff)
			{
				writer.write("\\u0" + hex(ch));
			}
			else if(ch > 0x7f)
			{
				writer.write("\\u00" + hex(ch));
			}
			else if(ch < 32)
			{
				switch(ch)
				{
					case '\b':
						writer.write('\\');
						writer.write('b');
						break;
					case '\n':
						writer.write('\\');
						writer.write('n');
						break;
					case '\t':
						writer.write('\\');
						writer.write('t');
						break;
					case '\f':
						writer.write('\\');
						writer.write('f');
						break;
					case '\r':
						writer.write('\\');
						writer.write('r');
						break;
					default:
						if(ch > 0xf)
						{
							writer.write("\\u00" + hex(ch));
						}
						else
						{
							writer.write("\\u000" + hex(ch));
						}
						break;
				}
			}
			else
			{
				switch(ch)
				{
					case '\'':
						if(essq)
						{
							writer.write('\\');
						}
						writer.write('\'');
						break;
					case '"':
						writer.write('\\');
						writer.write('"');
						break;
					case '\\':
						writer.write('\\');
						writer.write('\\');
						break;
					case '/':
						if(esfs)
						{
							writer.write('\\');
						}
						writer.write('/');
						break;
					default:
						writer.write(ch);
						break;
				}
			}
		}
		
		return writer.toString();
	}
	
	/**
	 *  Primitive encoding approach: Merges multiple byte arrays
	 *  into a single one so it can be split later.
	 * 
	 *  @param data The input data.
	 *  @return A merged byte array.
	 */
	public static byte[] mergeData(byte[]... data)
	{
		int datasize = 0;
		for (int i = 0; i < data.length; ++i)
			datasize += data[i].length;
		byte[] ret = new byte[datasize + (data.length << 2)];
		int offset = 0;
		for (int i = 0; i < data.length; ++i)
		{
			SUtil.intIntoBytes(data[i].length, ret, offset);
			offset += 4;
			System.arraycopy(data[i], 0, ret, offset, data[i].length);
			offset += data[i].length;
		}
		return ret;
	}
	
	/**
	 *  Primitive encoding approach: Splits a byte array
	 *  that was encoded with mergeData().
	 * 
	 *  @param data The input data.
	 *  @return A list of byte arrays representing the original set.
	 */
	public static List<byte[]> splitData(byte[] data)
	{
		return splitData(data, -1, -1);
	}
	
	/**
	 *  Primitive encoding approach: Splits a byte array
	 *  that was encoded with mergeData().
	 * 
	 *  @param data The input data.
	 *  @param offset Offset where the data is located.
	 *  @param length Length of the data, rest of data used if length < 0.
	 *  @return A list of byte arrays representing the original set.
	 */
	public static List<byte[]> splitData(byte[] data, int offset, int length)
	{
		List<byte[]> ret = new ArrayList<byte[]>();
		offset = offset < 0 ? 0 : offset;
		length = length < 0 ? data.length - offset : length;
		int endpos = offset + length;
		while (offset < endpos)
		{
			int datalen = SUtil.bytesToInt(data, offset);
			offset += 4;
			if (offset + datalen > endpos)
				throw new IllegalArgumentException("Invalid encoded data.");
			byte[] datapart = new byte[datalen];
			System.arraycopy(data, offset, datapart, 0, datalen);
			offset += datalen;
			ret.add(datapart);
		}
		return ret;
	}
	
	/**
	 *  Convert char to hex vavlue.
	 */
	public static String hex(char ch) 
	{
		return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
	}
	
	/**
	 *  Convert a byte array to a string representation.
	 */
	public static String hex(byte[] data)
	{
		return hex(data, null, 1, true);
	}
	
	/**
	 *  Convert a byte array to a string representation.
	 */
	public static String hex(byte[] data, boolean uppercase)
	{
		return hex(data, null, 1, uppercase);
	}
	
	/**
	 *  Convert a byte array to a string representation.
	 */
	public static String hex(byte[] data, String delim, int block)
	{
		return hex(data, delim, block, true);
	}
	
	/**
	 *  Convert a byte array to a string representation.
	 */
	public static String hex(byte[] data, String delim, int block, boolean uppercase)
	{
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<data.length; i++) 
		{
		    ret.append(String.format(uppercase? "%02X": "%02x", data[i]));
		    if(delim!=null && i+1<data.length && (i+1)%block==0)
		    	ret.append(delim);
		}
		return ret.toString();
	}
	
	/**
	 *  Encodes a set of data as a Base16 String (hex).
	 *  
	 * 	@param data The data.
	 * 	@return Base16-encoded String.
	 */
	public static String base16Encode(byte[] data)
	{
		StringBuilder ret = new StringBuilder();
		for (byte b : data)
		{
			int ib = b & 0xFF;
			if (ib < 16)
			{
				ret.append("0");
			}
			ret.append(Integer.toHexString(ib));
		}
		return ret.toString();
	}
	
	/**
	 *  Decodes a Base16-encoded String and returns the data.
	 *  
	 * 	@param data The encoded data.
	 *  @return The decoded data.
	 */
	public static byte[] base16Decode(String data)
	{
		byte[] ret = new byte[data.length() >> 1];
		for (int i = 0; i < ret.length; ++i)
		{
			int val = i * 2;
			String sub = data.substring(val, val + 2);
			val = Integer.valueOf(sub, 16);
			ret[i] = (byte) val;
		}
		return ret;
	}
	
	/**
	 *  Taken from ant.
	 *  Split a command line.
	 * 
	 *  @param line The command line to process.
	 *  @return The command line broken into strings. An empty or null toProcess
	 *    parameter results in a zero sized array.
	 */
	public static String[] splitCommandline(String line)
	{
		if(line == null || line.length() == 0)
		{
			// no command? no string
			return new String[0];
		}
		
		// parse with a simple finite state machine

		final int normal = 0;
		final int inquote = 1;
		final int indoublequote = 2;
		
		int state = normal;
		StringTokenizer tok = new StringTokenizer(line, "\"\' ", true);
		Vector v = new Vector();
		StringBuffer current = new StringBuffer();
		boolean lasttok = false;

		while(tok.hasMoreTokens())
		{
			String nextTok = tok.nextToken();
			switch(state)
			{
				case inquote:
					if("\'".equals(nextTok))
					{
						lasttok = true;
						state = normal;
					}
					else
					{
						current.append(nextTok);
					}
					break;
				case indoublequote:
					if("\"".equals(nextTok))
					{
						lasttok = true;
						state = normal;
					}
					else
					{
						current.append(nextTok);
					}
					break;
				default:
					if("\'".equals(nextTok))
					{
						state = inquote;
					}
					else if("\"".equals(nextTok))
					{
						state = indoublequote;
					}
					else if(" ".equals(nextTok))
					{
						if(lasttok || current.length() != 0)
						{
							v.addElement(current.toString());
							current = new StringBuffer();
						}
					}
					else
					{
						current.append(nextTok);
					}
					lasttok = false;
					break;
			}
		}
		if(lasttok || current.length() != 0)
		{
			v.addElement(current.toString());
		}
		if(state == inquote || state == indoublequote)
		{
			throw new RuntimeException("unbalanced quotes in " + line);
		}
		String[] args = new String[v.size()];
		v.copyInto(args);
		
		return args;
	}

	
	/**
	 *  Get the file from an URL.
	 *	@param url	The file URL.
	 *  @return	The file.
	 */
	public static File	getFile(URL url)
	{
		assert url.getProtocol().equals("file");
		
		File	file;
		try
		{
			file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
//			String	filename	= URLDecoder.decode(url.toString(), "UTF-8");
//			file = new File(filename.substring(5));	// strip "file:"
		}
		catch(UnsupportedEncodingException e)
		{
			// Shouldn't happen for existing files!?
			throw new RuntimeException(e);			
		}
		return file;
	}

	/**
	 *  Copy a file.
	 *  @param source	The source file.
	 *  @param target	The target file or directory (will be deleted first).
	 */
	public static void	copyFile(File source, File target)	throws IOException
	{
		if(target.isDirectory())
		{
			target	= new File(target, source.getName());
		}
		if(target.exists())
		{
			target.delete();
		}
		InputStream	is	= new FileInputStream(source);
		OutputStream	os	= new FileOutputStream(target);
		byte[]	buf	= new byte[8192];
		int	read;
		while((read=is.read(buf))!=-1)
		{
			os.write(buf, 0, read);
		}
		os.close();
		is.close();
	}
	
	/**
	 *  Moves a file to a target location.
	 *  @param source	The source file.
	 *  @param target	The target file location (will be deleted first, if it exists).
	 */
	public static void moveFile(File source, File target) throws IOException
	{
		IOException ex = null;
		
		int maxtries = 1;
		
		// Antivirus programs in Windows sometimes read and therefore block files
		// directly after writing, so we have to try a few times. 
		if (isWindows())
			maxtries = 10;
		
		for (int i = 0; i < maxtries; ++i)
		{
			try
			{
				internalMoveFile(source, target);
				i = maxtries;
				ex = null;
			}
			catch (IOException e)
			{
				ex = e;
				sleep(10);
			}
		}
		
		if (ex != null)
			throw ex;
	}
	
	/**
	 *  Moves a file to a target location.
	 *  @param source	The source file.
	 *  @param target	The target file location (will be deleted first, if it exists).
	 */
	protected static void internalMoveFile(File source, File target) throws IOException
	{
		Class<?> filesclazz = null;
		try
		{
			filesclazz = Class.forName("java.nio.file.Files");
		}
		catch (ClassNotFoundException e)
		{
		}
		
		if (filesclazz != null)
		{
			// Java 7+ mode
			try
			{
				Class<?> pathclazz = Class.forName("java.nio.file.Path");
				Class<?> copyoptionclazz = Class.forName("java.nio.file.CopyOption");
				Class<?> standardcopyoptionclazz = Class.forName("java.nio.file.StandardCopyOption");
				Object atomicflag = standardcopyoptionclazz.getField("ATOMIC_MOVE").get(null);
				Object replaceflag = standardcopyoptionclazz.getField("REPLACE_EXISTING").get(null);
				Object moveoptions = Array.newInstance(copyoptionclazz, 2);
				Array.set(moveoptions, 0, replaceflag);
				Array.set(moveoptions, 1, atomicflag);
				Class<?> copyoptionarrayclazz = moveoptions.getClass();
				Method movemethod = filesclazz.getMethod("move", pathclazz, pathclazz, copyoptionarrayclazz);
				
				Method topathmethod = File.class.getMethod("toPath", (Class<?>[]) null);
				Object srcpath = topathmethod.invoke(source, (Object[]) null);
				Object tgtpath = topathmethod.invoke(target, (Object[]) null);
				
				try
				{
					movemethod.invoke(null, srcpath, tgtpath, moveoptions);
				}
				catch (Exception e)
				{
					// Try non-atomic move.
					moveoptions = Array.newInstance(copyoptionclazz, 1);
					Array.set(moveoptions, 0, replaceflag);
					movemethod.invoke(null, srcpath, tgtpath, moveoptions);
				}
			}
			catch (Exception e)
			{
				// Still try fallback, maybe that works.
				filesclazz = null;
			}
		}
		
		if (filesclazz == null)
		{
			// Compatability fallback.
			if (!source.renameTo(target))
			{
				copyFile(source, target);
				if (!source.delete())
				{
					throw new IOException("Unable to delete source file after move: " + source.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	 *  Reads a file into memory (byte array).
	 *  Note: This only works for files smaller than 2GiB.
	 *  
	 * 	@param file The file.
	 * 	@return Contents of the file.
	 * 	@throws IOException Exception on IO errors.
	 */
	public static byte[] readFile(File file) throws IOException
	{
		byte[] ret = null;
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			ret = readStream(fis);
		}
		catch (Exception e)
		{
			close(fis);
			throwUnchecked(e);
		}
		close(fis);
		
		return ret;
	}
	
	/**
	 *  Reads an input stream into memory (byte array).
	 *  Note: This only works for files smaller than 2GiB.
	 *  
	 * 	@param is The InputStream.
	 * 	@return Contents of the file.
	 * 	@throws IOException Exception on IO errors.
	 */
	public static byte[] readStream(InputStream is) throws IOException
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		
		byte[] data = new byte[4096];

		int cnt;
		while((cnt=is.read(data, 0, data.length))!=-1) 
		{
			buf.write(data, 0, cnt);
		}

		buf.flush();

		return buf.toByteArray();
	}
	
	/**
	 *  Fills buffer from an input stream.
	 *  Note: This only works for sizes smaller than 2GiB.
	 *  
	 *  @param buf The buffer.
	 * 	@param is The InputStream.
	 */
	public static void readStream(byte[] buf, InputStream is)
	{
		readStream(buf, 0, buf.length, is, 0);
	}
	
	/**
	 *  Reads part of an input stream into a buffer.
	 *  Note: This only works for sizes smaller than 2GiB.
	 *  
	 *  @param buf The buffer.
	 *  @param off Offset for writing into the buffer.
	 *  @param len Number of bytes to read from stream, set to -1 to fill the rest of the buffer.
	 * 	@param is The InputStream.
	 */
	public static void readStream(byte[] buf, int off, int len, InputStream is)
	{
		readStream(buf, off, len, is, 0);
	}
	
	/**
	 *  Reads part of an input stream into a buffer.
	 *  Note: This only works for sizes smaller than 2GiB.
	 *  
	 *  @param buf The buffer.
	 *  @param off Offset for writing into the buffer.
	 *  @param len Number of bytes to read from stream, set to -1 to fill the rest of the buffer.
	 * 	@param is The InputStream.
	 *  @param skip Skip this number of bytes from the stream before reading, skip<=0 for no skip.
	 */
	public static void readStream(byte[] buf, int off, int len, InputStream is, long skip)
	{
		try
		{
			if (skip > 0)
				is.skip(skip);
			
			if (len < 0)
				len = buf.length - off;
			
			int read = 0;
			while (read < len)
			{
				read = is.read(buf, off, len - read);
				off += read;
			}
		}
		catch (Exception e)
		{
			rethrowAsUnchecked(e);
		}
	}
	
	/**
	 *  Attempt to close a Socket (e.g. on error recovery)
	 *  ignoring any error.
	 *  (compatibility for Java versions below 7 where Closeable exists
	 *   but is not implemented by Socket because stupid)
	 *  
	 *  @param socket The socket.
	 */
	public static void close(Socket socket)
	{
		if (socket != null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	/**
	 *  Attempt to close a Closeable (e.g. on error recovery)
	 *  ignoring any error.
	 *  
	 *  @param closeable The closeable.
	 */
	public static void close(Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	/**
	 *  Get the exception stack trace as string. 
	 *  @param e The exception.
	 *  @return The string.
	 */
	public static String getStackTrace(Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	/**
	 *  Fast way to compute log2(x).
	 *  @param num The number.
	 *  @return The log2(x).
	 */
	public static int log2(int num) 
	{
	    int ret = 0;
	    if((num & 0xffff0000)!= 0) 
	    { 
	    	num >>>= 16; 
			ret = 16; 
	    }
	    if(num >= 256) 
	    { 
	    	num >>>= 8; 
	    	ret += 8; 
	    }
	    if(num >= 16) 
	    { 
	    	num >>>= 4; 
	    	ret += 4; 
	    }
	    if(num >= 4) 
	    { 
	    	num >>>= 2; 
	    	ret += 2; 
	    }
	    return ret + (num >>> 1);
	}
	
	/**
	 *  Fast way to compute log2(x).
	 *  @param num The number.
	 *  @return The log2(x).
	 */
	public static int log2(long num) 
	{
	    int ret = 0;
	    
	    if((num & 0xffffffff00000000l)!= 0) 
	    { 
	    	num >>>= 64; 
			ret = 64; 
	    }
	    if(num >= 4294967296l) 
	    { 
	    	num >>>= 32; 
			ret = 32; 
	    }
	    if(num >= 65536) 
	    { 
	    	num >>>= 16; 
			ret = 16; 
	    }
	    if(num >= 256) 
	    { 
	    	num >>>= 8; 
	    	ret += 8; 
	    }
	    if(num >= 16) 
	    { 
	    	num >>>= 4; 
	    	ret += 4; 
	    }
	    if(num >= 4) 
	    { 
	    	num >>>= 2; 
	    	ret += 2; 
	    }
	    if(num >= 2)
	    {
	    	ret++;
	    }
	    return ret;
	}

	/**
	 *  Compute a file hash.
	 *  @param filename The filename.
	 *  @return The hash.
	 */
	public static byte[] computeFileHash(String filename)
	{
		return computeFileHash(filename, null);
	}
	
	/**
	 *  Compute a file hash.
	 *  @param filename The filename.
	 *  @param algorithm The hash algorithm.
	 *  @return The hash.
	 */
	public static byte[] computeFileHash(String filename, String algorithm)
	{
		byte[] ret = null;
		
		FileInputStream fis = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance(algorithm==null? "SHA-1": algorithm);
			File file = new File(filename);
			
			if(file.exists() && !file.isDirectory())
			{
				fis = new FileInputStream(file);
				int bufsize = 8192;
				byte[] data = new byte[bufsize];
				int read = 0;
				long len = file.length();
				while(read!=-1 && len>0)
				{
					int toread = (int)Math.min(len, bufsize);
					read = fis.read(data, 0, toread);
				    len -= read;
					md.update(data, 0, read);
				}
				ret = md.digest();
 			}
			else
			{
				String[] files = file.list(new FilenameFilter()
				{
					public boolean accept(File dir, String name)
					{
						return !".jadexbackup".equals(name);
					}
				});
				for(String name: files)
				{
					md.update((byte)0);	// marker between directory names to avoid {a, bc} being the same as {ab, c}. 
					md.update(name.getBytes("UTF-8"));
				}
				
				ret = md.digest();
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(fis!=null)
				{
					fis.close();
				}
			}
			catch(Exception e)
			{
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert a string to the same string with first
	 *  letter in upper case.
	 *  @param str The string.
	 *  @return The string with first letter in uppercase.
	 */
	public static String firstToUpperCase(String str)
	{
		return str.substring(0, 1).toUpperCase()+str.substring(1);
	}
	
	/** Cached for speed. */
	public static volatile String[] macs;

	/**
	 *  Get the mac address.
	 *  @return The mac address.
	 */
	public static String getMacAddress()
	{
		String[] adrs = getMacAddresses();
		String	ret	= null;
		for(int i=0; ret==null && i<adrs.length; i++)
		{
			// Use first real, i.e. non-tunnel, mac address
			if(!"[0, 0, 0, 0, 0, 0, 0, -32]".equals(adrs[i]))
			{
				ret	= adrs[i];
			}
		}
		return ret;
	}
	
	/**
	 *  Get the mac address.
	 *  @return The mac address.
	 */
	public static String[] getMacAddresses()
	{
		if(macs==null)
		{
			if(!SReflect.isAndroid() || androidUtils().getAndroidVersion() > 8)
			{
				macs	= SNonAndroid.getMacAddresses();
			} else {
				macs = new String[0];
			}
		}
		
		return macs;
	}
	
	/**
	 *  Create a regex from a normal bnf pattern.
	 */
	public static Pattern createRegexFromGlob(String glob)
	{
		 return Pattern.compile("^\\Q" 
			+glob.replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q")+"\\E$");
	}
		
	/**
	 *  Read a file to string.
	 *  @param filename The file name.
	 *  @return The string.
	 */
	public static String readFile(String filename)
	{
		return readFile(filename, null);
	}
	
	/**
	 *  Read a file to string.
	 *  @param filename The file name.
	 *  @return The string.
	 */
	public static String readFile(String filename, ClassLoader cl)
	{
		String ret = null;
		Scanner sc = null;
		try
		{
			InputStream is = SUtil.getResource0(filename, cl);
			if(is==null)
				throw new RuntimeException("Resource not found: "+filename);
			sc = new Scanner(is);
			ret = sc.useDelimiter("\\A").next();
		}
		finally
		{
			try
			{
				if(sc!=null)
					sc.close();
			}
			catch(Exception e)
			{
			}
		}
		return ret;
	}
	
	
	/**
	 *  Write a string to a file.
	 *  @param val The string to write.
	 *  @param filename The file name.
	 */
	public static void writeFile(String val, String filename)
	{
		writeFile(val, filename, null);
	}
	
	/**
	 *  Write a string to a file.
	 *  @param val The string to write.
	 *  @param filename The file name.
	 */
	public static void writeFile(String val, String filename, String charset)
	{
		PrintWriter out = null;
		try
		{
			if(charset!=null)
			{
				out = new PrintWriter(filename, charset);
			}
			else
			{
				out = new PrintWriter(filename);
			}
			out.print(val);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out!=null)
			{
				out.close();
			}
		}
	}
	
	/**
	 *  Gets the "application directory", normally the current working directory,
	 *  except in Android.
	 * 
	 *  @return Application directory.
	 */
	public static File getAppDir()
	{
		if (appdir == null)
		{
			synchronized(SUtil.class)
			{
				if (appdir == null)
				{
					if (SReflect.isAndroid())
					{
						try
						{
							Class<?> acmclass = Class.forName("jadex.android.AndroidContextManager");
							Method getinstance = acmclass.getMethod("getInstance");
							Object acm = getinstance.invoke(null);
							Method getandroidcontext = acmclass.getMethod("getAndroidContext");
							Object ac = null;
							for (int i = 0; ac == null && i < 5; ++i)
								ac = getandroidcontext.invoke(acm);
							if (ac != null)
							{
								Method getfilesdir = ac.getClass().getMethod("getFilesDir");
								File filesdir = null;
								for (int i = 0; filesdir == null && i < 5; ++i)
									filesdir = (File) getfilesdir.invoke(ac);
								if (filesdir != null)
									appdir = filesdir;
								else
									appdir = File.createTempFile("", "").getParentFile();
							}
							else
							{
								appdir = File.createTempFile("", "").getParentFile();
							}
						}
						catch (Exception e)
						{
							appdir = (new File(System.getProperty("java.io.tmpdir"))).getAbsoluteFile();
						}
					}
					else
					{
						appdir = (new File("")).getAbsoluteFile();
					}
				}
			}
		}
		return appdir;
	}
	
	/**
	 * Get the AndroidUtils, if available.
	 * @return AndroidUtils
	 */
	public static AndroidUtils androidUtils() 
	{
		if (SReflect.isAndroid() && androidutils == null)
		{
			synchronized (SUtil.class)
			{
				if(androidutils == null && SReflect.isAndroid())
				{
					Class<?> clazz = SReflect.classForName0("jadex.android.commons.AndroidUtilsImpl", SReflect.class.getClassLoader());
					try
					{
						androidutils = (AndroidUtils) clazz.newInstance();
					}
					catch(InstantiationException e)
					{
						e.printStackTrace();
					}
					catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		return androidutils;
	}
	
	public interface AndroidUtils {

		/**
		 * Get Android API version. Possible values:
		 * http://developer.android.com/reference/android/os/Build.VERSION_CODES.html
		 * 
		 * @return Android API version
		 */
		int getAndroidVersion();

		/**
		 * Traverse the Hierarchy of the given classloader and collect all
		 * DexPaths that are found as URLs.
		 * @param classloader
		 * @return URLs
		 */
		Collection<? extends URL> collectDexPathUrls(ClassLoader classloader);

		/**
		 * Checks whether the Platform has the necessary classes to provide XML
		 * encoding and decoding support.
		 * @return true, if platform supports xml
		 */
		boolean hasXmlSupport();

		/**
		 * Looks up the ClassLoader Hierarchy and tries to find a JadexDexClassLoader in it.
		 * @param cl
		 * @return {@link ClassLoader} or <code>null</code>, if none found.
		 */
		ClassLoader findJadexDexClassLoader(ClassLoader cl);

		/**
		 * Creates an URL object from a given Path to an android APK file
		 * @param apkPath
		 * @return {@link URL}
		 * @throws MalformedURLException
		 */
		URL urlFromApkPath(String apkPath) throws MalformedURLException;
		
		/**
		 * Retrieves the APK Path from a given URL, if its an Android APK URL.
		 * @param url
		 * @return {@link String}
		 */
		String apkPathFromUrl(URL url);

		/**
		 * Get all Classes in a dex file as Enumeration.
		 * @param dexFile the dex file
		 * @return Enumeration of full-qualified classnames
		 * @throws IOException
		 */
		Enumeration<String> getDexEntries(File dexFile) throws IOException;

		/**
		 * Check whether the current Thread is the android UI thread.
		 * @return true, if current thread is ui main thread.
		 */
		boolean runningOnUiThread();
	}
	
//	/**
//	 * Main method for testing.
//	 */
//	public static void main(String[] args)
//	{
////		List<URL> urls = getClasspathURLs(null);
//		Properties props = System.getProperties();
//		for(Object key: props.keySet())
//		{
//			System.out.println(key+" "+props.get(key));
//		}
//		
////		System.out.println(log2(8));
////		System.out.println(log2(800000000000L));
//		
//		
////		System.out.println("Here: " + createUniqueId("test", 3));
////		System.out.println(htmlwraps);
////		testIntByteConversion();
//	}

	/**
	 *  Load a binary file as base 64 string, e.g. for embedded html images.
	 */
	public static String	loadBinary(String file)
	{
		String	ret;
		
		InputStream is = getResource0(file, Thread.currentThread().getContextClassLoader());
		byte[]	buf	= new byte[8192];
		ByteArrayOutputStream	baos	= new ByteArrayOutputStream(buf.length);
		try
		{
			for(int len=is.read(buf); len!=-1; len=is.read(buf))
			{
				baos.write(buf, 0, len);
			}
			
			ret	= new String(Base64.encode(baos.toByteArray()), "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if(is!=null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
				}
			}
			if(baos!=null)
			{
				try
				{
					baos.close();
				}
				catch(IOException e)
				{
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove BOM from UTF text.
	 *  @param text The text.
	 *  @return The corrected string.
	 */
	public static String removeBOM(String text)
	{
		try
		{
			String ret = text;
//			byte[] bytes = text.getBytes("ISO-8859-1");
			byte[] bytes = text.getBytes("UTF-8");
			if(isUTF8(bytes)) 
			{
				 byte[] barray = new byte[bytes.length - 3];
			     System.arraycopy(bytes, 3, barray, 0, barray.length);
//			     ret = new String(barray, "ISO-8859-1");
			     ret = new String(barray, "UTF-8");
			}
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Test if starts with UTF-8 BOM.
	 *  @param bytes The text.
	 *  @return Bytes without BOM.
	 */
	public static boolean isUTF8(byte[] bytes) 
	{
		return (bytes[0] & 0xFF) == 0xEF && 
			(bytes[1] & 0xFF) == 0xBB && 
			(bytes[2] & 0xFF) == 0xBF;
    }
	
	/**
	 *  Returns a UTF8 byte array as string.
	 * 
	 *  @param bytes The bytes.
	 *  @return The string.
	 */
	public static String toUTF8(byte[] bytes)
	{
		return new String(bytes, UTF8);
	}
	
	/**
	 *  Get the exception stacktrace.
	 *  @param e The exception.
	 *  @return The exception stacktrace.
	 */
	public static String getExceptionStacktrace(Throwable e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	/**
	 *  Convert a throwable to an unchecked exception (i.e. error or runtime exception).
	 *  Also unpacks InvocationTargeteException and ErrorException.
	 *  @param t	The throwable.
	 *  @return Dummy return value as exception will be thrown inside. Use as <code>throw SUtil.throwUnchecked(t);</code> to avoid compilation errors.
	 */
	public static RuntimeException throwUnchecked(Throwable t)	throws Error,	RuntimeException
	{
		if(t instanceof InvocationTargetException)
		{
			throw throwUnchecked(((InvocationTargetException)t).getTargetException());
		}
		else if(t instanceof ErrorException)
		{
			throw throwUnchecked(((ErrorException)t).getError());
		}
		else if(t instanceof Error)
		{
			throw (Error)t;
		}
		else if(t instanceof RuntimeException)
		{
			throw (RuntimeException)t;
		}
		else
		{
			throw new RuntimeException(t);
		}
	}

	/**
	 *  Guess the mime type by the file name.
	 *  @param name The filename
	 *  @return The mime type.
	 */
	public static String guessContentTypeByFilename(String name)
	{
		if(name == null)
			return null;

		int li = name.lastIndexOf('.');
		if(li!=-1)
		{
			String ext = name.substring(li+1).toLowerCase();
			if(MIMETYPES==null)
			{
				synchronized(SUtil.class)
				{
					if(MIMETYPES==null)
					{
						MIMETYPES = new HashMap<String, String>();
						InputStream is = SUtil.class.getResourceAsStream("mimetypes.properties");
						try
						{
							Properties props = new Properties();
							props.load(is);
							for(Object key: props.keySet())
							{
								String val = props.getProperty((String)key);
								StringTokenizer st = new StringTokenizer(val, " ");
								while(st.hasMoreTokens())
								{
									MIMETYPES.put(st.nextToken(), (String)key);
								}
							}
						}
						catch(IOException e)
						{
							throw new RuntimeException(e);
						}
						finally
						{
							try
							{
								is.close();
							}
							catch(IOException e)
							{
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
			return MIMETYPES.get(ext);
		}
		return null;
	}
	
	/**
	 *  Test if a string contains a digit.
	 *  @param s The string.
	 *  @return True, if the string contains a digit.
	 */
	public static boolean containsDigit(String s)
	{  
	    boolean ret = false;

	    if(s != null && s.length()>0)
	    {
	        for(char c : s.toCharArray())
	        {
	            if(ret = Character.isDigit(c))
	            {
	                break;
	            }
	        }
	    }

	    return ret;
	}
	
	//-------- file/jar hash --------
	
	/** LRU for hashes. */
	protected static LRU<String, Tuple2<Long, String>>	HASHES	= loadHashCache();
	
	/** LRU for directory modification dates. */
	protected static LRU<String, Long>	LASTMODS	= new LRU<String, Long>(1000);
	
	// Hash code cannot be done here since commons no longer has access to binaryserializer.
	
	/**
	 *  Get the hash code for a file or directory.
	 */
	/*public static String	getHashCode(File f, boolean flat)
	{
//		long	start0	= System.nanoTime();
		
		try
		{
			String	path	= f.getAbsolutePath();	// Not canonical, because we want to ignore symlinks.
			Tuple2<Long, String>	entry	= HASHES.get(path);
			String	hash	=	entry!=null ? entry.getSecondEntity() : null; 
			
			if(f.exists() && (entry==null || entry.getFirstEntity().longValue()!=getLastModified(f)))
			{
//				long	start	= System.nanoTime();
				MessageDigest md = MessageDigest.getInstance("SHA-512");
				if(f.isDirectory())
				{
					hashDirectory(path, f, md);
				}
				else
				{
					if(!flat)
					{
						ZipFile	zf	= null;
						try
						{
							// Try zip file as directory.
							zf	= new ZipFile(f);
							List<ZipEntry>	entries	= new ArrayList<ZipEntry>();
							Enumeration<? extends ZipEntry>	en	= zf.entries();
							while(en.hasMoreElements())
							{
								ZipEntry	ze	= en.nextElement();
								if(!ze.isDirectory() && !ze.getName().startsWith("META-INF/"))
								{
									entries.add(ze);
								}
							}
							Collections.sort(entries, new Comparator<ZipEntry>()
							{
								public int compare(ZipEntry o1, ZipEntry o2)
								{
									return o1.getName().compareTo(o2.getName());
								}
							});
							for(ZipEntry ze: entries)
							{
	//							System.out.println("Zip entry: "+ze.getName());
								md.update(ze.getName().getBytes("UTF-8"));
								hashStream(zf.getInputStream(ze), md);
							}
						}
						catch(ZipException ze)
						{
							// Treat as flat file.
							hashStream(new FileInputStream(f), md);					
						}
						finally
						{
							if(zf!=null)
							{
								zf.close();
							}
						}
					}
					else
					{
						hashStream(new FileInputStream(f), md);
					}
				}
				hash	= new String(Base64.encode(md.digest()), "UTF-8");
				
//				long	end	= System.nanoTime();
//				System.out.println("Hashing of "+(f.isDirectory() ? path : f.getName())+" took "+((end-start)/100000)/10.0+" ms: "+hash);
				HASHES.put(path, new Tuple2<Long, String>(Long.valueOf(getLastModified(f)), hash));
				saveHashCache();
			}
			
//			long	end0	= System.nanoTime();
//			System.out.println("Hashing of "+(f.isDirectory() ? path : f.getName())+" took "+((end0-start0)/100000)/10.0+" ms: "+hash);
			return hash;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}*/
	
	/**
	 *  Load the stored hashes.
	 */
	protected static LRU<String, Tuple2<Long, String>>	loadHashCache()
	{
		LRU<String, Tuple2<Long, String>>	ret	= null;
		
		File	cache	= new File(JADEXDIR, "hash.cache");
//		if(cache.exists())
//		{
//			try
//			{
//				FileInputStream	fis	= new FileInputStream(cache);
//				ret	= (LRU<String, Tuple2<Long, String>>)SBinarySerializer.readObjectFromStream(fis, null, null, null, null, null);
//			}
//			catch(Exception e)
//			{
//			}
//		}

		return ret!=null ? ret : new LRU<String, Tuple2<Long,String>>(1000);
	}
	
	/**
	 *  Save the caclulated hashes.
	 */
	/*protected static void	saveHashCache()
	{
		File	cache	= new File(JADEXDIR, "hash.cache"); // TODO: will not work for android, needs writable dir!
		try
		{
			cache.getParentFile().mkdirs();
			FileOutputStream	fos	= new FileOutputStream(cache);
			SBinarySerializer.writeObjectToStream(fos, HASHES, null);
		}
		catch(Exception e)
		{
			System.err.println("Warning: could not store hash cache: "+e);
		}
	}*/
	
	/**
	 *  Recursively get the newest last modified of a file or directory tree.
	 */
	public static long	getLastModified(File f)
	{
		long ret;
		if(f.isDirectory())
		{
			try
			{
				String	path	= f.getCanonicalPath();
				Long	lastmod	= LASTMODS.get(path);
				if(lastmod!=null)
				{
					ret	= lastmod.longValue();
				}
				else
				{
					ret	= Integer.MIN_VALUE;
					for(File f2: f.listFiles())
					{
						ret	= Math.max(ret, getLastModified(f2, true));
					}
					LASTMODS.put(path, Long.valueOf(ret));
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			ret	= f.lastModified();
		}
		return ret;
	}
	
	/**
	 *  Recursively get the newest last modified of a file or directory tree.
	 */
	public static long	getLastModified(File f, boolean nocache)
	{
		long ret;
		if(f.isDirectory())
		{
			ret	= Integer.MIN_VALUE;
			for(File f2: f.listFiles())
			{
				ret	= Math.max(ret, getLastModified(f2));
			}
		}
		else
		{
			ret	= f.lastModified();
		}
		return ret;
	}
	
	/**
	 *  Get the hash code of a directory recursively.
	 */
	protected static void	hashDirectory(String root, File dir, MessageDigest md) throws Exception
	{
		File[]	files	= dir.listFiles();
		Arrays.sort(files, new Comparator<File>()
		{
			// Grr... files are sorted by default ignoring capitalization on windows but respecting capitalization on linux
			// -> have to implement our own portable comparator for matching hash values on all systems. 
			public int compare(File o1, File o2)
			{
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());	// Not canonical, because we want to ignore symlinks.
			}
		});
		
		for(File f: files)
		{
			if(f.isDirectory())
			{
				if(!(f.getName().equals("META-INF") && f.getParentFile().getAbsolutePath().equals(root)))
				{
					hashDirectory(root, f, md);
				}
			}
			else
			{
				String	fpath	= f.getAbsolutePath(); 	// Not canonical, because we want to ignore symlinks.
				assert fpath.startsWith(root);
				if(root.length()+1>fpath.length())
				{
					// Shouldn't happen (bugfix for 3.0.0), left here for testing -> remove when confirmed working with symlinks
					System.out.println("root: "+root+", fpath: "+fpath);
				}
				else
				{
					String	entry	= fpath.substring(root.length()+1).replace(File.separatorChar, '/');
					md.update(entry.getBytes("UTF-8"));
				}
//				System.out.println("Dir entry: "+entry);
				hashStream(new FileInputStream(f), md);
			}
		}
	}

	protected static void hashStream(InputStream is, MessageDigest md) throws Exception
	{
		DigestInputStream	dis	= new DigestInputStream(is, md);
		byte[]	buf	= new byte[8192];
		while(dis.read(buf)!=-1);
		dis.close();
	}
	
	/**
	 *  Write a directory as jar to an output stream. 
	 */
	public static void	writeDirectory(File dir, OutputStream out)
	{
		try
		{
			ZipOutputStream	zos	= new JarOutputStream(out);
			writeDirectory("", dir, zos, new byte[8192]);
			zos.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Write a directory as jar to an output stream. 
	 */
	protected static void	writeDirectory(String prefix, File dir, ZipOutputStream zos, byte[] buf)	throws Exception
	{
        File[]	files	= dir.listFiles();
        for(int i=0; i<files.length; i++)
        {
        	if(files[i].isDirectory())
        	{
        		writeDirectory(prefix+files[i].getName()+"/", files[i], zos, buf);
        	}
        	else
        	{
//        		System.out.println("write: "+prefix+files[i].getName());
	        	ZipEntry	ze	= new ZipEntry(prefix+files[i].getName());
	        	ze.setTime(files[i].lastModified());
	        	zos.putNextEntry(ze);
	
	        	FileInputStream	fis	= new FileInputStream(files[i]);
	        	int	read;
	        	while((read=fis.read(buf))>0)
	        	{
	        		zos.write(buf, 0, read);
	        	}
	        	zos.closeEntry();
	        	fis.close();
        	}
        }
	}
	
	/**
	 *  Helper method to allow iterating over possibly null lists.
	 */
	public static <T> List<T>	safeList(List<T> list)
	{
		if(list!=null)
		{
			return list;
		}
		else
		{
			return Collections.emptyList();
		}
	}
	
	/**
	 *  Helper method to allow iterating over possibly null collections.
	 */
	public static <T> Collection<T>	safeCollection(Collection<T> coll)
	{
		if(coll!=null)
		{
			return coll;
		}
		else
		{
			return Collections.emptyList();
		}
	}
	
	/**
	 *  Helper method to allow iterating over possibly null sets.
	 */
	public static <T> Set<T>	safeSet(Set<T> set)
	{
		if(set!=null)
		{
			return set;
		}
		else
		{
			return Collections.emptySet();
		}
	}
	
	/**
	 *  Helper method to allow iterating over possibly null maps.
	 */
	public static <K, E> Map<K, E>	safeMap(Map<K, E> map)
	{
		if(map!=null)
		{
			return map;
		}
		else
		{
			return Collections.emptyMap();
		}
	}
	
	/**
	 *  Helper to find first matching key (if any) for a value (identity check).
	 */
	public static <K, V> K	findKeyForValue(Map<K, V> map, V value)
	{
		K	key	= null;
		for(K test: map.keySet())
		{
			if(map.get(test)==value)
			{
				key	= test;
				break;
			}
		}
		return key;
	}
	
	/**
	 *  Converts a stack trace array to a printable string.
	 *  
	 *  @param topline The first line to be printed with info, if needed.
	 *  @param trace The stack trace.
	 *  @return Printable string.
	 */
	public static final String getStackTraceString(String topline, StackTraceElement[] trace)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(topline);
		sb.append("\n");
		for (StackTraceElement traceele : trace)
		{
			sb.append("\tat ");
			sb.append(traceele);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 *  Try to find the correct classpath root directories for current build tool chain.
	 *  Tries bin (e.g. eclipse), build/classes/main (gradle), target/classes (maven)
	 *  and uses the directory with the newest file.
	 *  @return an expression string of the fpr 'new String[]{...}'.
	 */
	public static String	getOutputDirsExpression(String projectroot, boolean includeTestClasses)
	{
		StringBuffer	ret	= new StringBuffer("new String[]{");
		for(File f: findOutputDirs(projectroot, includeTestClasses))
		{
			try
			{
				String	s	= f.toURI().toURL().toString();
				if(ret.length()>13)
				{
					ret.append(", ");
				}
				ret.append("\"");
				ret.append(s);
				ret.append("\"");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		ret.append("}");
		return  ret.toString();
	}
	
	/**
	 *  Try to find the correct classpath root directories for current build tool chain.
	 *  Tries bin (e.g. eclipse), build/classes/main (gradle), target/classes (maven)
	 *  and uses the directory with the newest file.
	 */
	public static File[]	findOutputDirs(String projectroot, boolean includeTestClasses)
	{
		File projectDir = findDirForProject(projectroot);
		
		List<List<File>>	candidates	= new ArrayList<List<File>>();
		
		// eclipse
		candidates.add(new ArrayList<File>(Arrays.asList(
			new File(projectDir, "bin"))));
		
		// gradle
		candidates.add(new ArrayList<File>(Arrays.asList(
			new File(new File(new File(new File(projectDir, "build"), "classes"),"java"),  "main"),
			new File(new File(new File(projectDir, "build"), "resources"),  "main"))));
		if (includeTestClasses) {
			candidates.add(new ArrayList<File>(Arrays.asList(
				new File(new File(new File(new File(projectDir, "build"), "classes"),"java"),  "test"),
				new File(new File(new File(projectDir, "build"), "resources"),  "test"))));
		}

		// maven
		candidates.add(new ArrayList<File>(Arrays.asList(
			new File(new File(projectDir, "target"), "classes"),
			new File(new File(projectDir, "target"), "resources"))));
		if (includeTestClasses) {
			candidates.add(new ArrayList<File>(Arrays.asList(
					new File(new File(projectDir, "target"), "test-classes"),
					new File(new File(projectDir, "target"), "test-resources"))));
		}
		
		// Choose newest list of files based on first entry
		List<File>	found	= null;
		long	retmod	= -1;
		for(List<File> cand: candidates)
		{
			if(cand.get(0).exists())
			{
				long	mod	= SUtil.getLastModified(cand.get(0));
				if(mod>retmod)
				{
					found	= cand;
					retmod	= mod;
				}
			}
		}
		
		// Remove non-existing files from chosen list
		if(found!=null)
		{
			for(Iterator<File> it=found.iterator(); it.hasNext(); )
			{
				if(!it.next().exists())
				{
					it.remove();
				}
			}
		}

		return found!=null ? found.toArray(new File[found.size()]) : new File[0];
	}

	/**
	 * Find dir for given project.
	 * This allows to run under different environments with different working paths
	 * (e.g. intellij by default uses WP jadex/ while gradle uses WP jadex/jadex-integration-test
	 * @param project
	 * @return File
	 */
	private static File findDirForProject(String project) {
		File result = new File(project);
		if(!result.exists()) 
		{
			result = new File("../" + project);
		}
		return result;
	}
	
	/**
	 *  Generate a diffuse string hash.
	 *  @param s The string.
	 *  @return The hash.
	 */
	public static final int diffuseStringHash(String s) 
	{ 
		long state0 = 0; 
		long state1 = 0; 
		char[] chararr = s.toCharArray(); 
		for(int i = 0; i < chararr.length; ++i) 
		{ 
			if ((i & 7) < 4) 
			{
				state0 ^= chararr[i] << ((i & 3) << 3); 
			} 
			else 
			{ 
				state1 ^= chararr[i] << (((i & 7) - 4) << 3); 
			} 
		}  
		
		for(int i = 0; i < 5; ++i) 
		{ 
			state0 = Long.rotateLeft(state0, 55) ^ state1 ^ (state1 << 14); 
			state1 = Long.rotateLeft(state1, 36); 
		}  
		long result = state0 + state1;  
		return (int) result; 
	}
	
	/**
	 *  Tests if the OS is Windows.
	 *  @return True, if Windows.
	 */
	public static final boolean isWindows()
	{
		String osname = System.getProperty("os.name");
		return osname != null && osname.startsWith("Windows");
	}
	
	/**
     * Lookup table used for unique strings.
     */
    protected static final char[] ID_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    										  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
    										  'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    										  'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
    										  'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
    										  'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
    										  'Y', 'Z', '?', '!' };
}
