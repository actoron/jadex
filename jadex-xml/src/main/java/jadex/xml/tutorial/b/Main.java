package jadex.xml.tutorial.b;

import jadex.commons.SUtil;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanAttributeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 *  Main class to execute tutorial lesson a.
 *  
 *  Topic: reading a simple XML file in Java objects.
 *  The xml and Java structures differ a bit in the way
 *  the person tags are defined.
 */
public class Main
{
	/**
	 *  Main method for using the xml reader/writer.
	 */
	public static void main(String[] args) throws Exception
	{
		// Create Type infos for both types that need to be mapped
		// The person type has 3 subobjects that are mapped to different
		// object attributes. They are considered as subobjectinfos here
		// and not as attributeinfos, because they are subtags in they xml.
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(null, "customer", Customer.class));
		typeinfos.add(new TypeInfo(null, "person", Person.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("cust-num", "customernumber")),
			new SubobjectInfo(new BeanAttributeInfo("first-name", "firstname")),
			new SubobjectInfo(new BeanAttributeInfo("last-name", "lastname"))
			}
		));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/b/customer.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
