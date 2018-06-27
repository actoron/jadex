package jadex.xml.tutorial.jibx.example14;

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
import jadex.xml.reader.Reader;

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
		Set typeinfos = new HashSet();
		
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("ship-address", "shipAddress")),
			new SubobjectInfo(new AccessInfo("bill-address", "billAddress"))
		})));
		
		typeinfos.add(new TypeInfo(new XMLInfo("ship-address"), new ObjectInfo(Address.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("bill-address"), new ObjectInfo(Address.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("subscriber"), new ObjectInfo(Subscriber.class)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example14/data1.xml", null);
		Object object1 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/jibx/example14/data2.xml", null);
		Object object2 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/jibx/example14/data3.xml", null);
		Object object3 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object 1: "+object1);
		System.out.println("Read object 2: "+object2);
		System.out.println("Read object 3: "+object3);
	}
}
