package jadex.xml;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.HashSet;
import java.util.Set;

/* $if !android $ */
import javax.xml.namespace.QName;
/* $else $
import javaxx.xml.namespace.QName;
$endif $ */

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
		
		String uri = "http://jadex.sourceforge.net/jadexconf";
		
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
		
//		typeinfos.add(new TypeInfo(null, new QName[]{new QName("http://jadex.sourceforge.net/jadexconf", "property")}, Property.class, null, new BeanAttributeInfo(null, "value")));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName(uri, "property")), 
			new ObjectInfo(Property.class), new MappingInfo(null, null, new AttributeInfo(new AccessInfo((String)null, "value")),
				new AttributeInfo[]{new AttributeInfo(new AccessInfo("name")), new AttributeInfo(new AccessInfo("type"))})));
	}
	
	/**
	 *  Get the xml properties writer.
	 *  @return The writer.
	 */
	public static Writer getPropertyWriter()
	{
		if(writer==null)
		{
			synchronized(PropertiesXMLHelper.class)
			{
				if(writer==null)
				{
					writer = new jadex.xml.writer.Writer(new BeanObjectWriterHandler(typeinfos));
				}
			}
		}
		return writer;
	}
	
	/**
	 *  Get the xml properties reader.
	 *  @return The reader.
	 */
	public static Reader getPropertyReader()
	{
		if(reader==null)
		{
			synchronized(PropertiesXMLHelper.class)
			{
				if(reader==null)
				{
					reader = new jadex.xml.reader.Reader(new TypeInfoPathManager(typeinfos));
				}
			}
		}
		return reader;
	}
}
