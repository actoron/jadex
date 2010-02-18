package jadex.xml.tutorial.example14;

import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *  Main class to execute tutorial lesson c (taken from Jibx website).
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
		
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("ship-address", "shipaddress")),
			new SubobjectInfo(new AccessInfo("bill-address", "billaddress"))
		})));
		
		typeinfos.add(new TypeInfo(new XMLInfo("ship-address"), new ObjectInfo(Address.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("bill-address"), new ObjectInfo(Address.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("subscriber"), new ObjectInfo(Subscriber.class)));
		
//		typeinfos.add(new TypeInfo(null, "customer", Customer.class, null, null,
//			null, null, null, new SubobjectInfo[]{
//			new SubobjectInfo(new AttributeInfo("ship-address", "shipaddress")),
//			new SubobjectInfo(new AttributeInfo("bill-address", "billaddress"))
//		}));
//		typeinfos.add(new TypeInfo(null, "ship-address", Address.class));
//		typeinfos.add(new TypeInfo(null, "bill-address", Address.class));
//		typeinfos.add(new TypeInfo(null, "subscriber", Subscriber.class));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example14/data1.xml", null);
		Object object1 = xmlreader.read(is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/example14/data2.xml", null);
		Object object2 = xmlreader.read(is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/example14/data3.xml", null);
		Object object3 = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object 1: "+object1);
		System.out.println("Read object 2: "+object2);
		System.out.println("Read object 3: "+object3);
	}
}
