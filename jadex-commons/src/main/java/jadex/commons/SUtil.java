package jadex.commons;

import jadex.commons.collection.SCollection;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;

/**
 * This class provides several useful static util methods.
 */
public class SUtil
{
	/** Constant that indicates a conversion of all known characters. */
	public static final int CONVERT_ALL = 1;

	/** Constant that indicates a conversion of all known characters except &. */
	public static final int CONVERT_ALL_EXCEPT_AMP = 2;

	/** Constant that indicates a conversion of no characters. */
	public static final int CONVERT_NONE = 3;

	/** A Null value. */
	public static final String NULL = "NULL";

	/** Mapping from single characters to encoded version for displaying
		on xml-style interfaces. */
	protected static Map htmlwraps;

	/** Holds the single characters. */
	protected static String seps;
	

	/** An empty enumeration. */
	public static final Enumeration EMPTY_ENUMERATION = new Enumeration()
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
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	static
	{
		htmlwraps	= new Hashtable();
		htmlwraps.put("\"", "&quot;");
		htmlwraps.put("&", "&amp;"); // Hmm???
		htmlwraps.put("'", "&apos;");
		htmlwraps.put("<", "&lt;");
		htmlwraps.put(">", "&gt;");
		htmlwraps.put("�", "&auml;");
		htmlwraps.put("�", "&Auml;");
		htmlwraps.put("�", "&uuml;");
		htmlwraps.put("�", "&Uuml;");
		htmlwraps.put("�", "&ouml;");
		htmlwraps.put("�", "&Ouml;");

		htmlwraps.put("�", "&acute;");
		htmlwraps.put("�", "&agrave;");
		htmlwraps.put("�", "&aring;");
		htmlwraps.put("�", "&acirc;");
		htmlwraps.put("�", "&Acute;");
		htmlwraps.put("�", "&Agrave;");
		htmlwraps.put("�", "&Aring;");
		htmlwraps.put("�", "&Acirc;");

		htmlwraps.put("�", "&ecute;");
		htmlwraps.put("�", "&egrave;");
		htmlwraps.put("�", "&ecirc;");
		htmlwraps.put("�", "&Ecute;");
		htmlwraps.put("�", "&Egrave;");
		htmlwraps.put("�", "&Ecirc;");

		htmlwraps.put("�", "&icute;");
		htmlwraps.put("�", "&igrave;");
		htmlwraps.put("�", "&icirc;");
		htmlwraps.put("�", "&Icute;");
		htmlwraps.put("�", "&Igrave;");
		htmlwraps.put("�", "&Icirc;");

		htmlwraps.put("�", "&ocute;");
		htmlwraps.put("�", "&ograve;");
		htmlwraps.put("�", "&ocirc;");
		htmlwraps.put("�", "&otilde;");
		htmlwraps.put("�", "&Ocute;");
		htmlwraps.put("�", "&Ograve;");
		htmlwraps.put("�", "&Ocirc;");
		htmlwraps.put("�", "&Otilde;");

		htmlwraps.put("�", "&ucute;");
		htmlwraps.put("�", "&ugrave;");
		htmlwraps.put("�", "&ucirc;");
		htmlwraps.put("�", "&Ucute;");
		htmlwraps.put("�", "&Ugrave;");
		htmlwraps.put("�", "&Ucirc;");

		htmlwraps.put("�", "&cccedil;");
		htmlwraps.put("�", "&Ccedil;");

		seps	= "";
		Iterator it	=  htmlwraps.keySet().iterator();
		while(it.hasNext())
			seps	+= (String)it.next();
	}

	/**
	 *  Get a string array of properties that are separated by commas.
	 *  @param key The key.
	 *  @param props The properties.
	 *  @return The strings.
	 */
	public static String[]	getStringArray(String key, Properties props)
	{
		String[]	ret	= new String[0];
		String os	= props.getProperty(key);
		if(os!=null)
		{
			StringTokenizer stok	= new StringTokenizer(os, ",");
			ret	= new String[stok.countTokens()];
			for(int i=0; stok.hasMoreTokens(); i++)
			{
				ret[i]	= stok.nextToken().trim();
			}
		}
		return ret;
	}

	/**
	 *  Joins two arrays of the same type.
	 *  Creates a new array containing the values of the first array
	 *  followed by the values of the second.
	 *  @param a1	The first array.
	 *  @param a2	The second array.
	 *  @return The joined array.
	 */
	public static Object	joinArrays(Object a1, Object a2)
	{
		int	l1	= Array.getLength(a1);
		int	l2	= Array.getLength(a2);
		Object	res	= Array
			.newInstance(a1.getClass().getComponentType(), l1 + l2);
		System.arraycopy(a1, 0, res, 0, l1);
		System.arraycopy(a2, 0, res, l1, l2);
		return res;
	}

	/**
	 *  Joins any arrays of (possibly) different type.
	 *  todo: Does not support basic types yet.
	 *  Problem basic type array and object arrays cannot
	 *  be mapped (except they are mapped).
	 *  @param as The array of arrays to join..
	 *  @return The joined array.
	 */
	public static Object[]	joinArbitraryArrays(Object[] as)
	{
		int lsum = 0;
		for(int i=0; i<as.length; i++)
			lsum += Array.getLength(as[i]);
		Object[] ret = new Object[lsum];
		//Object	ret	= Array.newInstance(Object.class, lsum);

		int start = 0;
		for(int i=0; i<as.length; i++)
		{
			int length = Array.getLength(as[i]);
			System.arraycopy(as[i], 0, ret, start, length);
			start += length;
		}

		return ret;
	}

