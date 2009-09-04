package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.QName;
import jadex.commons.xml.SXML;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.reader.Reader;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Java specific reader that supports collection classes and arrays.
 */
public class JavaReader extends Reader
{
	//-------- attributes --------

	/** The reader. */
	protected static Reader reader;

	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public JavaReader(Set typeinfos)
	{
		super(new BeanObjectReaderHandler(joinTypeInfos(typeinfos)));
	}

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
	 *  Get the type infos.
	 */
	public static Set getTypeInfos()
	{
		Set typeinfos = new HashSet();
		try
		{
			// java.util.Map
			
			TypeInfo ti_map = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Map")}, Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entry"), null, 
					null, new MapEntryConverter(), null, "", null, null, Map.class.getMethod("put", new Class[]{Object.class, Object.class}), MapEntry.class.getMethod("getKey", new Class[0])))
			});
			typeinfos.add(ti_map);
			
			TypeInfo ti_mapentry = new TypeInfo(null, "entry", MapEntry.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("key"), "key", 
					null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
				new SubobjectInfo(new BeanAttributeInfo(new QName("value"), "value", 
					null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
			});
			typeinfos.add(ti_mapentry);
			
			// java.util.List
			
			TypeInfo ti_list = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "List")}, List.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS,
					null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})))
			});
			typeinfos.add(ti_list);
			
			// java.util.Set
			
			TypeInfo ti_set = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Set")}, Set.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS,
					null, null, null, null, null, null, HashSet.class.getMethod("add", new Class[]{Object.class})))
			});
			typeinfos.add(ti_set);
			
			// java.util.Color
			
			TypeInfo ti_color = new TypeInfo(null, new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.awt", "Color")}, null, null, 
				new BeanAttributeInfo(null, AttributeInfo.THIS, null, new ITypeConverter()
				{
					public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
					{
						return Color.decode((String)val);
					}
				}, null),
			null, null, null, null, false);
			typeinfos.add(ti_color);
			
			// java.util.Date
			
			// No special read info necessary.
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return typeinfos;
	}
	
	/**
	 *  Convert an xml to an object.
	 *  @param val The string value.
	 *  @return The decoded object.
	 */
	public static Object objectFromXML(String val, ClassLoader classloader)
	{
		if(reader==null)
			reader = new JavaReader(null);
		return Reader.objectFromXML(reader, val, classloader);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader)
	{
		if(reader==null)
			reader = new JavaReader(null);
		return Reader.objectFromByteArray(reader, val, classloader);
	}
}
