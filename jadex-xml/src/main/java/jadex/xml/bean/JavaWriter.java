package jadex.xml.bean;


import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.collection.MultiCollection;
/* $if !android $ */
import jadex.commons.gui.SGUI;
/* $endif $ */
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

/* $if !android $ */
import java.awt.Color;
import java.awt.Image;
/* $endif $ */
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/* $if !android $ */
import javax.xml.namespace.QName;
/* $else $
import javaxx.xml.namespace.QName;
$endif $ */

/**
 * Java specific reader that supports collection classes and arrays.
 */
public class JavaWriter extends Writer
{
	//-------- attributes --------
	
	/** The static writer instance. */
	protected static Writer writer;
	
	/* $if !android $ */
	protected static final Color TRANSPARENT_WHITE = new Color( 255, 255, 255, 0); 
	/* $endif $ */
	
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
	 *  - java.util.UUID
	 */
	public static Set<TypeInfo> getTypeInfos()
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
			
			// jadex.commons.collection.MultiCollection
			TypeInfo ti_mc = new TypeInfo(null, new ObjectInfo(MultiCollection.class),
				new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("entries", null, null, null,
				new BeanAccessInfo(null, Map.class.getMethod("entrySet", new Class[0]))), null, true)
			}));
			typeinfos.add(ti_mc);
			
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
			
			// java.util.UnmodifyableSet
			// java.util.UnmodifyableList
//			TypeInfo ti_unlist = new TypeInfo(null, new ObjectInfo(Collections.UnmodifiableList.class), new MappingInfo(null,
//				new SubobjectInfo[]{
//				new SubobjectInfo(new AccessInfo("entries", AccessInfo.THIS), null, true)
//			}));
//			typeinfos.add(ti_unlist);
			// java.util.UnmodifyableMap
			
			// java.util.Color
			IObjectStringConverter coconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					/* $if !android $ */
					return ""+((Color)val).getRGB();
					/* $else $
					return "";
					$endif $ */
				}
			};
			/* $if !android $ */
			TypeInfo ti_color = new TypeInfo(null, new ObjectInfo(Color.class), new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, coconv))));
			/* $else $
			TypeInfo ti_color = new TypeInfo(null, new ObjectInfo(null), new MappingInfo(null, null,
					new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, coconv))));
			$endif $ */
			typeinfos.add(ti_color);
			
			// java.util.Date
			// Ignores several redundant bean attributes for performance reasons.
			TypeInfo ti_date = new TypeInfo(null, new ObjectInfo(Date.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("time", null)),
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
//					String	ret	= ""+((Class)val).getCanonicalName();
					// Todo: SReflect doesn not work for some case!? (Lars)
					String	ret	= SReflect.getClassName((Class)val);
					return ret;
				}
			};
			TypeInfo ti_class = new TypeInfo(null, new ObjectInfo(Class.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("classname", AccessInfo.THIS), new AttributeConverter(null, clconv))},
				null
			));
			typeinfos.add(ti_class);
			
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
			
			// java.logging.Level
			TypeInfo ti_level = new TypeInfo(null, new ObjectInfo(Level.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("name", null))},
//				new AttributeInfo(new AccessInfo("value", null, null, null, 
//					new BeanAccessInfo(null, Level.class.getMethod("intValue", new Class[0])))),
//				new AttributeInfo(new AccessInfo("resourceBundleName", null))},
				null
			));
			typeinfos.add(ti_level);
			
			// java.logging.LogRecord
			TypeInfo ti_record = new TypeInfo(null, new ObjectInfo(LogRecord.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("level", null), new AttributeConverter(null, new IObjectStringConverter()
				{
					public String convertObject(Object val, IContext context)
					{
						return ((Level)val).getName();
					}
				})),
				new AttributeInfo(new AccessInfo("level", null)),
				new AttributeInfo(new AccessInfo("message", null))},
				null
			));
			typeinfos.add(ti_record);
			
			// java.net.InetAddress
			// The following hack ensures that all subclasses of InetAdress will be stored using the same tag
			// todo: make this more easily possible
			TypeInfo ti_inetaddr = new TypeInfo(new XMLInfo(new QName("typeinfo:java.net", "InetAddress")), new ObjectInfo(InetAddress.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("hostAddress", null))},
				null
			));
			typeinfos.add(ti_inetaddr);
			
			// java.awt.image.RenderedImage
			IObjectStringConverter imgconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					try
					{
						/* $if !android $ */
						byte[] buf = SGUI.imageToStandardBytes((Image)val, "image/png");
						return new String(Base64.encode(buf));	
						/* $else $
						return "";
						$endif $ */
					}
					catch(Exception e)
					{
						// todo: use context report
						throw new RuntimeException(e);
					}
				}
			};
			
			// java.lang.Class
			IObjectStringConverter oclconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					String	ret	= SReflect.getClassName(val.getClass());
					return ret;
				}
			};
			
			/* $if !android $ */
			TypeInfo ti_image = new TypeInfo(new XMLInfo(new QName("typeinfo:java.awt.image", "Image")), 
				new ObjectInfo(Image.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("imgdata", AccessInfo.THIS), new AttributeConverter(null, imgconv)),
				new AttributeInfo(new AccessInfo("data", null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("classname", AccessInfo.THIS), new AttributeConverter(null, oclconv))},
				null
			));
			/* $else $
			TypeInfo ti_image = new TypeInfo(new XMLInfo(new QName("typeinfo:java.awt.image", "Image")), 
					new ObjectInfo(null), new MappingInfo(null, new AttributeInfo[]{
					new AttributeInfo(new AccessInfo("imgdata", AccessInfo.THIS), new AttributeConverter(null, imgconv)),
					new AttributeInfo(new AccessInfo("data", null, AccessInfo.IGNORE_READWRITE)),
					new AttributeInfo(new AccessInfo("classname", null, null, null, new BeanAccessInfo(null, Object.class.getMethod("getClass", new Class[0]))))},
					null
				));
			$endif $ */
			typeinfos.add(ti_image);
			
			// java.lang.String