	/**
	 *  Cut two arrays.
	 *  @param a1	The first array.
	 *  @param a2	The second array.
	 *  @return The cutted array.
	 */
	public static Object	cutArrays(Object a1, Object a2)
	{
		List ar1	= arrayToList(a1);
		List ar2	= arrayToList(a2);
		List ret	= new ArrayList();
		Object tmp;

		for(int i=0; i<ar1.size(); i++)
		{
			tmp	= ar1.get(i);
			if(ar2.contains(tmp))
			{
				ret.add(tmp);
			}
		}
		return ret.toArray((Object[])Array.newInstance(
			a1.getClass().getComponentType(), ret.size()));
	}

	/**
	 *  First array minus second array.
	 *  @param a1	The first array.
	 *  @param a2	The second array.
	 *  @return The substracted array.
	 */
	public static Object	substractArrays(Object a1, Object a2)
	{
		List ar1	= arrayToList(a1);
		List ar2	= arrayToList(a2);
		Object tmp;

		for(int i=0; i<ar2.size(); i++)
		{
			tmp	= ar2.get(i);
			if(ar1.contains(tmp))
			{
				ar1.remove(tmp);
			}
		}
		return ar1.toArray((Object[])Array.newInstance(
			a1.getClass().getComponentType(), ar1.size()));
	}

	/**
	 *  Transform an array to a vector.
	 *  @param a	The array.
	 *  @return The vector for the array.
	 */
	public static List	arrayToList(Object a)
	{
		int	l	= Array.getLength(a);
		ArrayList ret	= SCollection.createArrayList();
		for(int i=0; i<l; i++)
		{
			ret.add(Array.get(a, i));
		}
		return ret;
	}

	/**
	 *  Transform an array to a vector.
	 *  @param a	The array.
	 *  @return The vector for the array.
	 */
	public static Set	arrayToSet(Object a)
	{
		int	l	= Array.getLength(a);
		Set ret	= SCollection.createHashSet();
		for(int i=0; i<l; i++)
		{
			ret.add(Array.get(a, i));
		}
		return ret;
	}
	
	/**
	 *  Join two sets.
	 *  @param a The first set.
	 *  @param b The second set.
	 *  @return A set with elements from a and b.
	 * /
	public static Set joinSets(Set a, Set b)
	{
		Set ret = new HashSet();
		ret.addAll(a);
		ret.addAll(b);
		return ret;
	}*/

	/**
	 *  Transform an iterator to a list.
	 */
	public static List iteratorToList(Iterator it)
	{
		List ret = new ArrayList();
		while(it.hasNext())
			ret.add(it.next());
		return ret;
	}

	/**
	 *  Transform an iterator to a list.
	 */
	public static List iteratorToList(Iterator it, List ret)
	{
		if(ret==null)
			ret = new ArrayList();
		while(it.hasNext())
			ret.add(it.next());
		return ret;
	}

	/**
	 *  Transform an iterator to an array.
	 */
	public static Object[] iteratorToArray(Iterator it, Class clazz)
	{
		List list = iteratorToList(it);
		return list.toArray((Object[])Array.newInstance(clazz, list.size()));
	}

	/**
	 *  Check if an element is contained in an array.
	 *  @param array	The array.
	 *  @param value	The value.
	 */
	public static boolean	arrayContains(Object array, Object value)
	{
		int	l	= Array.getLength(array);
		boolean	ret	= false;
		for(int i=0; !ret && i<l; i++)
		{
			ret	= equals(Array.get(array, i), value);
		}
		return ret;
	}
	
	/**
	 *  Get the dimension of an array.
	 *  @param array
	 *  @return The array dimension.
	 */
	public static int[] getArrayLengths(Object array) 
	{ 
		List lens = new ArrayList(); 
		Class cls = array.getClass(); 
		
		while(cls.isArray()) 
		{ 
			lens.add(new Integer(Array.getLength(array)));
			cls = cls.getComponentType(); 
		} 
		
		int[] ret = new int[lens.size()];
		for(int i=0; i<lens.size(); i++)
			ret[i] = ((Integer)lens.get(i)).intValue();
		
		return ret;
	} 

	/*
	 *  Test if two values are equal or both null.
	 *  @param val1	The first value.
	 *  @param val2	The second value.
	 *  @return	True when the values are equal.
	 */
	public static boolean	equals(Object val1, Object val2)
	{
		// Should try comparable first, for consistency???
		return val1==val2 || val1!=null && val1.equals(val2); 
	}
	
	/**
	 *  Test if two arrays are content equal or both null.
	 *  @param array1	The first array.
	 *  @param array2	The second array.
	 *  @return	True when the arrays are content equal.
	 */
	public static boolean	arrayEquals(Object array1, Object array2)
	{
		boolean ret = array1==null && array2==null;
		if(!ret && array1!=null && array2!=null)
		{
			int l1 = Array.getLength(array1);
			int l2 = Array.getLength(array2);
			if(l1==l2)
			{
				ret = true;
				for(int i=0; i<l1 && ret; i++)
				{
					if(!Array.get(array1, i).equals(Array.get(array2, i)))
						ret = false;
				}
			}
		}
		return ret;
	}

