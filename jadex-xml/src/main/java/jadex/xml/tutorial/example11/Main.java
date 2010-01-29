package jadex.xml.tutorial.example11;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.XMLInfo;
import jadex.xml.MappingInfo;
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
		typeinfos.add(new TypeInfo(new XMLInfo("timetable"), new ObjectInfo(TimeTable.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("carrier"), new ObjectInfo(Carrier.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("code", null, null, null, null, null, false, AttributeInfo.ID)})));
		typeinfos.add(new TypeInfo(new XMLInfo("carrier"), new ObjectInfo(Carrier.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("code", null, null, null, null, null, false, AttributeInfo.ID)})));
		typeinfos.add(new TypeInfo(new XMLInfo("airport"), new ObjectInfo(Airport.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("code", null, null, null, null, null, false, AttributeInfo.ID)})));
		typeinfos.add(new TypeInfo(new XMLInfo("route"), new ObjectInfo(Route.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("from", null, null, null, null, null, false, AttributeInfo.IDREF),
			new AttributeInfo("to", null, null, null, null, null, false, AttributeInfo.IDREF)}
		)));
		typeinfos.add(new TypeInfo(new XMLInfo("route"), new ObjectInfo(Airport.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("from", null, null, null, null, null, false, AttributeInfo.IDREF),
			new AttributeInfo("to", null, null, null, null, null, false, AttributeInfo.IDREF)}
		)));
		typeinfos.add(new TypeInfo(new XMLInfo("flight"), new ObjectInfo(Flight.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo("carrier", null, null, null, null, null, false, AttributeInfo.IDREF),
			new AttributeInfo("depart", "departure"),
			new AttributeInfo("arrive", "arrival"),}
		)));
	
//		typeinfos.add(new TypeInfo(null, "timetable", TimeTable.class, null, null, null, null, null,
//			new SubobjectInfo[]{
//			//new SubobjectInfo(new BeanAttributeInfo("phone", "phone")),
//		}, true, true));
//		typeinfos.add(new TypeInfo(null, "carrier", Carrier.class, null, null, 
//			new AttributeInfo[]{
//			new AttributeInfo("code", "code", null, null, null, null, false, AttributeInfo.ID)
//		}, null));
//		typeinfos.add(new TypeInfo(null, "airport", Airport.class, null, null, 
//			new AttributeInfo[]{
//			new AttributeInfo("code", "code", null, null, null, null, false, AttributeInfo.ID)
//		}, null));
//		typeinfos.add(new TypeInfo(null, "route", Route.class, null, null, 
//			new AttributeInfo[]{
//			new AttributeInfo("from", "from", null, null, null, null, false, AttributeInfo.IDREF),
//			new AttributeInfo("to", "to", null, null, null, null, false, AttributeInfo.IDREF)
//		}, null));
//		typeinfos.add(new TypeInfo(null, "flight", Flight.class, null, null, 
//			new AttributeInfo[]{
//			new AttributeInfo("carrier", "carrier", null, null, null, null, false, AttributeInfo.IDREF),
//			new AttributeInfo("depart", "departure"),
//			new AttributeInfo("arrive", "arrival"),
//		}, null));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example11/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
