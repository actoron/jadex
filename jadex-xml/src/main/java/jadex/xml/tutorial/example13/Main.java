package jadex.xml.tutorial.example13;

import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
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
		// This example shows how the same tag in an xml can be mapped to different
		// Java objects. In this case also the xml context (path) is equal.
		// In Jadex it is also possible to disambiguate even xml tags with the same context.
		// This can be done by using filters.
		
		// Create minimal type infos for types that need to be mapped
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("item", "product")),
			})));
		TypeInfo ti_product = new TypeInfo(new XMLInfo("product"), null, new MappingInfo(null,
			new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("type", "type", AccessInfo.IGNORE_READWRITE))	
			}));
		typeinfos.add(new TypeInfo(new XMLInfo("item", new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj!=null && "software".equals(((Map)obj).get("type"));
			}
		}), new ObjectInfo(Software.class), new MappingInfo(ti_product)));
		typeinfos.add(new TypeInfo(new XMLInfo("item",  new IFilter()
		{
			public boolean filter(Object obj)
			{
				return obj!=null && "computer".equals(((Map)obj).get("type"));
			}
		}), new ObjectInfo(Computer.class), new MappingInfo(ti_product)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example13/data.xml", null);
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
