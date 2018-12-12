package jadex.xml.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeConverter;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.SubObjectConverter;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.reader.AReader;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.XMLReaderFactory;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.QName;
import jadex.xml.stax.XMLReporter;



/**
 *  Java specific reader that supports collection classes and arrays.
 */
public class JavaReader
{
	//-------- attributes --------
	
	/** The reader. */
	protected static volatile AReader reader;
	
	/** The path manager. */
	protected static volatile TypeInfoPathManager pathmanager;

	/** The type manager. */
	protected static volatile IObjectReaderHandler handler;

	/**
	 *  Join sets of typeinfos.
	 *  @param typeinfos The user specific type infos. 
	 *  @return The joined type infos.
	 */
	public static Set<TypeInfo> joinTypeInfos(Set<TypeInfo> typeinfos)
	{
		Set<TypeInfo> ret = getTypeInfos();
		if(typeinfos!=null)
			ret.addAll(typeinfos);
		return ret;
	}
	
	/**
	 *  Get the java type infos.
	 *  
	 *  Supported types:
	 *  
	 *  - java.util.Map
	 *  - jadex.commons.collection.MultiCollection
	 *  - java.util.List
	 *  - java.util.Set
	 *  - Array
	 *  - java.util.Color
	 *  - java.util.Date
	 *  - java.util.Calendar
	 *  - java.util.Currency
	 *  - java.lang.Class
	 *  - java.net.URL
	 *  - java.logging.Level
	 *  - java.logging.LogRecord
	 *  - java.net.InetAddress
	 *  - java.awt.image.RenderedImage
	 *  - java.lang.String
	 *  - java.lang.Boolean
	 *	- java.lang.Integer
	 *	- java.lang.Double
	 *	- java.lang.Float
	 *	- java.lang.Long
	 *	- java.lang.Short
	 *	- java.lang.Byte
	 *	- java.lang.Character
	 *	- java.lang.enum
	 *	- boolean/Boolean Array
	 *	- int/Integer Array
	 *	- double/Double Array
	 *	- float/Float array
	 *	- long/Long array
	 *	- short/Short Array
	 *	- byte/Byte Array
	 *	- java.lang.Character
	 *	- jadex.commons.Tuple
	 *	- jadex.commons.Tuple2
	 *	- jadex.commons.Tuple3
	 *  - java.util.UUID
	 *  - java.security.Certifcate
	 *  - java.lang.Throwable
	 */
	public static Set<TypeInfo> getTypeInfos()
	{
		Set<TypeInfo> typeinfos = new HashSet<TypeInfo>();
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
			
			TypeInfo ti_lru = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "LRU")}),
				new ObjectInfo(Map.class), new MappingInfo(null,
				new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("maxEntries", null))	
				},
				new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("cleaner")),
				new SubobjectInfo(new XMLInfo("entry"), new AccessInfo("entry", null, null, null,  
					new BeanAccessInfo(Map.class.getMethod("put", new Class[]{Object.class, Object.class}), null, "", MapEntry.class.getMethod("getKey", new Class[0]))), 
				new SubObjectConverter(entryconv, null), true, null)
			}));
			typeinfos.add(ti_lru);
			
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
					new BeanAccessInfo(MultiCollection.class.getMethod("put", new Class[]{Object.class, Collection.class}), null, "", MapEntry.class.getMethod("getKey", new Class[0]))), 
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
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
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
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
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
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Collections.EMPTY_MAP;
					}
				}
			));
			typeinfos.add(ti_emptymap);
			
			// java.util.UnmodifyableSet
			TypeInfo ti_unset = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-UnmodifiableSet")}),
				new ObjectInfo(HashSet.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null,
				new BeanAccessInfo(Set.class.getMethod("add", new Class[]{Object.class}), null)))
			}));
			typeinfos.add(ti_unset);
				
			// java.util.UnmodifyableList
			TypeInfo ti_unlist = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-UnmodifiableList")}),
				new ObjectInfo(ArrayList.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null, 
				new BeanAccessInfo(List.class.getMethod("add", new Class[]{Object.class}), null)))
			}));
			typeinfos.add(ti_unlist);
			
			// java.util.UnmodifyableMap
			TypeInfo ti_unmap = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Collections-UnmodifiableMap")}),
				new ObjectInfo(HashMap.class), new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo("entry"), new AccessInfo("entry", null, null, null,  
					new BeanAccessInfo(Map.class.getMethod("put", new Class[]{Object.class, Object.class}), null, "", MapEntry.class.getMethod("getKey", new Class[0]))), 
				new SubObjectConverter(entryconv, null), true, null)
			}));
			typeinfos.add(ti_unmap);
			
			TypeInfo ti_class = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Class")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return SReflect.classForName((String)rawattributes.get("classname"), context.getClassLoader());
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("classname", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_class);
			
			// No special read info necessary.
			TypeInfo ti_timestamp = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.sql", "Timestamp")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						long time = Long.parseLong((String)rawattributes.get("time"));
						return new Timestamp(time);
					}
				}), new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("time", null))}));
			typeinfos.add(ti_timestamp);	

			// java.util.Date
			// No special read info necessary.
			TypeInfo ti_date = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Date")}),
				new ObjectInfo(Date.class), new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("time", null))}));
			typeinfos.add(ti_date);	
			
			// java.util.Calendar
			TypeInfo ti_calendar = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Calendar")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						Class<?> cl = SReflect.classForName((String)rawattributes.get("classname"), context.getClassLoader());
						Calendar cal = (Calendar)cl.newInstance();
						cal.setTime(new Date(Long.parseLong((String)rawattributes.get("time"))));
						return cal;
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("time", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("classname", null, AccessInfo.IGNORE_READWRITE))
				}
			));
			typeinfos.add(ti_calendar);	

			// java.util.Currency
			TypeInfo ti_currency = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "Currency")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Currency.getInstance((String)rawattributes.get("currencyCode"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("currencyCode", null, AccessInfo.IGNORE_READWRITE))
				}
			));
			typeinfos.add(ti_currency);
			
			// java.text.SimpleDateFormat
			// Pattern managed with applyPattern(String) and String toPattern() grrrr.
			TypeInfo ti_simpledateformat = new TypeInfo(new XMLInfo(new QName(SXML.PROTOCOL_TYPEINFO+"java.text", "SimpleDateFormat")),
				new ObjectInfo(SimpleDateFormat.class), 
				new MappingInfo(null, null, null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("pattern", null, null, null,
					new BeanAccessInfo(SimpleDateFormat.class.getMethod("applyPattern", String.class),
						SimpleDateFormat.class.getMethod("toPattern"))))},
				null, true, null, null	// Include methods for DateFormat properties
			));
			typeinfos.add(ti_simpledateformat);

			TypeInfo ti_biginteger = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.math", "BigInteger")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						byte[] bytes = Base64.decode(((String)rawattributes.get("byteArray")).getBytes("UTF-8"));
						return new BigInteger(bytes);
					}
				}), new MappingInfo(null, 
					new AttributeInfo[]{
						new AttributeInfo(new AccessInfo("byteArray", null, AccessInfo.IGNORE_READWRITE)),
						new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READWRITE))
					}));

			typeinfos.add(ti_biginteger);
			
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
			
			// Bean creator works only when all info is in attribues (content is not available).
			// If content is used a attribute converter for content should be used that maps back to THIS
			
			// java.lang.Boolean
			TypeInfo ti_boolean = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Boolean")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Boolean.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_boolean);
			
			// java.lang.Integer
			TypeInfo ti_integer = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Integer")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Integer.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_integer);
			
			// java.lang.Double
			TypeInfo ti_double = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Double")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Double.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_double);
			
			// java.lang.Float
			TypeInfo ti_float = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Float")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Float.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_float);
			
			// java.lang.Long
			TypeInfo ti_long = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Long")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Long.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_long);
			
			// java.lang.Short
			TypeInfo ti_short = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Short")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Short.valueOf((String)rawattributes.get("content"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_short);
			
			// java.lang.Byte
			TypeInfo ti_byte = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Byte")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						byte[] bytes = Base64.decode(((String)rawattributes.get("content")).getBytes("UTF-8"));
						return Byte.valueOf(bytes[0]);
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_byte);
			
			// java.lang.Character
			TypeInfo ti_character = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Character")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return Character.valueOf(((String)rawattributes.get("content")).charAt(0));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_character);

			// java.net.URL
			TypeInfo ti_url = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.net", "URL")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return new URL((String)rawattributes.get("protocol"), (String)rawattributes.get("host"), 
							Integer.parseInt((String)rawattributes.get("port")), (String)rawattributes.get("file"));
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
			
			// java.net.URL
			TypeInfo ti_uri = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.net", "URI")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return new URI((String)rawattributes.get("uri"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]
				{
					new AttributeInfo(new AccessInfo("uri", null, AccessInfo.IGNORE_READWRITE)),
				}
			));
			typeinfos.add(ti_uri);
			
			// java.logging.Level
			TypeInfo ti_level = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util.logging", "Level")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
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
			
			// java.logging.LogRecord
			TypeInfo ti_record = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util.logging", "LogRecord")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						String name = (String)rawattributes.get("level");
						String msg = (String)rawattributes.get("message");
						Level level = Level.parse(name);
						LogRecord ret = new LogRecord(level, msg);
						return ret;
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("level", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("message", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_record);
			
			// java.net.InetAddress
			TypeInfo ti_inetaddr = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.net", "InetAddress")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						return InetAddress.getByName((String)rawattributes.get("hostAddress"));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("hostAddress", null, AccessInfo.IGNORE_READWRITE)),
				}
			));
			typeinfos.add(ti_inetaddr);
			
			TypeInfo ti_enum = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Enum")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						String tmp = (String)rawattributes.get("content");
						int idx = tmp.indexOf("=");
						String classname = tmp.substring(0, idx);
						String name = tmp.substring(idx+1);
						Class clazz = SReflect.classForName(classname, context.getClassLoader());
						return Enum.valueOf(clazz, name);
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("content", null, AccessInfo.IGNORE_READWRITE))}
			));
			typeinfos.add(ti_enum);
			
			// java.security.Certificate
			TypeInfo ti_cert = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.security.cert", "Certificate")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						String type = (String)rawattributes.get("type");
						if(type==null)
							type = "X.509";
						CertificateFactory cf = CertificateFactory.getInstance(type);
						return cf.generateCertificate(new ByteArrayInputStream(Base64.decode(((String)rawattributes.get("data")).getBytes())));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("data", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("type", null, AccessInfo.IGNORE_READWRITE)),
				}
			));
			typeinfos.add(ti_cert);
			
			// Shortcut notations for simple array types
			
			// boolean/Boolean Array
			IStringObjectConverter booleanconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					boolean[] ret = new boolean[val.length()];
					for(int i=0; i<val.length(); i++)
						ret[i] = val.charAt(i)=='1'? true: false;
					return ret;
				}
			};
			TypeInfo ti_booleanarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "boolean__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(booleanconv, null))));
			typeinfos.add(ti_booleanarray);
			
			IStringObjectConverter bbooleanconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					Boolean[] ret = new Boolean[val.length()];
					for(int i=0; i<val.length(); i++)
						ret[i] = val.charAt(i)=='1'? Boolean.TRUE: Boolean.FALSE;
					return ret;
				}
			};
			TypeInfo ti_bbooleanarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Boolean__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(bbooleanconv, null))));
			typeinfos.add(ti_bbooleanarray);
			
			// int/Integer Array
			IStringObjectConverter intconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					int[] ret = new int[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Integer.parseInt(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_intarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "int__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(intconv, null))));
			typeinfos.add(ti_intarray);
			
			IStringObjectConverter integerconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					Integer[] ret = new Integer[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Integer.valueOf(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_integerarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Integer__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(integerconv, null))));
			typeinfos.add(ti_integerarray);
			
			// double/Double array
			IStringObjectConverter doubleconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, "_");
					int len = Integer.parseInt(stok.nextToken());
					double[] ret = new double[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Double.parseDouble(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_doublearray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "double__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(doubleconv, null))));
			typeinfos.add(ti_doublearray);
			
			IStringObjectConverter bdoubleconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, "_");
					int len = Integer.parseInt(stok.nextToken());
					Double[] ret = new Double[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Double.valueOf(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_bdoublerarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Double__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(bdoubleconv, null))));
			typeinfos.add(ti_bdoublerarray);
			
			// java.lang.Float
			IStringObjectConverter floatconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, "_");
					int len = Integer.parseInt(stok.nextToken());
					float[] ret = new float[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Float.parseFloat(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_floatarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "float__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(floatconv, null))));
			typeinfos.add(ti_floatarray);
			
			IStringObjectConverter bfloatconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, "_");
					int len = Integer.parseInt(stok.nextToken());
					Float[] ret = new Float[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Float.valueOf(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_bfloatarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Float__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(bfloatconv, null))));
			typeinfos.add(ti_bfloatarray);
			
			// java.lang.Long
			IStringObjectConverter longconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					long[] ret = new long[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Long.parseLong(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_longarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "long__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(longconv, null))));
			typeinfos.add(ti_longarray);
			
			IStringObjectConverter blongconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					Long[] ret = new Long[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Long.valueOf(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_blongarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Long__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(blongconv, null))));
			typeinfos.add(ti_blongarray);
			
			// short/Short Array
			IStringObjectConverter shortconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					short[] ret = new short[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Short.parseShort(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_shortarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "short__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(shortconv, null))));
			typeinfos.add(ti_shortarray);
			
			IStringObjectConverter bshortconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					StringTokenizer stok = new StringTokenizer(val, ",");
					int len = Integer.parseInt(stok.nextToken());
					Short[] ret = new Short[len];
					
					for(int i=0; stok.hasMoreTokens(); i++)
						ret[i] = Short.valueOf(stok.nextToken());
					return ret;
				}
			};
			TypeInfo ti_bshortarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Short__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(bshortconv, null))));
			typeinfos.add(ti_bshortarray);
			
			// byte/Byte Array
			IStringObjectConverter byteconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					return Base64.decode(val.getBytes());
				}
			};
			TypeInfo ti_bytearray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "byte__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(byteconv, null))));
			typeinfos.add(ti_bytearray);
			
			IStringObjectConverter bbyteconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					byte[] bytes = val.getBytes();
					Byte[] bbytes = new Byte[bytes.length];
					for(int i=0; i<bytes.length; i++)
						bbytes[i] = Byte.valueOf(bytes[i]);
					return bbytes;
				}
			};
			TypeInfo ti_bbytearray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Byte__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(bbyteconv, null))));
			typeinfos.add(ti_bbytearray);
			
			// java.lang.Character
			IStringObjectConverter charconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					return val.toCharArray();
				}
			};
			TypeInfo ti_chararray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO, "char__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(charconv, null))));
			typeinfos.add(ti_chararray);
			
			IStringObjectConverter characterconv = new IStringObjectConverter()
			{
				public Object convertString(String val, Object context)
				{
					char[] chars = val.toCharArray();
					Character[] bchars = new Character[chars.length];
					for(int i=0; i<chars.length; i++)
						bchars[i] = Character.valueOf(chars[i]);
					return bchars;
				}
			};
			TypeInfo ti_characterarray = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Character__1")}), null,
				new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(characterconv, null))));
			typeinfos.add(ti_characterarray);
			
			TypeInfo ti_tuple	= new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.commons", "Tuple")}),
				new ObjectInfo(HashMap.class, new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					Map<String, Object>	map	= (Map<String, Object>) object;
					return new Tuple((Object[])map.get("entities"));
				}
				
				public int getPass()
				{
					return 0;
				}
			}), new MappingInfo(null, new SubobjectInfo[]
			{
				new SubobjectInfo(new AccessInfo("entities", "entities", null, null, new BeanAccessInfo(AccessInfo.THIS))),
			}));
			typeinfos.add(ti_tuple);
				
			TypeInfo ti_tuple2	= new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.commons", "Tuple2")}),
				new ObjectInfo(HashMap.class, new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					Map<String, Object>	map	= (Map<String, Object>) object;
					return new Tuple2<Object, Object>(map.get("firstEntity"), map.get("secondEntity"));
				}
				
				public int getPass()
				{
					return 0;
				}
			}), new MappingInfo(null, new SubobjectInfo[]
			{
				new SubobjectInfo(new AccessInfo("firstEntity", "firstEntity", null, null, new BeanAccessInfo(AccessInfo.THIS))),
				new SubobjectInfo(new AccessInfo("secondEntity", "secondEntity", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			}));
			typeinfos.add(ti_tuple2);
			
			TypeInfo ti_tuple3	= new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"jadex.commons", "Tuple3")}),
				new ObjectInfo(HashMap.class, new IPostProcessor()
			{
				public Object postProcess(IContext context, Object object)
				{
					Map<String, Object>	map	= (Map<String, Object>)object;
					return new Tuple3<Object, Object, Object>(map.get("firstEntity"), map.get("secondEntity"), map.get("thirdEntity"));
				}
				
				public int getPass()
				{
					return 0;
				}
			}), new MappingInfo(null, new SubobjectInfo[]
			{
				new SubobjectInfo(new AccessInfo("firstEntity", "firstEntity", null, null, new BeanAccessInfo(AccessInfo.THIS))),
				new SubobjectInfo(new AccessInfo("secondEntity", "secondEntity", null, null, new BeanAccessInfo(AccessInfo.THIS))),
				new SubobjectInfo(new AccessInfo("thirdEntity", "thirdEntity", null, null, new BeanAccessInfo(AccessInfo.THIS)))
			}));
			typeinfos.add(ti_tuple3);
			
			TypeInfo ti_uuid	= new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.util", "UUID")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						long msb = Long.parseLong((String)rawattributes.get("mostSignificantBits"));
						long lsb = Long.parseLong((String)rawattributes.get("leastSignificantBits"));
						return new UUID(msb, lsb);
					}
				}
			), new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("mostSignificantBits", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("leastSignificantBits", null, AccessInfo.IGNORE_READWRITE))
			}));
			typeinfos.add(ti_uuid);
			
			// java.lang.Throwable
			TypeInfo ti_th = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "Throwable")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						Object ret = null;
						String clname = (String)rawattributes.get("class");
						String msg = (String)rawattributes.get("message");
						Class<?> cl = SReflect.findClass(clname, null, context.getClassLoader());
						try
						{
							Constructor<?> con = cl.getConstructor(new Class<?>[]{String.class});
							ret = con.newInstance(new Object[]{msg});
						}
						catch(Exception e)
						{
							try
							{
								// Try find empty constructor
								Constructor<?> con = cl.getConstructor(new Class<?>[0]);
								ret = con.newInstance(new Object[0]);
							}
							catch(Exception e2)
							{
								// Try private constructor for error exception (hack???)
								Constructor<?> con = cl.getDeclaredConstructor(new Class<?>[0]);
								con.setAccessible(true);
								ret = con.newInstance(new Object[0]);
							}
						}
						return ret;
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("class", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("message", null, AccessInfo.IGNORE_READWRITE))},
				new SubobjectInfo[]{
					new SubobjectInfo(new AccessInfo("cause", null, null, null, 
						new BeanAccessInfo(Exception.class.getMethod("initCause", new Class[]{Throwable.class}), null)))	
				}
			));
			typeinfos.add(ti_th);
			TypeInfo ti_ste = new TypeInfo(new XMLInfo(new QName[]{new QName(SXML.PROTOCOL_TYPEINFO+"java.lang", "StackTraceElement")}),
				new ObjectInfo(new IBeanObjectCreator()
				{
					public Object createObject(IContext context, Map<String, String> rawattributes) throws Exception
					{
						String decl = (String)rawattributes.get("className");
						String mname = (String)rawattributes.get("methodName");
						String fname = (String)rawattributes.get("fileName");
						String num = (String)rawattributes.get("lineNumber");
						return new StackTraceElement(decl, mname, fname, Integer.parseInt(num));
					}
				}),
				new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("className", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("methodName", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("fileName", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("lineNumber", null, AccessInfo.IGNORE_READWRITE))
				})
			);
			typeinfos.add(ti_ste);
			
			if(!SReflect.isAndroid()) 
			{
				typeinfos.addAll(STypeInfosAWT.getReaderTypeInfos());
			}
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
	public static <T> T objectFromXML(String val, ClassLoader classloader)
	{
		return JavaReader.<T>objectFromXML(val, classloader, null, null);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep)
	{
		return objectFromByteArray(val, classloader, null, null, rep);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The input stream.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromInputStream(InputStream val, ClassLoader classloader, IErrorReporter rep)
	{
		return objectFromInputStream(val, classloader, null, null, rep);
	}
	
	/**
	 *  Convert an xml to an object.
	 *  @param val The string value.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromXML(String val, ClassLoader classloader, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		return (T)AReader.objectFromXML(getInstance(), val, classloader,
			manager==null? getPathManager(): manager, handler==null? getObjectHandler(): handler);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader, 
		TypeInfoPathManager manager, IObjectReaderHandler handler, final IErrorReporter rep)
	{
		return rep!=null
			? AReader.objectFromByteArray(getReader(new XMLReporter()
			{
				public void report(String message, String errorType,
					Object relatedInformation, ILocation location) throws Exception
				{
					rep.exceptionOccurred(new RuntimeException(message));
				}
			}), val, classloader, 
				 manager==null? getPathManager(): manager, handler==null? getObjectHandler(): handler)
			: AReader.objectFromByteArray(getInstance(), val, classloader, 
				manager==null? getPathManager(): manager, handler==null? getObjectHandler(): handler);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The input stream.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromInputStream(InputStream val, ClassLoader classloader,
		TypeInfoPathManager manager, IObjectReaderHandler handler, final IErrorReporter rep)
	{
		return rep!=null
			? AReader.objectFromInputStream(getReader(new XMLReporter()
			{
				public void report(String message, String errorType,
					Object relatedInformation, ILocation location) throws Exception
				{
					rep.exceptionOccurred(new RuntimeException(message));
				}
			}), val, classloader, 
				 manager==null? getPathManager(): manager, handler==null? getObjectHandler(): handler)
			: AReader.objectFromInputStream(getInstance(), val, classloader, 
				manager==null? getPathManager(): manager, handler==null? getObjectHandler(): handler);
	}
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	private static AReader getInstance()
	{
		if(reader==null)
		{
			synchronized(JavaReader.class)
			{
				if(reader==null)
				{
					reader	= XMLReaderFactory.getInstance().createReader(false, false, false, null);
				}
			}
		}
		return reader;
	}
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	private static AReader	getReader(XMLReporter reporter)
	{
		return XMLReaderFactory.getInstance().createReader(false, false, false, reporter);
	}
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	public static TypeInfoPathManager getPathManager()
	{
		if(pathmanager==null)
		{
			synchronized(JavaReader.class)
			{
				if(pathmanager==null)
				{
					pathmanager = new TypeInfoPathManager(getTypeInfos());
				}
			}
		}
		return pathmanager;
	}
	
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	public static IObjectReaderHandler getObjectHandler()
	{
		if(handler==null)
		{
			synchronized(JavaReader.class)
			{
				if(handler==null)
				{
					handler = new BeanObjectReaderHandler(getTypeInfos());
				}
			}
		}
		return handler;
	}
}
