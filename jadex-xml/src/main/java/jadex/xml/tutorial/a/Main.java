package jadex.xml.tutorial.a;

import jadex.commons.SUtil;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 *  Main class to execute tutorial lesson a.
 *  
 *  Topic: reading a simple XML file in Java objects.
 *  The xml and Java structures directly correspond to each other.
 */
public class Main
{
	/**
	 *  Main method for using the xml reader/writer.
	 */
	public static void main(String[] args) throws Exception
	{
		// Create minimal type infos for both types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(null, "customer", Customer.class));
		typeinfos.add(new TypeInfo(null, "person", Person.class));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/a/customer.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
