package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MAgentInstance;
import jadex.adapter.base.appdescriptor.MAgentType;
import jadex.adapter.base.appdescriptor.MApplicationInstance;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MArgument;
import jadex.adapter.base.appdescriptor.MArgumentSet;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.xml.BeanObjectHandler;
import jadex.commons.xml.Reader;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *  Test loading of an xml with differennt name spaces (app + agr).
 */
public class TestNamespaceReader
{
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		File file	= new File("./src/main/java/jadex/adapter/base/agr/Marsworld.contexts.xml");
		
		// JAXB code
		// %JAVA_HOME%\bin\xjc -b application.xjb application.xsd -p jadex.adapter.base.appdescriptor.jaxb
		// %JAVA_HOME%\bin\xjc agr.xsd -p jadex.adapter.base.appdescriptor.jaxb.agr	
//		
//		try
//		{
//			JAXBContext jc = JAXBContext.newInstance(new Class[]{Applicationtype.class, Spacetypeel.class});
//			Unmarshaller unmarshaller = jc.createUnmarshaller();
//			Object ret = unmarshaller.unmarshal(file);
//			System.out.println(ret);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		// Jadex StAX XML reader code
		try
		{
			Map types = new HashMap();
			
			// App types
			types.put("applicationtype", MApplicationType.class);
			types.put("spacetype", MSpaceType.class);
			types.put("agenttype", MAgentType.class);
			types.put("application", MApplicationInstance.class);
			types.put("space", MSpaceInstance.class);
			types.put("agent", MAgentInstance.class);
			types.put("parameter", MArgument.class);
			types.put("parameterset", MArgumentSet.class);
			types.put("value", String.class);
			types.put("import", String.class);
			types.put("property", String.class);
			
			// AGR types
			types.put("agrspacetype", MAGRSpaceType.class);
			types.put("grouptype", MGroupType.class);
			types.put("role", MRoleType.class);
			
			Reader	reader = new Reader(new BeanObjectHandler(types, "setDescription"));
			Object	ret	= reader.read(new FileInputStream(file), null, null);
			System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
