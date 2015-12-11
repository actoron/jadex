package jadex.xml.tutorial.jibx.example07;

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
 *  Main class to execute tutorial lesson.
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
			new SubobjectInfo(new AccessInfo("street")),
			new SubobjectInfo(new AccessInfo("city")),
			new SubobjectInfo(new AccessInfo("state")),
			new SubobjectInfo(new AccessInfo("zip")),
			new SubobjectInfo(new AccessInfo("instructions", "instructions", AccessInfo.IGNORE_READWRITE)),
			new SubobjectInfo(new AccessInfo("phone"))
		})));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader( false, false, false, null);
		InputStream is = SUtil.getResource("jadex/xml/tutorial/jibx/example07/data.xml", null);
		Object object = xmlreader.read(new TypeInfoPathManager(typeinfos), new BeanObjectReaderHandler(), is, null, null);
		is.close();
		
		// Write the xml to the output file.
		Writer xmlwriter = new Writer(false, true, true);
		OutputStream os = new FileOutputStream("out.xml");
		xmlwriter.write(new BeanObjectWriterHandler(typeinfos), object, os, null, null);
		os.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
