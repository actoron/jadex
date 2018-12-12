package jadex.xml.tutorial.jibx.example03;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

/**
 * Main class to execute tutorial lesson.
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
		typeinfos.add(new TypeInfo(new XMLInfo("person"), new ObjectInfo(Person.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("cust-num", "customerNumber")),
			new SubobjectInfo(new AccessInfo("first-name", "firstName")),
			new SubobjectInfo(new AccessInfo("last-name", "lastName"))
		})));

		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example03/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// The typeinfos need to be refined for writing.
		// The writer must know which subobjects/attributes to write.
		// If attributes instead of subtags would be ok, then the following would be sufficient:
		// typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class), 
		// new MappingInfo(true)));
		// It tells the writer to also write Java fields (normally only bean accessible 
		// attributes will be written).
		
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("person", "person")),
			new SubobjectInfo(new AccessInfo("street", "street")),
			new SubobjectInfo(new AccessInfo("city", "city")),
			new SubobjectInfo(new AccessInfo("state", "state")),
			new SubobjectInfo(new AccessInfo("zip", "zip")),
			new SubobjectInfo(new AccessInfo("phone", "phone"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("person"), new ObjectInfo(Person.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("cust-num", "customerNumber")),
			new SubobjectInfo(new AccessInfo("first-name", "firstName")),
			new SubobjectInfo(new AccessInfo("last-name", "lastName"))
		})));
		
		// Write the xml to the output file.
		Writer xmlwriter = new Writer(false, true, true);
		OutputStream os = new FileOutputStream("out.xml");
		xmlwriter.write(new BeanObjectWriterHandler(typeinfos), object, os, null, null);
		os.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