//			TypeInfo ti_string = new TypeInfo(null, new ObjectInfo(String.class), new MappingInfo(null, new AttributeInfo[]{
//				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			TypeInfo ti_string = new TypeInfo(null, new ObjectInfo(String.class), new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS))));
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
			IObjectStringConverter byteconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					return new String(Base64.encode(new byte[]{((Byte)val).byteValue()}));
				}
			};
			TypeInfo ti_byte = new TypeInfo(null, new ObjectInfo(Byte.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS), new AttributeConverter(null, byteconv))}));
			typeinfos.add(ti_byte);
			
			// java.lang.Character
			TypeInfo ti_character = new TypeInfo(null, new ObjectInfo(Character.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS))}));
			typeinfos.add(ti_character);
			
			// java.lang.enum
			IObjectStringConverter enumconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Enum en = (Enum)val;
					String clazz = SReflect.getClassName(val.getClass());
					String name = en.name();
					return clazz+"="+name;
				}
			};
			TypeInfo ti_enum = new TypeInfo(new XMLInfo(new QName("typeinfo:java.lang", "Enum")), new ObjectInfo(Enum.class), new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("content", AccessInfo.THIS), new AttributeConverter(null, enumconv))}));
			typeinfos.add(ti_enum);
			
			// Shortcut notations for simple array types
			
			// boolean/Boolean Array
			IObjectStringConverter booleanconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					boolean[] data = (boolean[])val;
					StringBuilder bul = new StringBuilder();
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]? 1: 0);
					}
					return bul.toString();
				}
			};
			TypeInfo ti_booleanarray = new TypeInfo(null, new ObjectInfo(boolean[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, booleanconv))));
			typeinfos.add(ti_booleanarray);
			
			IObjectStringConverter bbooleanconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Boolean[] data = (Boolean[])val;
					StringBuilder bul = new StringBuilder();
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i].booleanValue()? 1: 0);
					}
					return bul.toString();
				}
			};
			TypeInfo ti_bbooleanarray = new TypeInfo(null, new ObjectInfo(Boolean[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bbooleanconv))));
			typeinfos.add(ti_bbooleanarray);
			
			// int/Integer Array
			IObjectStringConverter intconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					int[] data = (int[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_intarray = new TypeInfo(null, new ObjectInfo(int[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, intconv))));
			typeinfos.add(ti_intarray);
			
			IObjectStringConverter integerconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Integer[] data = (Integer[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_integerarray = new TypeInfo(null, new ObjectInfo(Integer[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, integerconv))));
			typeinfos.add(ti_integerarray);
			
			// double/Double Array
			IObjectStringConverter doubleconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					double[] data = (double[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append("_");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append("_");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_doublearray = new TypeInfo(null, new ObjectInfo(double[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, doubleconv))));
			typeinfos.add(ti_doublearray);
			
			IObjectStringConverter bdoubleconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Double[] data = (Double[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append("_");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append("_");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_bdoublearray = new TypeInfo(null, new ObjectInfo(Double[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bdoubleconv))));
			typeinfos.add(ti_bdoublearray);
			
			// float/Float array
			IObjectStringConverter floatconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					float[] data = (float[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append("_");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append("_");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_floatarray = new TypeInfo(null, new ObjectInfo(float[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, floatconv))));
			typeinfos.add(ti_floatarray);
			
			IObjectStringConverter bfloatconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Float[] data = (Float[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append("_");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append("_");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_bfloatarray = new TypeInfo(null, new ObjectInfo(Float[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bfloatconv))));
			typeinfos.add(ti_bfloatarray);
			
			// java.lang.Long
			IObjectStringConverter longconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					long[] data = (long[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_longarray = new TypeInfo(null, new ObjectInfo(long[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, longconv))));
			typeinfos.add(ti_longarray);
			
			IObjectStringConverter blongconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Long[] data = (Long[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_blongarray = new TypeInfo(null, new ObjectInfo(Long[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, blongconv))));
			typeinfos.add(ti_blongarray);
			
			// short/Short Array
			IObjectStringConverter shortconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					short[] data = (short[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_shortarray = new TypeInfo(null, new ObjectInfo(short[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, shortconv))));
			typeinfos.add(ti_shortarray);
			
			IObjectStringConverter bshortconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Short[] data = (Short[])val;
					StringBuilder bul = new StringBuilder();
					bul.append(data.length).append(",");
					for(int i=0; i<data.length; i++)
					{
						bul.append(data[i]);
						if(i+1<data.length)
							bul.append(",");
					}
					return bul.toString();
				}
			};
			TypeInfo ti_bshortarray = new TypeInfo(null, new ObjectInfo(Short[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bshortconv))));
			typeinfos.add(ti_bshortarray);
			
			// byte/Byte Array
			IObjectStringConverter bytesconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					byte[] bytes = Base64.encode((byte[])val);
//					byte[] bytes = (byte[])val;
					return new String(bytes);
				}
			};
			TypeInfo ti_bytearray = new TypeInfo(null, new ObjectInfo(byte[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bytesconv))));
			typeinfos.add(ti_bytearray);
			
			IObjectStringConverter bbyteconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Byte[] bbytes = (Byte[])val;
					byte[] bytes = new byte[bbytes.length];
					for(int i=0; i<bbytes.length; i++)
						bytes[i] = bbytes[i].byteValue();
					return new String(bytes);
				}
			};
			TypeInfo ti_bbytearray = new TypeInfo(null, new ObjectInfo(Byte[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, bbyteconv))));
			typeinfos.add(ti_bbytearray);
			
			// java.lang.Character
			IObjectStringConverter charconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					char[] chars = (char[])val;
					return new String(chars);
					
				}
			};
			TypeInfo ti_chararray = new TypeInfo(null, new ObjectInfo(char[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, charconv))));
			typeinfos.add(ti_chararray);
			
			IObjectStringConverter characterconv = new IObjectStringConverter()
			{
				public String convertObject(Object val, IContext context)
				{
					Character[] bchars = (Character[])val;
					char[] chars = new char[bchars.length];
					for(int i=0; i<bchars.length; i++)
						chars[i] = bchars[i];
					return new String(chars);
				}
			};
			TypeInfo ti_characterarray = new TypeInfo(null, new ObjectInfo(Character[].class),
				new MappingInfo(null, null,
				new AttributeInfo(new AccessInfo((String)null, AccessInfo.THIS), new AttributeConverter(null, characterconv))));
			typeinfos.add(ti_characterarray);
			
			TypeInfo ti_tuple	= new TypeInfo(null, new ObjectInfo(Tuple.class), new MappingInfo(null, new SubobjectInfo[]
			{
				new SubobjectInfo(new AccessInfo("entities"))
			}));
			typeinfos.add(ti_tuple);
			
			TypeInfo ti_tuple2	= new TypeInfo(null, new ObjectInfo(Tuple2.class), new MappingInfo(null, new SubobjectInfo[]
			{
				new SubobjectInfo(new AccessInfo("firstEntity")),
				new SubobjectInfo(new AccessInfo("secondEntity"))
			}));
			typeinfos.add(ti_tuple2);		
			
			// java.util.UUID
			TypeInfo ti_uuid = new TypeInfo(null, new ObjectInfo(UUID.class), 
				new MappingInfo(null, new AttributeInfo[]{
				new AttributeInfo(new AccessInfo("leastSignificantBits", null)),
				new AttributeInfo(new AccessInfo("mostSignificantBits", null))},
				null
			));
			typeinfos.add(ti_uuid);
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
			createWriter();
		return Writer.objectToXML(writer, val, classloader);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader)
	{
		if(writer==null)
			createWriter();
		return Writer.objectToByteArray(writer, val, classloader);
	}
	
	/**
	 *  Write to output stream.
	 */
	public static void objectToOutputStream(Object val, OutputStream os, ClassLoader classloader)
	{
		if(writer==null)
			createWriter();
		Writer.objectToOutputStream(writer, val, os, classloader, null);
	}
	
	
	/**
	 *  Get the default Java writer.
	 *  @return The Java writer.
	 */
	public static Writer getInstance()
	{
		if(writer==null)
			createWriter();
		return writer;
	}
	
	/**
	 *  Conditionally create the writer instance.
	 *  Note that the synchronized check needs to be done.
	 *  Otherwise double creation may happen (leading to
	 *  concurrency issues).
	 */
	protected static synchronized void createWriter()
	{
		if(writer==null)
			writer = new JavaWriter(null);
	}
}
