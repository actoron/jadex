package jadex.adapter.base.appdescriptor;

import jadex.adapter.base.appdescriptor.jaxb.Applicationtype;
import jadex.adapter.base.appdescriptor.jaxb.agr.Spacetypeel;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;


/**
 * %JAVA_HOME%\bin\xjc -b application.xjb application.xsd -p jadex.adapter.base.appdescriptor.jaxb
 * %JAVA_HOME%\bin\xjc agr.xsd -p jadex.adapter.base.appdescriptor.jaxb.agr
 */
public class TestNamespaceReader
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(new Class[]{Applicationtype.class, Spacetypeel.class});
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Object ret = unmarshaller.unmarshal(new File("./src/main/java/jadex/adapter/base/appdescriptor/Marsworld.contexts.xml"));
			System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
