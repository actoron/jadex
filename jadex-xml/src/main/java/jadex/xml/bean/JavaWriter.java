package jadex.xml.bean;

import jadex.xml.AttributeInfo;
import jadex.xml.ITypeConverter;
import jadex.xml.SXML;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.writer.Writer;

import java.awt.Color;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

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
		super(new BeanObjectWriterHandler(true, joinTypeInfos(typeinfos)));
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
			
			TypeInfo ti_map = new TypeInfo(null, (String)null, Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), "entrySet", 
					null, null, null, null, null, null, Map.class.getMethod("entrySet", new Class[0])), null, null, true)
			});
			typeinfos.add(ti_map);
			
			// Cannot let xmltag be null, because class name then contains $ which is not allowed in a tag
			TypeInfo ti_mapentry = new TypeInfo(null, "entry", Map.Entry.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("key"), "key", 
					null, null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]))),
				new SubobjectInfo(new BeanAttributeInfo(new QName("value"), "value", 
					null, null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0])))
			});
			typeinfos.add(ti_mapentry);
			
			// java.util.List
			
			TypeInfo ti_list = new TypeInfo(null, (String)null, List.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_list);
			
			// java.util.Set
			
			TypeInfo ti_set = new TypeInfo(null, (String)null, Set.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_set);
			
			// Array
			
			TypeInfo ti_array = new TypeInfo(null, (String)null, Object[].class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_array);
			
			// java.util.Color
			
			TypeInfo ti_color = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.awt", "Color")}, Color.class, null, 
				new BeanAttributeInfo((String)null, AttributeInfo.THIS, null, null, new ITypeConverter()
				{
					public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
					{
						return ""+((Color)val).getRGB();
					}
				})
			);
			typeinfos.add(ti_color);
			
			// java.util.Date
			
			// Ignores several redundant bean attributes for performance reasons.
			
			TypeInfo ti_date = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Date")}, Date.class, null, null, 
				new AttributeInfo[]{
				new BeanAttributeInfo("hours", null, AttributeInfo.IGNORE_READWRITE),
				new BeanAttributeInfo("minutes", null, AttributeInfo.IGNORE_READWRITE),
				new BeanAttributeInfo("seconds", null, AttributeInfo.IGNORE_READWRITE),
				new BeanAttributeInfo("month", null, AttributeInfo.IGNORE_READWRITE),
				new BeanAttributeInfo("year", null, AttributeInfo.IGNORE_READWRITE),
				new BeanAttributeInfo("date", null, AttributeInfo.IGNORE_READWRITE)},
				null
			);
			typeinfos.add(ti_date);
			
			// java.lang.Class
			
			TypeInfo ti_class = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Class")}, Class.class, null, null, 
				new AttributeInfo[]{
				new BeanAttributeInfo("classname", AttributeInfo.THIS, null, null, new ITypeConverter()
				{
					public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
					{
						return ""+((Class)val).getCanonicalName();
					}
				})},
				null
			);
			typeinfos.add(ti_class);
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
