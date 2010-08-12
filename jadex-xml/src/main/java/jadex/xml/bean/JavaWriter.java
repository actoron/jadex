package jadex.xml.bean;

import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectStringConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.writer.Writer;

import java.awt.Color;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java specific reader that supports collection classes and arrays.
 */
public class JavaWriter extends Writer
{
	//-------- attributes --------
	
	/** The static writer instance. */
	protected static Writer writer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public JavaWriter(Set typeinfos)
	{
		super(new BeanObjectWriterHandler(joinTypeInfos(typeinfos), true));
	}

	//-------- methods --------
	
	/**
	 *  Join sets of typeinfos.
	 *  @param typeinfos The user specific type infos. 
	 *  @return The joined type infos.
	 */
	public static Set joinTypeInfos(Set typeinfos)
	{
		Set ret = getTypeInfos();
		if(typeinfos!=null)
			ret.addAll(typeinfos);
		return ret;
	}
	
	/**
	 *  Get the java type infos.
	 */
	public static Set getTypeInfos()
	{
		Set typeinfos = new HashSet();
		
		try
		{
			// java.util.Map
			TypeInfo ti_map = new TypeInfo(null, new ObjectInfo(Map.class),
				new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null,
				new BeanAccessInfo(null, Map.class.getMethod("entrySet", new Class[0]))), null, true)
			}));
			typeinfos.add(ti_map);
			
			// Cannot let xmltag be null, because class name then contains $ which is not allowed in a tag
			// Note: XMLinfo is necessary because it cannot be written as 'Map$Entry'
			TypeInfo ti_mapentry = new TypeInfo(new XMLInfo("entry"),
				new ObjectInfo(Map.Entry.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("key", null, null, null,  
				new BeanAccessInfo(null, Map.Entry.class.getMethod("getKey", new Class[0])))),
				new SubobjectInfo(new AccessInfo("value", null, null, null, 
				new BeanAccessInfo(null, Map.Entry.class.getMethod("getValue", new Class[0]))))
			}));
			typeinfos.add(ti_mapentry);
			
			// java.util.List
			TypeInfo ti_list = new TypeInfo(null, new ObjectInfo(List.class), new MappingInfo(null,
				new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", AccessInfo.THIS), null, true)
			}));
			typeinfos.add(ti_list);
			
			// java.util.Set
			TypeInfo ti_set = new TypeInfo(null, new ObjectInfo(Set.class), new MappingInfo(null,
				new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", AccessInfo.THIS), null, true)
			}));
			typeinfos.add(ti_set);
			
			// Array
			TypeInfo ti_array = new TypeInfo(null, new ObjectInfo(Object[].class),
				new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", AccessInfo.THIS), null, true)
			}));
			typeinfos.add(ti_array);
			
			// java.util.Color
			IObjectStringConverter coconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					return ""+((Color)val).getRGB();
				}
			};
			TypeInfo ti_color = new TypeInfo(null, new ObjectInfo(Color.class), new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, coconv))));
			typeinfos.add(ti_color);
			
			// java.util.Date
			// Ignores several redundant bean attributes for performance reasons.
			TypeInfo ti_date = new TypeInfo(null, new ObjectInfo(Date.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("hours", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("minutes", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("seconds", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("month", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("year", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("date", null, AccessInfo.IGNORE_READWRITE))},
				null
			));
			typeinfos.add(ti_date);
			
			// java.lang.Class
			IObjectStringConverter clconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					return ""+((Class)val).getCanonicalName();
				}
			};
			TypeInfo ti_class = new TypeInfo(null, new ObjectInfo(Class.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("classname", AccessInfo.THIS), new AttributeConverter(null, clconv))},
				null
			));
			typeinfos.add(ti_class);
			
			// java.lang.String
			TypeInfo ti_string = new TypeInfo(null, new ObjectInfo(String.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_string);
			
			// java.lang.Boolean
			TypeInfo ti_boolean = new TypeInfo(null, new ObjectInfo(Boolean.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_boolean);
			
			// java.lang.Integer
			TypeInfo ti_integer = new TypeInfo(null, new ObjectInfo(Integer.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_integer);
			
			// java.lang.Double
			TypeInfo ti_double = new TypeInfo(null, new ObjectInfo(Double.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_double);
			
			// java.lang.Float
			TypeInfo ti_float = new TypeInfo(null, new ObjectInfo(Float.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_float);
			
			// java.lang.Long
			TypeInfo ti_long = new TypeInfo(null, new ObjectInfo(Long.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_long);
			
			// java.lang.Short
			TypeInfo ti_short = new TypeInfo(null, new ObjectInfo(Short.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_short);
			
			// java.lang.Byte
			TypeInfo ti_byte = new TypeInfo(null, new ObjectInfo(Byte.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_byte);
			
			// java.lang.Character
			TypeInfo ti_character = new TypeInfo(null, new ObjectInfo(Character.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_character);
			
			// java.net.URL
			TypeInfo ti_url = new TypeInfo(null, new ObjectInfo(URL.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("protocol", null)),
				new AttributeInfo(new AccessInfo("host", null)),
				new AttributeInfo(new AccessInfo("port", null)),
				new AttributeInfo(new AccessInfo("file", null))},
				null
			));
			typeinfos.add(ti_url);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return typeinfos;
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToXML(Object val, ClassLoader classloader)
	{
		if(writer==null)
			writer = new JavaWriter(null);
		return Writer.objectToXML(writer, val, classloader);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader)
	{
		if(writer==null)
			writer = new JavaWriter(null);
		return Writer.objectToByteArray(writer, val, classloader);
	}
}
