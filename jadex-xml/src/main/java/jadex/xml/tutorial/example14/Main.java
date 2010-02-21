package jadex.xml.tutorial.example14;

import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectReaderHandler;
import jadex.xml.reader.Reader;

import java.io.InputStream;
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
		// This example shows the usage of post processors. 
		// They can be used to initialize objects in arbitrary many passes.
		// Pass 0 means that the post processor is called directly
		// after the construction of the object has finished (just before
		// object linking). All higher passes are called after the
		// complete object structure has been built.
		// Here it is used to set the product list on the single products.
		
		// Create minimal type infos for types that need to be mapped
		
		IPostProcessor listpp = new IPostProcessor()
		{
			public Object postProcess(IContext context, Object object)
			{
				ProductList pl = ((ProductList)object);
				Product[] products = pl.getProducts();
				for(int i=0; i<products.length; i++)
				{
					products[i].setProductlist(pl);
				}
				return null; // Only in pass 0 when object should be changed
			}
			
			public int getPass()
			{
				return 1;
			}
		};
		
		Set typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class, listpp), 
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("software", "product")),
			new SubobjectInfo(new AccessInfo("computer", "product"))
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("software"), new ObjectInfo(Software.class)));
		typeinfos.add(new TypeInfo(new XMLInfo("computer"), new ObjectInfo(Computer.class)));
		
		// Create an xml reader with standard bean object reader and the
		// custom typeinfos
		Reader xmlreader = new Reader(new BeanObjectReaderHandler(typeinfos));
		InputStream is = SUtil.getResource("jadex/xml/tutorial/example14/data.xml", null);
		
		// Read the xml.
		Object object = xmlreader.read(is, null, null);
		is.close();
		
		// And print out the result.
		System.out.println("Read object: "+object);
	}
}
