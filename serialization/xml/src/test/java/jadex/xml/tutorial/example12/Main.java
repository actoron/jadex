package jadex.xml.tutorial.example12;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
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
		// Java objects. In this case the context of the xml tag is used to distinguish
		// both kinds of object (software/item vs. computers", "product).
		
		// During writing one problem is that we want to put 'software' and 'computer'
		// tags around products according to their type. This can be done without
		// implementing special getSoftware() and getComputers() methods in the Java product list class.
		// Instead two multi subobjects are declared using an object info to declare which
		// object instances are really part of the subobject.
		
		// Create minimal type infos for types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("software", "product")),
			new SubobjectInfo(new AccessInfo("computers", "product"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("software/item"), new ObjectInfo(Software.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("computers/item"), new ObjectInfo(Computer.class)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example12/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new XMLInfo("software/item"), new AccessInfo("software", "products"), null, true, new ObjectInfo(Software.class)),
			new SubobjectInfo(new XMLInfo("computers/item"), new AccessInfo("computers", "products"), null, true, new ObjectInfo(Computer.class)),
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("software/item"), new ObjectInfo(Software.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("computers/item"), new ObjectInfo(Computer.class)));

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
