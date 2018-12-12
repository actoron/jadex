package jadex.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.AReader;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.XMLReaderFactory;
import jadex.xml.stax.QName;
import jadex.xml.writer.AWriter;
import jadex.xml.writer.IObjectWriterHandler;
import jadex.xml.writer.XMLWriterFactory;

/**
 *  A simple static helper class for reading and writing jadex.commons.Properties.
 */
public class PropertiesXMLHelper
{
	//-------- static attributes --------
	
	/** The type infos. */
	public static final Set typeinfos;
	
//	/** The writer. */
//	public static Writer writer;
//	
//	/** The reader. */
//	public static Reader reader;
	
	/** The path manager. */
	protected static volatile TypeInfoPathManager pathmanager;

	/** The reader handler. */
	protected static volatile IObjectReaderHandler readerhandler;

	/** The writer handler. */
	protected static volatile IObjectWriterHandler writerhandler;

	
	//-------- static initializer --------
	
	static
	{
		typeinfos = new HashSet();
		
		String uri = "http://www.activecomponents.org/jadex-conf";
		
		TypeInfo	propstype	= new TypeInfo(new XMLInfo(new QName(uri, "properties")), new ObjectInfo(Properties.class), 
			new MappingInfo(null, 
			new AttributeInfo[]{
				new AttributeInfo(new AccessInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AccessInfo.IGNORE_READWRITE)),
				new AttributeInfo(new AccessInfo("name")),
				new AttributeInfo(new AccessInfo("type")),
				new AttributeInfo(new AccessInfo("id"))
			},
			new SubobjectInfo[]{
				new SubobjectInfo(new XMLInfo(new QName(uri, "property")), new AccessInfo(new QName(uri, "property"), "properties"), null, true), 
				new SubobjectInfo(new XMLInfo(new QName(uri, "properties")), new AccessInfo(new QName(uri, "properties"), "subproperties"), null, true)
			}));
		propstype.setReaderHandler(new BeanObjectReaderHandler());
		typeinfos.add(propstype);
		
//		typeinfos.add(new TypeInfo(null, new QName[]{new QName("http://www.activecomponents.org/jadex-conf", "property")}, Property.class, null, new BeanAttributeInfo(null, "value")));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), 
			new ObjectInfo(Property.class), new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, "value")),
				new AttributeInfo[]{new AttributeInfo(new AccessInfo("name")), new AttributeInfo(new AccessInfo("type"))})));
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String write(Object val, ClassLoader classloader)
	{
		return AWriter.objectToXML(XMLWriterFactory.getInstance().createWriter(), val, classloader, getObjectWriterHandler());
	}
	
	/**
	 *  Convert to a string.
	 */
	public static void write(Object val, OutputStream os, ClassLoader classloader)
	{
		AWriter.objectToOutputStream(XMLWriterFactory.getInstance().createWriter(), val, os, classloader, null, getObjectWriterHandler());
	}
	
	/**
	 *  Convert an xml to an object.
	 *  @param val The string value.
	 *  @return The decoded object.
	 */
	public static <T> T read(String val, ClassLoader classloader)
	{
		return (T)AReader.objectFromXML(XMLReaderFactory.getInstance().createReader(), val, classloader, getPathManager(), getObjectReaderHandler());
	}
	
	/**
	 *  Convert an xml to an object.
	 *  @param val The string value.
	 *  @return The decoded object.
	 */
	public static <T> T read(InputStream is, ClassLoader classloader)
	{
		return (T)AReader.objectFromInputStream(XMLReaderFactory.getInstance().createReader(), is, classloader, getPathManager(), getObjectReaderHandler());
	}
	
	
//	/**
//	 *  Get the xml properties writer.
//	 *  @return The writer.
//	 */
//	public static Writer getPropertyWriter()
//	{
//		if(writer==null)
//		{
//			synchronized(PropertiesXMLHelper.class)
//			{
//				if(writer==null)
//				{
//					writer = new jadex.xml.writer.Writer(new BeanObjectWriterHandler(typeinfos));
//				}
//			}
//		}
//		return writer;
//	}
//	
//	/**
//	 *  Get the xml properties reader.
//	 *  @return The reader.
//	 */
//	public static Reader getPropertyReader()
//	{
//		if(reader==null)
//		{
//			synchronized(PropertiesXMLHelper.class)
//			{
//				if(reader==null)
//				{
//					reader = new jadex.xml.reader.Reader(new TypeInfoPathManager(typeinfos));
//				}
//			}
//		}
//		return reader;
//	}
	
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
					pathmanager = new TypeInfoPathManager(typeinfos);
				}
			}
		}
		return pathmanager;
	}
	
	
	/**
	 *  Get the default Java reader.
	 *  @return The Java reader.
	 */
	public static IObjectReaderHandler getObjectReaderHandler()
	{
		if(readerhandler==null)
		{
			synchronized(JavaReader.class)
			{
				if(readerhandler==null)
				{
					readerhandler = new BeanObjectReaderHandler(typeinfos);
				}
			}
		}
		return readerhandler;
	}
	
	/**
	 *  Get the default Java writer.
	 *  @return The Java writer.
	 */
	public static IObjectWriterHandler getObjectWriterHandler()
	{
		if(writerhandler==null)
		{
			synchronized(JavaWriter.class)
			{
				if(writerhandler==null)
				{
					writerhandler = new BeanObjectWriterHandler(typeinfos);
				}
			}
		}
		return writerhandler;
	}
}