	/**
	 *  Calculate a hash code for an array.
	 */
	public static int arrayHashCode(Object a)
	{
		int	ret	= 1;

		for(int i=0; i<Array.getLength(a); i++)
		{
			Object	val	= Array.get(a, i);
			ret	= 31 * ret + (val!=null ? val.hashCode() : 0);
		}

		return ret;
	}

	/**
	 * Get a string representation for an array.
	 * @param array The array.
	 * @return formatted string.
	 */
	public static String	arrayToString(Object array)
	{
		String	str	= "";

		if(array != null && array.getClass().getComponentType()!=null)
		{
			// inside arrays.
			str	+= "[";
			for(int i=0; i<Array.getLength(array); i++)
			{
				str	+= arrayToString(Array.get(array,i));
				if(i<Array.getLength(array)-1)
				{
					str	+= ", ";
				}
			}
			str	+= "]";
		}
		else
		{
			// simple type
			str	+= ""+array;
		}
		return str;
	}

	/**
	 *  Get the singular of a word in plural.
	 *  Does NOT find all correct singular.
	 *  @param s The plural word.
	 *  @return The singular of this word.
	 */
	public static String getSingular(String s)
	{
		String sing	= s;
		if(s.endsWith("shes") || s.endsWith("ches")
			|| s.endsWith("xes") || s.endsWith("ses"))
		{
			sing = s.substring(0, s.length()-2);
		}
		else if(s.endsWith("ies"))
		{
			sing = s.substring(0, s.length()-3)+"y";
		}
		else if(s.endsWith("s"))
		{
			sing = s.substring(0, s.length()-1);
		}

		return sing;
	}

	/**
	 *  Get the plural of a word in singular.
	 *  Does NOT find all correct plurals.
	 *  @param s The word.
	 *  @return The plural of this word.
	 */
	public static String getPlural(String s)
	{
		String plu	= s;
		if(s.endsWith("y"))
		{
			plu = s.substring(0, s.length()-1)+"ies";
		}
		else if(s.endsWith("s"))
		{
			plu	= s+"es";
		}
		else
		{
			plu	= s+"s";
		}
		return plu;
	}

	/**
	 *  Compares two strings, ignoring case.
	 *  @param a	The first string.
	 *  @param b	The second string.
	 *  @return a<b => <0
	 */
	public static int compareTo(String a, String b)
	{
		return a.toUpperCase().toLowerCase().compareTo(b.toUpperCase().toLowerCase());
	}

	/**
	 *  Test if the date is in the range. Start 
	 *	or end may null and will so not be checked.
	 *  @param date The date.
	 *  @param start The start.
	 *  @param end	The end.
	 *  @return True, if date is in range.
	 */
	public static boolean isInRange(Date date, Date start, Date end)
	{
		boolean ret	= true;
		if(start != null && date.before(start))
		{
			ret	= false;
		}
		if(ret && end != null && date.after(end))
		{
			ret = false;
		}
		return ret;
	}

	/**
	 *  Remove file extension.
	 *  @param fn The filename..
	 *  @return filename without extension.
	 */
	public static String removeExtension(String fn)
	{
		int	index	= fn.lastIndexOf(".");
		if(index>-1)
		{
			fn	= fn.substring(0, index);
		}
		return fn;
	}

	/**
	 *  Wrap a text at a given line length.
	 *  Doesn't to word wrap, just inserts
	 *  linebreaks every nth character.
	 *  If the string already contains linebreaks,
	 *  these are handled properly (extra
	 *  linebreaks will only be inserted when needed).
	 *
	 *  @param text	The text to wrap.
	 */
	public static String	wrapText(String text)
	{
		return wrapText(text, 80);
	}

	/**
	 *  Wrap a text at a given line length.
	 *  Doesn't to word wrap, just inserts
	 *  linebreaks every nth character.
	 *  If the string already contains linebreaks,
	 *  these are handled properly (extra
	 *  linebreaks will only be inserted when needed).
	 *
	 *  @param text	The text to wrap.
	 *  @param wrap	The column width.
	 */
	public static String	wrapText(String text, int wrap)
	{
		StringBuffer	buf	= new StringBuffer(text);
		int	i	= 0;

		// Insert line breaks, while more than <wrap> characters to go.
		while(buf.length()>i+wrap)
		{
			// Find next line break.
			// int	next	= buf.indexOf("\n", i); // works for 1.4 only.
			int next = buf.substring(i).indexOf("\n");

			// Skip line break, when in sight.
			if(next!=-1 && next-i<=wrap)
			{
				i	= i+next+1;
			}

			// Otherwise, insert line break.
			else
			{
				buf.insert(i+wrap, '\n');
				i	= i+wrap+1;
			}
		}

		return buf.toString();
	}


	/** Constant for sorting up. */
	public static final int SORT_UP	= 0;

	/** Constant for sorting down. */
	public static final int SORT_DOWN	= 1;

