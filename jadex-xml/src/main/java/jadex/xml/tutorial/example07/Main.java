package jadex.xml.tutorial.example07;

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
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
import java.text.SimpleDateFormat;
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
		// Read an xml in an object structure (an invoice that contains a product object). 
		// Using a custom converter to convert a string to 
		
		// Create minimal type infos for both types that need to be mapped
		Set typeinfos = new HashSet();
		
		IObjectObjectConverter dateconv = new IObjectObjectConverter()
		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy");
			public Object convertObject(Object val, IContext context)
			{
				Object ret = null;
				try
				{
					ret = sdf.parse((String)val);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return ret;
			}
		};
		
		typeinfos.add(new TypeInfo(new XMLInfo("invoice"), new ObjectInfo(Invoice.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("product"), new ObjectInfo(Product.class), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("date", "date"), new SubObjectConverter(dateconv, null))
			})));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example07/data.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
