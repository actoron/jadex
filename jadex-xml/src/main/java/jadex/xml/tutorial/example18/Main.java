package jadex.xml.tutorial.example18;

import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectWriterHandler;
import jadex.xml.writer.Writer;

import java.io.FileOutputStream;
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
		//
		
		Product sugar = new Product("sugar", 1.0);
		Product milk = new Product("milk", 0.5);
		Product egg = new Product("egg", 0.1);
		Product cookie = new Product("cookie", 2.0, new Part[]{
			new Part(sugar, 4), new Part(milk, 0.5), new Part(egg, 2)});
		ProductList pl = new ProductList(new Product[]{sugar, milk, egg, cookie});
		
		Set<TypeInfo> typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(new XMLInfo("products"), new ObjectInfo(ProductList.class),
			new MappingInfo(null, new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("product", "products"), null, true)
			}))); 
		typeinfos.add(new TypeInfo(new XMLInfo("product"), new ObjectInfo(Product.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("name"), null, AttributeInfo.ID)},
			new SubobjectInfo[]{
			new SubobjectInfo(new AccessInfo("price", "price")),
			new SubobjectInfo(new XMLInfo("parts/part"), new AccessInfo("part", "parts"), null, true)
			})));
		typeinfos.add(new TypeInfo(new XMLInfo("part"), new ObjectInfo(Part.class),
			new MappingInfo(null, new AttributeInfo[]{
			new AttributeInfo(new AccessInfo("product"), null, AttributeInfo.IDREF)
		})));
		
		// Write the xml to the output file.
		Writer xmlwriter = new Writer(new BeanObjectWriterHandler(false, true, true, typeinfos), false, true);
		OutputStream os = new FileOutputStream("out.xml");
		xmlwriter.write(pl, os, null, null);
		os.close();
		
		xmlwriter = new Writer(new BeanObjectWriterHandler(false, true, false, typeinfos), false, true);
		os = new FileOutputStream("out2.xml");
		xmlwriter.write(pl, os, null, null);
		os.close();
		
		// And print out the result.
		System.out.println("Wrote object to out.xml");
	}
}
