package jadex.xml.tutorial.example10;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.XMLInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanAttributeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.tutorial.example08.Airport;
import jadex.xml.tutorial.example08.Carrier;
import jadex.xml.writer.Writer;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

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
		typeinfos.add(new TypeInfo(new XMLInfo("timetable"), new ObjectInfo(TimeTable.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("carrier"), new ObjectInfo(Carrier.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("airport"), new ObjectInfo(Airport.class)));
		
//		new TypeInfo(null, "timetable", TimeTable.class, null, null, null, null, null,
//			new SubobjectInfo[]{
//			//new SubobjectInfo(new BeanAttributeInfo("phone", "phone")),
//		}, true, true));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example10/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
