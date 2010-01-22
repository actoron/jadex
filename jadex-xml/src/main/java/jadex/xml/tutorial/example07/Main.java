package jadex.xml.tutorial.example07;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanAttributeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 *  Main class to execute tutorial lesson c (taken from Jibx website).
 *  
 *  Topic: reading a simple XML file in Java objects.
 *  The xml and Java structures differ substantially.
 *  In Java only one object is used.
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
		typeinfos.add(new TypeInfo(null, "customer", Customer.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("street", "street")),
			new SubobjectInfo(new BeanAttributeInfo("city", "city")),
			new SubobjectInfo(new BeanAttributeInfo("state", "state")),
			new SubobjectInfo(new BeanAttributeInfo("zip", "zip")),
			new SubobjectInfo(new BeanAttributeInfo("instructions", "instructions", AttributeInfo.IGNORE_READWRITE)),
			new SubobjectInfo(new BeanAttributeInfo("phone", "phone")),
		}, true, true));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example07/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// Write the xml to the output file.
		Writer xmlwriter = new Writer(new BeanObjectWriterHandler(typeinfos), false, true);
		OutputStream os = new FileOutputStream("out.xml");
		xmlwriter.write(object, os, null, null);
		os.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
