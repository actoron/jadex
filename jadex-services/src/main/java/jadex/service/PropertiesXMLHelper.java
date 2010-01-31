package jadex.service;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAttributeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  A simple static helper class for reading and writing jadex.commons.Properties.
 */
public class PropertiesXMLHelper
{
	//-------- static attributes --------
	
	/** The type infos. */
	public static Set typeinfos;
	
	/** The writer. */
	public static Writer writer;
	
	/** The reader. */
	public static Reader reader;
	
	//-------- static initializer --------
	
	static
	{
		typeinfos = new HashSet();
		
//		typeinfos.add(new TypeInfo(null, new QName[]{new QName("http://jadex.sourceforge.net/jadexconf", "properties")}, Properties.class, null, null, 
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName("properties")}), new ObjectInfo(Properties.class), 
			new MappingInfo(null, 
			new BeanAttributeInfo[]{
				new BeanAttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE)},
			new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("property", "properties"), null, null, true), 
				new SubobjectInfo(new BeanAttributeInfo("properties", "subproperties"), null, null, true)
			})));
		
//		typeinfos.add(new TypeInfo(null, new QName[]{new QName("http://jadex.sourceforge.net/jadexconf", "property")}, Property.class, null, new BeanAttributeInfo(null, "value")));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName("property")}), 
			new ObjectInfo(Property.class), new MappingInfo(null, null, new BeanAttributeInfo((String)null, "value"))));
	}
	
	/**
	 *  Get the xml properties writer.
	 *  @return The writer.
	 */
	public static Writer getPropertyWriter()
	{
		if(writer==null)
			writer = new jadex.xml.writer.Writer(new BeanObjectWriterHandler(typeinfos));
		return writer;
	}
	
	/**
	 *  Get the xml properties reader.
	 *  @return The reader.
	 */
	public static Reader getPropertyReader()
	{
		if(reader==null)
			reader = new jadex.xml.reader.Reader(new BeanObjectReaderHandler(typeinfos));
		return reader;
	}
}
