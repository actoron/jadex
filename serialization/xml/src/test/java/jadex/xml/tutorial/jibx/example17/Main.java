package jadex.xml.tutorial.jibx.example17;

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
		
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("person", "identity")),
			new SubobjectInfo(new AccessInfo("company", "identity")),
			new SubobjectInfo(new AccessInfo("base-ident", "identity"))
			})));
		TypeInfo ti_id = new TypeInfo(new XMLInfo("base-ident"), new ObjectInfo(Identity.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("cust-num", "customerNumber"))
			}));
		typeinfos.add(ti_id);
		typeinfos.add(new TypeInfo(new XMLInfo("person"), new ObjectInfo(Person.class),
			new MappingInfo(ti_id, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("first-name", "firstName")),
			new SubobjectInfo(new AccessInfo("last-name", "lastName"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("company"), new ObjectInfo(Company.class),
			new MappingInfo(ti_id, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("tax-id", "taxId")),
			})));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader( false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example17/data1.xml", null);
		Object object1 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/jibx/example17/data2.xml", null);
		Object object2 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		is = SUtil.getResource("jadex/xml/tutorial/jibx/example17/data3.xml", null);
		Object object3 = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object 1: "+object1);
		System.out.println("Read object 2: "+object2);
		System.out.println("Read object 3: "+object3);
	}
}
