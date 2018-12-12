package jadex.xml.tutorial.example15;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
import jadex.xml.stax.QName;
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
		// In this example different namespaces are used for reading and writing.
		// Jadex XML assumes that tags without explicit namespace belong
		// to the default namespace. If they are from a different namespace
		// this namespace has to be used in the XMLInfo description via
		// QNames.
		
		// todo: explicit support for default namespace when writing, i.e.
		// omit prefix for default namespace.
		
		// Create minimal type infos for both types that need to be mapped
		String uri1 = "http://jadex.sourceforge.net/ns1";
		String uri2 = "http://jadex.sourceforge.net/ns2";
		
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri1, "invoice")}), new ObjectInfo(Invoice.class)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri2, "product")}), new ObjectInfo(Product.class))); 
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example15/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri1, "invoice")}), new ObjectInfo(Invoice.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri1, "name"))),
			new SubobjectInfo(new AccessInfo(new QName(uri1, "description"))),	
			new SubobjectInfo(new AccessInfo(new QName(uri1, "price"))),
			new SubobjectInfo(new AccessInfo(new QName(uri1, "quantity")))
		})));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri2, "product")}), new ObjectInfo(Product.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(uri2, "name")))		
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri2, "type"))),
			new SubobjectInfo(new AccessInfo(new QName(uri2, "date")))	
			})));
		
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
