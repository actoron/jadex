package jadex.adapter.base.appdescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;


/**
 * c:\Programme\Java\jdk1.6.0_04\bin\xjc -b application.xjb application.xsd -p jadex.adapter.base.appdescriptor.jaxb
 */
public class TestReader
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance("jadex.adapter.base.appdescriptor.jaxb");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Object ret = unmarshaller.unmarshal(new File("C:/projects/jadexv2/jadex-platform-base/src/main/java/jadex/adapter/base/appdescriptor/Test.application.xml"));
			System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
