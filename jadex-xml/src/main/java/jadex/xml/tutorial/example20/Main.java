package jadex.xml.tutorial.example20;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.ITypeConverter;
import jadex.xml.XMLInfo;
import jadex.xml.MappingInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
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
		
		ITypeConverter totalconv = new ITypeConverter()
		{
			public Object convertObject(Object val, Object root,
				ClassLoader classloader, Object context)
			{
				return Conversion.deserializeDollarsCents((String)val);
			}
		};
		
		ITypeConverter ordersconv = new ITypeConverter()
		{
			public Object convertObject(Object val, Object root,
				ClassLoader classloader, Object context)
			{
				return Conversion.deserializeIntArray((String)val);
			}
		};
		
		typeinfos.add(new TypeInfo(new XMLInfo("customer"), new ObjectInfo(Customer.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AttributeInfo("total", null, null, totalconv, null)),
			new SubobjectInfo(new AttributeInfo("orders", null, null, ordersconv, null))
		})));
		
//		typeinfos.add(new TypeInfo(null, "customer", Customer.class, null, null,
//			null, null, null, new SubobjectInfo[]{
//			new SubobjectInfo(new AttributeInfo("total", null, null, totalconv, null)),
//			new SubobjectInfo(new AttributeInfo("orders", null, null, ordersconv, null))
//		}));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example20/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