	/**
	 *  Remove the least element form a collection.
	 */
	protected static int	getExtremeElementIndex(Vector source, int direction)
	{
		String ret	= (String)source.elementAt(0);
		int retidx	= 0;
		int size	= source.size();
		for(int i=0; i<size; i++)
		{
			String tmp	= (String)source.elementAt(i);
			int res	= tmp.compareTo(ret);
			if((res<0 && direction==SORT_UP) || (res>0 && direction==SORT_DOWN))
			{
				ret	= tmp;
				retidx	= i;
			}
		}
		return retidx;
	}

	/**
	 *  Convert an output to html/wml conform presentation.
	 *  @param input The input string.
	 *  @return The converted output string.
	 */
	public static String makeConform(String input)
	{
		return makeConform(input, CONVERT_ALL);
	}

	/**
	 *  Convert an output to html/wml conform presentation.
	 *  @param input The input string.
	 *  @param flag CONVERT_ALL, CONVERT_NONE, CONVERT_ALL_EXCEPT_AMP;
	 *  @return The converted output string.
	 */
	public static String makeConform(String input, int flag)
	{
		String res	= input;
		if(flag!=CONVERT_NONE)
		{
			StringTokenizer stok	= new StringTokenizer(input, seps, true);
			res	= "";
			while(stok.hasMoreTokens())
			{
				String tmp	= stok.nextToken();
				String rep	= null;
				if(!(tmp.equals("&") && flag==CONVERT_ALL_EXCEPT_AMP))
					rep	= (String)htmlwraps.get(tmp);
				if(rep!=null)
					res	+= rep;
				else
					res	+= tmp;
			}
		}
		return res;
	}

	/**
	 *  Strip tags (e.g. html) from a string, leaving only the text content.
	 */
	public static String	stripTags(String source)
	{
		int start, end;
		while((start=source.indexOf("<"))!=-1 && (end=source.indexOf(">"))>start)
		{
			if(end==source.length()-1)
			{
				source	= source.substring(0, start);
			}
			else
			{
				source	= source.substring(0, start) + source.substring(end+1);
			}
		}
		return source;
	}

	/**
	 *  Convert an output readable in english. Therefore remove
	 *  all &auml;s, &ouml;s, &uuml;s etc.
	 *  @param input The input string.
	 *  @return The converted output string.
	 */
	public static String makeEnglishConform(String input)
	{
		StringTokenizer stok	= new StringTokenizer(input, seps, true);
		String res	= "";
		while(stok.hasMoreTokens())
		{
			String tmp	= stok.nextToken();
			if(htmlwraps.get(tmp)==null)
				res	+= tmp;
		}
		return res;
	}

	/**
	 *  Convert a color to an html representation.
	 *  @param color	The color.
	 *  @return The html string representing the color.
	 */
	public static String	colorToHTML(Color color)
	{
		return "#" + Integer.toHexString(color.getRed())
			 + Integer.toHexString(color.getGreen())
			 + Integer.toHexString(color.getBlue());
	}

	/**
	 *  Convert a font to an html representation.
	 *  @param font	The font.
	 *  @return The html string representing the font.
	 */
	public static String	fontToHTML(Font font)
	{
		String	style;
		if(font.isBold())
		{
			style	= font.isItalic() ? "bolditalic " : "bold ";
		}
		else
		{
			style	= font.isItalic() ? "italic " : "";
		}
		return style + font.getSize()+" " + font.getName();
	}

	/**
	 *  Extract the values out of an sl message.
	 *  @param message	The sl message.
	 *  @return The extracted properties.
	 * /
	// obsolete ???
	public static Properties	parseSLToPropertiesFast(String message)
	{
		Properties	props	= new Properties();		

		int	index	= message.indexOf(':');
		while(index!=-1)
		{
			// Hack !!! Assume space separated slots.
			int	index2	= message.indexOf(' ', index);
			String	name	= message.substring(index+1, index2);
			index	= message.indexOf('"', index2);
			index2	= message.indexOf('"', index+1);
			String	value	= message.substring(index+1, index2);
			props.setProperty(name, value);
			index	= message.indexOf(':', index2);
		}
		return props;
	}
*/
	/**
	 *  Extract the value(s) out of an sl message.
	 *  @param message	The sl message.
	 *  @return The extracted value(s) as string, index map or array list.
	 *  @see #toSLString(Object)
	 * /
	public static Object	fromSLString(String message)
	{
		Object	ret;

		// Parse map.
		if(message.startsWith("(Map ") && message.endsWith(")"))
		{
			message	= message.substring(5, message.length()-1);
			ExpressionTokenizer	exto	= new ExpressionTokenizer(message, " \t\r\n",
				new String[]{"\"\"", "()"});
			Map	map	= new IndexMap().getAsMap();	// Hack???
			while(exto.hasMoreTokens())
			{
				// Check for ":" as start of slot name.
				String	slot	= exto.nextToken();
				if(!slot.startsWith(":") || !exto.hasMoreTokens())
					throw new RuntimeException("Invalid SL: "+message);
				slot	= slot.substring(1);
				//if(slot.equals("2"))
				//    System.out.println("Da1!");
				map.put(slot, fromSLString(exto.nextToken()));
			}
			ret	= map;
		}

		// Parse sequence to collection object.
		else if(message.startsWith("(sequence ") && message.endsWith(")"))
		{
			message	= message.substring(10, message.length()-1);
			ExpressionTokenizer	exto2	= new ExpressionTokenizer(message, " \t\r\n",
				new String[]{"\"\"", "()"});
			List	list	= new ArrayList();
			while(exto2.hasMoreTokens())
			{
				list.add(fromSLString(exto2.nextToken()));
			}
			ret	= list;
		}

		// Simple slot message.
		else
		{
			// Remove quotes from message.
			if(message.startsWith("\"") && message.endsWith("\""))
				message	= message.substring(1, message.length()-1);
			// Replace escaped quotes.
			message	= SUtil.replace(message, "\\\"", "\"");
			ret	= message;
		}

		return ret;
	}*/

