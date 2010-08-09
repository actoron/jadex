package jadex.xml.bean;

import jadex.commons.SReflect;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.IStringObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.SubObjectConverter;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.reader.Reader;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

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
			IObjectObjectConverter entryconv = new IObjectObjectConverter()
			{
				public Object convertObject(Object val, IContext context)
				{
					return ((MapEntry)val).getValue();
				}
			};
			
			TypeInfo ti_map = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Map")}),
				new ObjectInfo(Map.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo("entry"), new AccessInfo("entry", null, null, null,  
					new BeanAccessInfo(Map.class.getMethod("put", new Class[]{Object.class, Object.class}), null, "", MapEntry.class.getMethod("getKey", new Class[0]))), 
				new SubObjectConverter(entryconv, null), true, null)
			}));
			typeinfos.add(ti_map);
			
			TypeInfo ti_mapentry = new TypeInfo(new XMLInfo("entry"), new ObjectInfo(MapEntry.class),
				new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("key")),
				new SubobjectInfo(new AccessInfo("value"))
			}));
			typeinfos.add(ti_mapentry);
			
			// java.util.List
			TypeInfo ti_list = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "List")}),
				new ObjectInfo(List.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null, 
				new BeanAccessInfo(List.class.getMethod("add", new Class[]{Object.class}), null)))
			}));
			typeinfos.add(ti_list);
			
			// java.util.Set
			TypeInfo ti_set = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Set")}),
				new ObjectInfo(Set.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null,
				new BeanAccessInfo(Set.class.getMethod("add", new Class[]{Object.class}), null)))
			}));
			typeinfos.add(ti_set);
			
			// java.util.Color
			IStringObjectConverter coconv = new IStringObjectConverter()
			{
				public Object convertString(String val, IContext context)
				{
					return Color.decode(val);
				}
			};
			
			TypeInfo ti_color = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.awt", "Color")}), 
				null, new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(coconv, null))));
			typeinfos.add(ti_color);
			
			TypeInfo ti_class = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Class")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return SReflect.findClass((String)rawattributes.get("classname"), null, context.getClassLoader());
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("classname", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_class);
			
			// java.util.Date
			// No special read info necessary.
			
			// java.lang.String
			TypeInfo ti_string = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "String")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return (String)rawattributes.get("content");
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_string);
			
			// java.lang.Boolean
			TypeInfo ti_boolean = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Boolean")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Boolean((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_boolean);
			
			// java.lang.Integer
			TypeInfo ti_integer = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Integer")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Integer((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_integer);
			
			// java.lang.Double
			TypeInfo ti_double = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Double")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Double((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_double);
			
			// java.lang.Float
			TypeInfo ti_float = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Float")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Float((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_float);
			
			// java.lang.Long
			TypeInfo ti_long = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Long")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Long((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_long);
			
			// java.lang.Short
			TypeInfo ti_short = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Short")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Short((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_short);
			
			// java.lang.Byte
			TypeInfo ti_byte = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Byte")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Byte((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_byte);
			
			// java.lang.Character
			TypeInfo ti_character = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Character")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new Character(((String)rawattributes.get("content")).charAt(0));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_character);
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
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	public static Reader getInstance()
	{
		if(reader==null)
			reader = new JavaReader(null);
		return reader;
	}
}
