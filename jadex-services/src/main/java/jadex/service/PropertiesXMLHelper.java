package jadex.service;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.QName;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;

import java.util.HashSet;
import java.util.Set;

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
		typeinfos.add(new TypeInfo(null, new QName[]{new QName("properties")}, Properties.class, null, null, 
			new BeanAttributeInfo[]{new BeanAttributeInfo(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"), null, AttributeInfo.IGNORE_READWRITE)}, null, null,
			new SubobjectInfo[]
			{
				new SubobjectInfo(new BeanAttributeInfo("property", "properties"), null, null, true), 
				new SubobjectInfo(new BeanAttributeInfo("properties", "subproperties"), null, null, true)
			}
		));
		
//		typeinfos.add(new TypeInfo(null, new QName[]{new QName("http://jadex.sourceforge.net/jadexconf", "property")}, Property.class, null, new BeanAttributeInfo(null, "value")));
		typeinfos.add(new TypeInfo(null, new QName[]{new QName("property")}, Property.class, null, new BeanAttributeInfo((String)null, "value")));
	}
	
	/**
	 *  Get the xml properties writer.
	 *  @return The writer.
	 */
	public static Writer getPropertyWriter()
	{
		if(writer==null)
			writer = new jadex.commons.xml.writer.Writer(new BeanObjectWriterHandler(typeinfos));
		return writer;
	}
	
	/**
	 *  Get the xml properties reader.
	 *  @return The reader.
	 */
	public static Reader getPropertyReader()
	{
		if(reader==null)
			reader = new jadex.commons.xml.reader.Reader(new BeanObjectReaderHandler(typeinfos));
		return reader;
	}
}
