package jadex.xml.tutorial.example18;

import java.util.HashSet;
import java.util.Set;

import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.bean.BeanObjectWriterHandler;
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
		// This example shows what 'flattening' means for writing.
		// The same object structure is written with flattening=true and false.
		// As result flattening allows to share super tags, i.e. it puts
		// sub elements under the same super tag.
		
		Product sugar = new Product("sugar", 1.0);
		Product milk = new Product("milk", 0.5);
		Product egg = new Product("egg", 0.1);
		Product cookie = new Product("cookie", 2.0, new Part[]{
			new Part(sugar, 4), new Part(milk, 0.5), new Part(egg, 2)});
		ProductList pl = new ProductList(new Product[]{sugar, milk, egg, cookie});
		
		Set typeinfos = new HashSet();
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
		Writer xmlwriter = new Writer(false);
		String xml1 = Writer.objectToXML(xmlwriter, pl, null, new BeanObjectWriterHandler(typeinfos, false, true));
		xmlwriter = new Writer(false);
		String xml2 = Writer.objectToXML(xmlwriter, pl, null, new BeanObjectWriterHandler(typeinfos, false, true, false));
		
		// And print out the result.
		System.out.println("Wrote xml 1: "+xml1);
		System.out.println("Wrote xml 2: "+xml2);
	}
}