	/**
	 *  Convert an object to an SL string.
	 *  When the value is of type java.util.Map
	 *  the key value pairs are extracted as slots.
	 *  Keys must be valid slot names.
	 *  Values of type java.util.Collection are stored as sequence.
	 *  @return A string representation in SL.
	 * /
	public static String	toSLString(Object o)
	{
		StringBuffer	sbuf	= new StringBuffer();
		toSLString(o, sbuf);
		return sbuf.toString();
	}*/

	/**
	 *  Convert an object to an SL string.
	 *  When the value is of type java.util.Map
	 *  the key value pairs are extracted as slots.
	 *  Keys must be valid slot names.
	 *  Values of type java.util.Collection are stored as sequence.
	 *  @param o The object to convert to SL.
	 *  @param sbuf The buffer to convert into.
	 * /
	public static void	toSLString(Object o, StringBuffer sbuf)
	{
		// Get mapo from encodable object.
		/*if(o instanceof IEncodable)
		{
			o	= ((IEncodable)o).getEncodableRepresentation();
		}* /

		// Write contents as slot value pairs.
		if(o instanceof Map)
		{
			Map	contents	= (Map)o;
			sbuf.append("(Map ");
			for(Iterator i=contents.keySet().iterator(); i.hasNext();)
			{
				Object	key	= i.next();
				Object	val	= contents.get(key);
				if(val!=null && !"null".equals(val))	// Hack ???
				{
					// Check if key is valid slot identifier.
					String	keyval	= key.toString();
					if(keyval.indexOf(' ')!=-1
						|| keyval.indexOf('\t')!=-1
						|| keyval.indexOf('\r')!=-1
						|| keyval.indexOf('\n')!=-1)
					{
						throw new RuntimeException("Encoding error: Invalid slot name "+keyval);
					}
					sbuf.append(" :");
					sbuf.append(keyval);
					sbuf.append(" ");
					toSLString(val, sbuf);
				}
			}
			sbuf.append(")");
		}

		// Write collection value as sequence.
		else if(o instanceof Collection)
		{
			Collection	coll	= (Collection)o;
			sbuf.append(" (sequence ");
			for(Iterator j=coll.iterator(); j.hasNext(); )
			{
				sbuf.append(" ");
				toSLString(j.next(), sbuf);
			}
			sbuf.append(")");
		}

		// Write normal slot value as string.
		else
		{
			sbuf.append("\"");
			// Escape quotes (directly writes to string buffer).
			SUtil.replace(""+o, sbuf, "\"", "\\\"");
			sbuf.append("\"");
		}
	}*/

	/**
	 *  Parse a source string replacing occurrences
	 *  and storing the result in the given string buffer.
	 *  This is a fast alternative to String.replaceAll(),
	 *  because it does not use regular expressions.
	 *  @param source	The source string.
	 *  @param dest	The destination string buffer.
	 *  @param old	The string to replace.
	 *  @param newstring	The string to use as replacement.
	 */
	public static void	replace(String source, StringBuffer dest, String old, String newstring)
	{
		int last	= 0;
		int index;
		while((index=source.indexOf(old, last))!=-1)
		{
			dest.append(source.substring(last, index));
			dest.append(newstring);
			last	= index+old.length();
		}
		dest.append(source.substring(last));
	}

	/**
	 *  Parse a source string replacing occurrences
	 *  and returning the result.
	 *  This is a fast alternative to String.replaceAll(),
	 *  because it does not use regular expressions.
	 *  @param source	The source string.
	 *  @param old	The string to replace.
	 *  @param newstring	The string to use as replacement.
	 */
	public static String	replace(String source, String old, String newstring)
	{
		StringBuffer	sbuf	= new StringBuffer();
		replace(source, sbuf, old, newstring);
		return sbuf.toString();
	}

	/**
	 *  Get an input stream for whatever provided.
	 *  1. It is tried to load the resource as file.
	 *  2. It is tried to load the resource via the ClassLoader.
	 *  3. It is tried to load the resource as URL.
	 *  @param name The resource description.
	 *  @return The input stream for the resource.
	 *  @throws IOException	when the resource was not found.
	 */
	public static InputStream getResource(String name, ClassLoader classloader) throws IOException
	{
		InputStream is = getResource0(name, classloader);
		if(is==null)
			throw new IOException("Could not load file: "+name);

		return is;
	}

