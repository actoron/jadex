package jadex.bdi.simulation.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLHandler {

	
	public static Object parseXML(String filename, Class classname){
		Object obj;
		
		try {						
			// configure input:
			JAXBContext ctx = JAXBContext.newInstance(classname);
			Unmarshaller u = ctx.createUnmarshaller();
			
			// read and return:
			obj =  u.unmarshal(new FileInputStream(filename));
//			System.out.println("res of XML: ") ;
			return obj;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeXML(Object obj, String filename, Class classname){
		// Write
		Writer w = null;
		try {
			JAXBContext context = JAXBContext.newInstance(classname);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			m.marshal(output, System.out);
			m.marshal(obj, System.out);

			w = new FileWriter(filename);
			m.marshal(obj, w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}
}
