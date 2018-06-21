package jadex.xml.tutorial.example17;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

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
		// This example shows how the generic Java writer and reader
		// can be used to serialze and deserialize Java beans.
		// The Java writer/reader classes have built-in support for
		// collections like lists, maps and arrays. 
		// The Java reader/writer classes are based on the normal 
		// reader and writer and only use specific Java type infos for
		// Java classes that are not Java beans.
		// The Java writer/reader can also cope with cyclic object
		// structures, which are handled using auto generated id/idrefs.
		
		Product sugar = new Product("sugar", 1.0);
		Product milk = new Product("milk", 0.5);
		Product egg = new Product("egg", 0.1);
		Product cookie = new Product("cookie", 2.0, new Part[]{
			new Part(sugar, 4), new Part(milk, 0.5), new Part(egg, 2)});
		ProductList pl = new ProductList(new Product[]{sugar, milk, egg, cookie});
		
		String xml = JavaWriter.objectToXML(pl, null);
		
//		System.out.println("xml is:"+xml);
		
		Object ro = JavaReader.objectFromXML(xml, null);
		
		if(!pl.equals(ro))
			System.out.println("Not equal: "+pl.getClass()+" \n"+ro.getClass()+" \n"+xml);
		else
			System.out.println("Successfully serialzed and deserialized: "+pl);
	}
}