	/**
	 *  Get an input stream for whatever provided.
	 *  1. It is tried to load the resource as file.
	 *  2. It is tried to load the resource via the ClassLoader.
	 *  3. It is tried to load the resource as URL.
	 *  @param name The resource description.
	 *  @return The input stream for the resource or null when the resource was not found.
	 */
	public synchronized static InputStream getResource0(String name, ClassLoader classloader)
	{
		InputStream is = null;
		File file;
		
		if(classloader==null)
			classloader = SUtil.class.getClassLoader();

		// File...
		// Hack!!! Might throw exception in applet / webstart.
		try
		{
			file	= new File(name);
			if(file.exists())
			{
				try
				{
					is	= new FileInputStream(file);
				}
				catch(FileNotFoundException e)
				{
					// File is directory, or maybe locked...
				}
			}
		}
		catch(SecurityException e){}

		// Classpath...
		if(is==null)
		{
//			is = getClassLoader().getResourceAsStream(name.startsWith("/") ? name.substring(1) : name);
			is = classloader.getResourceAsStream(name.startsWith("/") ? name.substring(1) : name);
		}

		// URL...
		if(is==null)
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
	 *  Get an input stream for whatever provided.
	 *  1. It is tried to load the resource as file.
	 *  2. It is tried to load the resource via the ClassLoader.
	 *  3. It is tried to load the resource as URL.
	 *  @param name The resource description.
	 *  @return The info object for the resource or null when the resource was not found.
	 */
	public synchronized static ResourceInfo getResourceInfo0(String name, ClassLoader classloader)
	{
		ResourceInfo ret = null;
		File	file;

		if(classloader==null)
			classloader = SUtil.class.getClassLoader();
			
		// File...
		// Hack!!! Might throw exception in applet / webstart.
		try
		{
			file	= new File(name);
			if(file.exists())
			{
				if(file.isDirectory())
				{
					ret = new ResourceInfo(file.getAbsolutePath(), null, file.lastModified());
				}
				else
				{
					try
					{
						ret	= new ResourceInfo(file.getAbsolutePath(), new FileInputStream(file), file.lastModified());
					}
					catch(FileNotFoundException e)
					{
						// File is directory, or maybe locked...
					}
				}
			}
		}
		catch(SecurityException e){}

		// Classpath...
		if(ret==null)
		{
			URL url = classloader.getResource(name.startsWith("/") ? name.substring(1) : name);
//			System.out.println("Classloader: "+classloader+" "+name+" "+url+" "+classloader.getParent());
//			if(classloader instanceof URLClassLoader)
//				System.out.println("URLs: "+SUtil.arrayToString(((URLClassLoader)classloader).getURLs()));
				
			if(url!=null)
			{
				// Local file from classpath.
				// Hack!!! Needed because of last-modified precision (argl).
				if(url.getProtocol().equals("file"))
				{
					// Find out default encoding (might fail in applets).
					String	encoding	= "ISO-8859-1";
					try
					{
						encoding	= System.getProperty("file.encoding");
					}
					catch(SecurityException e){}

					try
					{
						file	= new File(URLDecoder.decode(url.getFile(), encoding)); // does only work since 1.4.
//						file	= new File(URLDecoder.decode(url.getFile())); // problem decode is deprecated.
						if(file.exists())
						{
							if(file.isDirectory())
							{
								ret = new ResourceInfo(file.getAbsolutePath(), null, file.lastModified());
							}
							else
							{
								try
								{
									ret	= new ResourceInfo(file.getAbsolutePath(), new FileInputStream(file), file.lastModified());
								}
								catch(FileNotFoundException fnfe)
								{
									// File is directory, or maybe locked...
								}
							}
						}
					}
					catch(UnsupportedEncodingException e)
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						throw new RuntimeException(sw.toString());
					}
				}

				// Remote or jar file...
				else
				{
					try
					{
						url = classloader.getResource(name.startsWith("/") ? name.substring(1) : name);
						URLConnection con = url.openConnection();
						con.setDefaultUseCaches(false);	// See Java Bug ID 4386865
						long	modified = con.getLastModified();
						String	filename = url.getFile();
						if(con instanceof JarURLConnection)
						{
							JarURLConnection juc	= (JarURLConnection)con;
//							System.out.println("Jar file:     "+juc.getJarFile());
//							System.out.println("Jar file url: "+juc.getJarFileURL());
//							System.out.println("Jar entry:    "+juc.getJarEntry());
//							System.out.println("Entry name:   "+juc.getEntryName());

							// Add jar protocol to file (hack???).
							if(!filename.startsWith("jar:"))
								filename	= "jar:" + filename;

							// Set modified date to time of entry (if specified).
							if(juc.getJarEntry().getTime()!=-1)
								modified	= juc.getJarEntry().getTime();

							try
							{
								ret	= new ResourceInfo(filename, con.getInputStream(), modified);
							}
							catch(NullPointerException e)
							{
								// Workaround for Java bug #5093378 !?
								// Maybe this is only a race condition???
								String jarfilename	= juc.getJarFile().getName();
								ret	= new ResourceInfo(filename, new JarFile(jarfilename).getInputStream(juc.getJarEntry()), modified);
//								System.err.println("loaded with workaround: "+url);
							}
							
							// todo: what about jar directories?!
						}
						else
						{
							ret	= new ResourceInfo(filename, con.getInputStream(), modified);
						}
					}
					catch(IOException e)
					{
//						System.err.println("Error loading: "+url);
//						e.printStackTrace();
					}
				}
			}
		}

		// URL...
		if(ret==null)
		{
			try
			{
				URL url = new URL(name);
				URLConnection con = url.openConnection();
				ret	= new ResourceInfo(name, con.getInputStream(), con.getLastModified());
			}
			catch(IOException le)
			{
			}
		}

		return ret;
	}

