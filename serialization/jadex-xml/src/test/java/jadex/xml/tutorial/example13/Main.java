package jadex.xml.tutorial.example13;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

/**
 *  Main class to execute tutorial lesson.
 */
public class Main
{
	/**
	 *  Main method for using the xml reader/writer.
	 */
	public static void main(String[] args) throws Exception
	{
		// This example shows how the same tag in an xml can be mapped to different
		// Java objects. In this case also the xml context (path) is equal.
		// In Jadex it is also possible to disambiguate even xml tags with the same context.
		// This can be done by using filters.
		
		// Create minimal type infos for types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("item", "products"), null, true),
			})));
		TypeInfo ti_product = new TypeInfo(new XMLInfo("product"), null, new MappingInfo(null,
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", "type", AccessInfo.IGNORE_READ))	
			}));
		typeinfos.add(new TypeInfo(new XMLInfo("item", new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj!=null && "Software".equals(((Map)obj).get("type"));
			}
		}), new ObjectInfo(Software.class), new MappingInfo(ti_product)));
		typeinfos.add(new TypeInfo(new XMLInfo("item",  new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj!=null && "Computer".equals(((Map)obj).get("type"));
			}
		}), new ObjectInfo(Computer.class), new MappingInfo(ti_product)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example13/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
//		typeinfos = new HashSet();
//		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class), 
//			new MappingInfo(null, new SubobjectInfo[]{
//			new SubobjectInfo(new AccessInfo("item", "products"), null, true, new ObjectInfo(Software.class)),
//			new SubobjectInfo(new AccessInfo("computers", "products"), null, true, new ObjectInfo(Computer.class)),
//			})));

		// Write the xml to the output file.
		Writer xmlwriter = new Writer(false);
		String xml = Writer.objectToXML(xmlwriter, object, null, new BeanObjectWriterHandler(typeinfos, false, true));
//		OutputStream os = new FileOutputStream("out.xml");
//		xmlwriter.write(object, os, null, null);
//		os.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
		System.out.println("Wrote xml: "+xml);
	}
}
