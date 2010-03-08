package deco.lang.dynamics;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import deco4mas.util.xml.XmlUtil;

/**
 * Generate a Schema (.xsd) for the MASDynamics language (deco.lang.dynamics.MasDynamics).<br>
 * <br>
 * Call this class directly to retrieve the .xsd file.<br> 
 * By default, it will be saved under "schema1.xsd".
 * 
 * @author Jan Sudeikat
 *
 */
public class SchemaGenerator {

	//-------- methods -------------
	
	public static void main(String[] args) {
		
		try {
			
			// only one call to util class:
			XmlUtil.generateSchema(MASDynamics.class);
		
		} catch (JAXBException e) {
			System.err.println("Problem: Generating Schema representation");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem: reading / writing file system");
			e.printStackTrace();
		}
	
	}
}