	/**
	 *  Encode an object into XML.
	 *  @param ob The object.
	 *  @return The xml representation.
	 * /
	// Hack!!! has to be synchronized due to bug in JDK 1.4 java.beans.Statement
	public synchronized static String encodeXML(Object ob)
	{
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(bs);
		e.setExceptionListener(new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.out.println("XML encoding ERROR: ");
				e.printStackTrace();
			}
		});
		e.writeObject(ob);
		e.close();
		return bs.toString();
	}*/

	/**
	 *  Decode an object from XML.
	 *  @param xml_ob The xml representation.
	 *  @return The object.
	 * /
	// Hack!!! has to be synchronized due to bug in JDK 1.4 java.beans.Statement
	public synchronized static Object decodeXML(final String xml_ob)
	{
		ByteArrayInputStream bs = new ByteArrayInputStream(xml_ob.getBytes());
		XMLDecoder d = new XMLDecoder(bs, null, new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				System.out.println("XML decoding ERROR: "+xml_ob);
				e.printStackTrace();
			}
		});
		Object ob = d.readObject();
		d.close();
		return ob;
	}*/

	/**
	 *  Get a string representation for a duration.
	 *  @param ms The duration in ms.
	 *  @return The string representation.
	 */
	public static String getDurationHMS(long ms)
	{
		long h = ms/3600000;
		ms = ms-h*3600000;
		long m = ms/60000;
		ms = ms-m*60000;
		long s = ms/1000;
		ms = ms-s*1000;
		StringBuffer ret = new StringBuffer("");
		if(h>0)
		{
			ret.append(h);
			ret.append("h ");
		}
		if(m>0)
		{
			ret.append(m);
			ret.append("min ");
		}
		//if(s>0)
		//{
			ret.append(s);
			ret.append(",");
			ret.append(ms/100);
			ret.append("s ");
		/*}
		if(h==0 && m==0 && s==0)
		{
			ret.append(ms);
			ret.append("ms");
		}*/
		return ret.toString();
	}

	/**
	 *  Find a package name from a path. Searches the most specific classpath
	 *  and uses the rest of the pathname as package name.
	 *  @param path The directory.
	 *  @return The package.
	 */
	public static String convertPathToPackage(String path, ClassLoader classloader)
	{
		String ret = null;
		File	fpath	= new File(path);
		if(!fpath.isDirectory())
			path	= fpath.getParent();

		List cps = getClasspathURLs(classloader);
		
		java.util.List toks = SCollection.createArrayList();
		StringTokenizer stok = new StringTokenizer(path, File.separator);
		while(stok.hasMoreTokens())
			toks.add(stok.nextToken());

		int quality = 0;
		for(int i=0; i<cps.size(); i++)
		{
			String cp = ((URL)cps.get(i)).getFile();
			stok = new StringTokenizer(cp, "/!");	// Exclamation mark to support jar files.

			int cplen = stok.countTokens();
			if(cplen<=toks.size())
			{
				int j = 0;
				for(; stok.hasMoreTokens(); j++)
				{
					if(!stok.nextToken().equals(toks.get(j)))
						break;
				}

				if(j==cplen && cplen>quality)
				{
					ret = "";
					for(int k=j; k<toks.size(); k++)
					{
						if(k>j && k<toks.size())
							ret+=".";
						ret += ""+toks.get(k);
					}
					quality = cplen;
				}
			}
		}
		return ret;
	}

//	/**
//	 *  Get the classloader.
//	 *  Uses the context class loader, if available.
//	 */
//	public static ClassLoader	getClassLoader()
//	{
//		ClassLoader	ret	= Thread.currentThread().getContextClassLoader();
//		if(ret==null)
//		{
//			ret	= SReflect.class.getClassLoader();
//		}
//		return ret;
//	}
	
	/**
	 *  Get the current classpath as a list of URLs
	 */
	public static List getClasspathURLs(ClassLoader classloader)
	{
		if(classloader==null)
			classloader = SUtil.class.getClassLoader();
		
		List cps	= SCollection.createArrayList();
		StringTokenizer	stok	= new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
		while(stok.hasMoreTokens())
		{
			try
			{
				String	entry	= stok.nextToken();
				File	file	= new File(entry);
				if(file.isDirectory() && !entry.endsWith(System.getProperty("file.separator")))
				{
					// Normalize, that directories end with "/".
					entry	+= System.getProperty("file.separator");
				}
				cps.add(new URL("file:///"+entry));
			}
			catch(MalformedURLException e)
			{
				// Maybe invalid classpath entries --> just ignore.
				// Hack!!! Print warning?
				//e.printStackTrace();
			}
		}

		if(classloader instanceof URLClassLoader)
		{
			URL[]	urls	= ((URLClassLoader)classloader).getURLs();
			for(int i=0; i<urls.length; i++)
				cps.add(urls[i]);
		}
		return cps;
	}
	
