package jadex.xml.tutorial.c;

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
 *  The xml and Java structures differ substantially.
 *  In Java only one object is used.
 *  
 *  Note: The following mapping cannot be done:
 *   public class Customer {
 *   public int customerNumber;
 *   public String firstName;
 *   public String lastName;
 *   public Address address;
 *   public String phone;
 * }
 * 
 * public class Address {
 *   public String street1;
 *   public String city;
 *   public String state;
 *   public String zip;
 * }
 * 
 *  Reason: The xml structure defines no tag identifying 
 *  the address. A subtag for address would be necessary.
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
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/c/customer.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
