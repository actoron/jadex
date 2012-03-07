package jadex.xml.tutorial.example01;

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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
		// Reading a simple xml in a fitting Java class with public fields.
		// Please note that Jadex xml automatically performs necessary basic
		// type conversions (here the quantity is made to int).
		
		// Create minimal type infos for types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("invoice"), new ObjectInfo(Invoice.class)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new TypeInfoPathManager(typeinfos), false, false, false, null, new BeanObjectReaderHandler());
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example01/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("invoice"), new ObjectInfo(Invoice.class),
			new MappingInfo(true)));
		
		// Write the xml to the output file.
		Writer xmlwriter = new Writer(new BeanObjectWriterHandler(typeinfos, false, true), false);
		String xml = Writer.objectToXML(xmlwriter, object, null);
//		OutputStream os = new FileOutputStream("out.xml");
//		xmlwriter.write(object, os, null, null);
//		os.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
		System.out.println("Wrote xml: "+xml);
		
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("invoice"), new ObjectInfo(Invoice.class),
			new MappingInfo(null, new SubobjectInfo[]{
				new SubobjectInfo(new AccessInfo("name")),
				new SubobjectInfo(new AccessInfo("description")),
				new SubobjectInfo(new AccessInfo("price")),
				new SubobjectInfo(new AccessInfo("key")),
				new SubobjectInfo(new AccessInfo("quantity"))
			})));
		
		// Write the xml to the output file.
		xmlwriter = new Writer(new BeanObjectWriterHandler(typeinfos, false, true), false);
		xml = Writer.objectToXML(xmlwriter, object, null);
	
		System.out.println("Wrote xml: "+xml);
	}
}
