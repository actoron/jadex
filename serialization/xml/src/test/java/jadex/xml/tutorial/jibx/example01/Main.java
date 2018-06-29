package jadex.xml.tutorial.jibx.example01;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.xml.ObjectInfo;
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
		// Create minimal type infos for both types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("person"), new ObjectInfo(Person.class)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example01/data.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
