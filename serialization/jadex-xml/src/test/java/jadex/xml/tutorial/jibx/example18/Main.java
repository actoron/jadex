package jadex.xml.tutorial.jibx.example18;

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
import jadex.xml.reader.Reader;
import jadex.xml.stax.QName;


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
		Set typeinfos = new HashSet();
		
		String uri1 = "http://www.sosnoski.com/ns1";
		String uri2 = "http://www.sosnoski.com/ns2";
		
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri1, "customer")}), new ObjectInfo(Customer.class))); 
		typeinfos.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri2, "person")}), new ObjectInfo(Person.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo(new QName(uri2, "cust-num"), "customerNumber"))		
			},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo(new QName(uri2, "first-name"), "firstName")),
			new SubobjectInfo(new AccessInfo(new QName(uri2, "last-name"), "lastName"))
			})));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example18/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
