package jadex.xml.tutorial.example15;

import jadex.commons.SUtil;
import jadex.xml.ObjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

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
		// In this examples different namespaces are used.
		// Jadex XML assumes that tags without explicit namespace belong
		// to the default namespace. If they are from a different namespace
		// this namespace has to be used in the XMLInfo description via
		// QNames.
		
		// Create minimal type infos for both types that need to be mapped
		String uri = "http://jadex.informatik.uni-hamburg.de/ns2";
		
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("invoice"), new ObjectInfo(Invoice.class)));
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "product")}), new ObjectInfo(Product.class))); 
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example15/data.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
