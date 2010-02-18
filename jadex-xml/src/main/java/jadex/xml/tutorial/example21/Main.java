package jadex.xml.tutorial.example21;

import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.IContext;
import jadex.xml.IObjectObjectConverter;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubObjectConverter;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

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
		
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("entry"), new ObjectInfo(Entry.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("directory"), new ObjectInfo(Directory.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("entry", null, null, null, 
			new BeanAccessInfo(Directory.class.getField("customerMap"),
				null, "customerMap", Entry.class.getField("key"))),
			new SubObjectConverter(new IObjectObjectConverter()
			{
				public Object convertObject(Object val, IContext context)
				{
					return ((Entry)val).customer;
				}
			}, null))
		})));
		
		//Directory.class.getMethod("putCustomer", new Class[]{String.class, Object.class})
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example21/data0.xml", null);
		Object object1 = xmlreader.read(is, null, null);
		is.close();
//		is = SUtil.getResource("jadex/xml/tutorial/example21/data1.xml", null);
//		Object object2 = xmlreader.read(is, null, null);
//		is.close();
		
		// And print out the result.
		System.out.println("Read object 1: "+object1);
//		System.out.println("Read object 2: "+object2);
	}
}
