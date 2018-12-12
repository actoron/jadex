package jadex.xml.tutorial.example19;

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
		// The same as example 17 (for website with simpler object).
		
		Invoice object = new Invoice("Invoice for shoes", 43.95);
		
		String xml = JavaWriter.objectToXML(object, null);
		
		System.out.println("xml is:"+xml);
		
		Object ro = JavaReader.objectFromXML(xml, null);
		
		if(!object.equals(ro))
			System.out.println("Not equal: "+object.getClass()+" \n"+ro.getClass()+" \n"+xml);
		else
			System.out.println("Successfully serialzed and deserialized: "+object);
	}
}