	/**
	 *  Calculate the cartesian product of parameters.
	 *  Example: names = {"a", "b"}, values = {{"1", "2"}, {"3", "4"}}
	 *  		 result = {{"a"="1", "b"="3"}, {"a"="2", "b"="3"}, {"a"="1", "b"="4"}, {"a=2", b="4"}}
	 *  @param names The names.
	 *  @param values The values (must be some form of collection, i.e. array, list, iterator etc.)
	 */
	public static List calculateCartesianProduct(String[] names, Object[] values)
	{
		ArrayList ret = SCollection.createArrayList();
		if(names==null || values==null)
			return ret;
		if(names.length!=values.length)
			throw new IllegalArgumentException("Must have same length: "+names.length+" "+values.length);
		
		HashMap binding = SCollection.createHashMap();
		Iterator[] iters = new Iterator[values.length];
		
		for(int i=0; i<values.length; i++)
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
			binding = (HashMap)binding.clone();
			int i = 0;
			for(; i<values.length && !iters[i].hasNext(); i++)
			{
				// Overflow: Re-init iterator.
				iters[i] = SReflect.getIterator(values[i]);
				binding.put(names[i], iters[i].next());
			}
			if(i<iters.length)
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
	 *  Test if a file is a Java source file.
	 *  @param filename The filename.
	 *  @return True, if it is a Java source file.
	 */
	public static boolean isJavaSourceFilename(String filename)
	{
		return filename!=null && filename.toLowerCase().endsWith(".java");
	}
	
	/**
	 *  Create a hash map from keys and values.
	 *  @param keys The keys.
	 *  @param values The values.
	 *  @return The map.
	 */
	public static Map createHashMap(Object[] keys, Object[] values)
	{
		HashMap ret = new HashMap();
		for(int i=0; i<keys.length; i++)
		{
			ret.put(keys[i], values[i]);
		}
		return ret;
	}
	
	/**
	 *  Create a hash set from values.
	 *  @param values The values.
	 *  @return The map.
	 */
	public static Set createHashSet(Object[] values)
	{
		Set ret = new HashSet();
		for(int i=0; i<values.length; i++)
		{
			ret.add(values[i]);
		}
		return ret;
	}
	
	/**
	 *  Create an array list from values.
	 *  @param values The values.
	 *  @return The map.
	 */
	public static List createArrayList(Object[] values)
	{
		List ret = new ArrayList();
		for(int i=0; i<values.length; i++)
		{
			ret.add(values[i]);
		}
		return ret;
	}
	
	/** The counter for conversation ids. */
	protected static int	convidcnt;

	/**
	 *  Create a globally unique conversation id.
	 *  @return The conversation id.
	 */
	public static String createUniqueId(String name)
	{
		synchronized(SUtil.class)
		{
			//return "id_"+name+"_"+System.currentTimeMillis()+"_"+Math.random()+"_"+(++convidcnt);
			return "id_"+name+"_"+Math.random()+"_"+(++convidcnt);
		}
	}
	
	public static String	convertPathToRelative(String absolute)
	{
		// Build path as list of files (directories).
		File	basedir	= new File(System.getProperty("user.dir"));
		List	basedirs	= new ArrayList();
		while(basedir!=null)
		{
			basedirs.add(0, basedir);
			basedir	= basedir.getParentFile();
		}

		// Build path as list of files (directories).
		File	target	= new File(absolute);
		List	targets	= new ArrayList();
		while(target!=null)
		{
			targets.add(0, target);
			target	= target.getParentFile();
		}
		
		// Find common path prefix
		int index	= 0;
		while(index<basedirs.size() && index<targets.size() && basedirs.get(index).equals(targets.get(index)))
		{
			index++;
		}

		String	ret;
		if(index>0)
		{
			StringBuffer	buf	= new StringBuffer();
			for(int i=index; i<basedirs.size(); i++)
			{
				buf.append("..");
				buf.append(File.separatorChar);
			}
			for(int i=index; i<targets.size(); i++)
			{
				buf.append(((File)targets.get(i)).getName());
				if(i!=targets.size()-1)
					buf.append(File.separatorChar);
			}
			ret	= buf.toString();
		}
		else
		{
			ret	= absolute;
		}
		
		return ret;
	}
	
	/**
	 *  Main method for testing.
	 * /
	public static void main(String[] args)
	{
		String res1 = getRelativePath("c:/a/b/c", "c:/a/d");
		String res2 = getRelativePath("c:/a/b/c", "c:/a/b/c");
		//String res2 = getRelativePath("c:/a/b/c", "d:/a/d");
		String res3 = getRelativePath("c:/a/b/c", "c:/a/b/c/d/e");
	
		//String tst = "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww";
		//System.out.println(tst);
		//System.out.println(SUtil.wrapText(tst));

		/*String[] a = new String[]{"a1", "a2", "a3"};
		Integer[] b = new Integer[]{new Integer(1), new Integer(2), new Integer(3)};
		System.out.println(arrayToString(joinArbitraryArrays(new Object[]{a, b})));*/

		/*try
		{
			URL target = new URL("file:///C:/projects/jadex/lib/examples.jar");
			DynamicURLClassLoader loader = new DynamicURLClassLoader(new URL[0]);

			try
			{
				Class clazz = loader.loadClass("jadex.examples.ping.PingPlan");
				System.out.println("Loaded class: "+clazz);
			}
			catch(ClassNotFoundException e){System.out.println(e);}

			loader.addURL(target);

			try
			{
				Class clazz = loader.loadClass("jadex.examples.ping.PingPlan");
				System.out.println("Loaded class: "+clazz);
			}
			catch(ClassNotFoundException e){System.out.println(e);}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}*/

}


