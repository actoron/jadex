package jadex.xml.bean;

import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.IPostProcessor;
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
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.imageio.ImageIO;
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
			
			// jadex.commons.collection.MultiCollection
			TypeInfo ti_mc = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.commons.collection", "MultiCollection")}),
				new ObjectInfo(MultiCollection.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo("entry"), new AccessInfo("entry", null, null, null,  
					new BeanAccessInfo(MultiCollection.class.getMethod("putCollection", new Class[]{Object.class, Collection.class}), null, "", MapEntry.class.getMethod("getKey", new Class[0]))), 
				new SubObjectConverter(entryconv, null), true, null)
			}));
			typeinfos.add(ti_mc);
			
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
			
			// java.util.EmptySet
			TypeInfo ti_emptyset = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-EmptySet")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return Collections.EMPTY_SET;
					}
				}
			));
			typeinfos.add(ti_emptyset);
			
			// java.util.EmptyList
			TypeInfo ti_emptylist = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-EmptyList")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return Collections.EMPTY_LIST;
					}
				}
			));
			typeinfos.add(ti_emptylist);
			
			// java.util.EmptyMap
			TypeInfo ti_emptymap = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-EmptyMap")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return Collections.EMPTY_MAP;
					}
				}
			));
			typeinfos.add(ti_emptymap);
			
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
				new ObjectInfo(null, new IPostProcessor()
				{
					public Object postProcess(IContext context, Object object)
					{
//						System.err.println("postprocess: "+object);
						return object!=null ? object : "";
					}
					
					public int getPass()
					{
						return 0;
					}
				})
//				new ObjectInfo(new IBeanObjectCreator()
//				{
//					public Object createObject(IContext context, Map rawattributes) throws Exception
//					{
//						return "";//(String)rawattributes.get("content");
//					}
//				}),
//				new MappingInfo(null, null, new AttributeInfo(new AccessInfo(AccessInfo.THIS)))
//				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
//			));
			);
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
			
			// java.net.URL
			TypeInfo ti_url = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.net", "URL")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return new URL((String)rawattributes.get("protocol"), (String)rawattributes.get("host"), 
							new Integer((String)rawattributes.get("port")).intValue(), (String)rawattributes.get("file"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("protocol", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("host", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("port", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("file", null, AccessInfo.IGNORE_READWRITE)),
				}
			));
			typeinfos.add(ti_url);
			
			// java.logging.Level
			TypeInfo ti_level = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util.logging", "Level")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						String name = (String)rawattributes.get("name");
						Level ret = Level.parse(name);
						return ret;
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("name", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_level);
			
			// java.net.InetAddress
			TypeInfo ti_inetaddr = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.net", "InetAddress")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						return InetAddress.getByName((String)rawattributes.get("hostAddress"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("hostAddress", null, AccessInfo.IGNORE_READWRITE)),
				}
			));
			typeinfos.add(ti_inetaddr);
			
			TypeInfo ti_image = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.awt.image", "Image")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map rawattributes) throws Exception
					{
						Image ret = null;
						String encdata = (String)rawattributes.get("imgdata");
						byte[] data = Base64.decode(encdata.getBytes());
						
						String classname = (String)rawattributes.get("classname");
						if(classname.indexOf("Toolkit")!=-1)
						{
							Toolkit t = Toolkit.getDefaultToolkit();
							ret = t.createImage(data);
						}
						else
						{
							ret = ImageIO.read(new ByteArrayInputStream(data));
						}
						return ret;
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("imgdata", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("classname", null, AccessInfo.IGNORE_READWRITE))
				}
			));
			typeinfos.add(ti_image);
		}
		catch(NoSuchMethodException e)
		{
			// Shouldn't happen
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
			createReader();
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
			createReader();
		return Reader.objectFromByteArray(reader, val, classloader);
	}
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	public static Reader getInstance()
	{
		if(reader==null)
			createReader();
		return reader;
	}
	
	/**
	 *  Conditionally create the reader instance.
	 *  Note that the synchronized check needs to be done.
	 *  Otherwise double creation may happen (leading to
	 *  concurrency issues).
	 */
	protected static synchronized void createReader()
	{
		if(reader==null)
			reader = new JavaReader(null);
	}
}
