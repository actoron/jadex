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
import jadex.commons.xml.TypeInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
			Set types = new HashSet();
			
			// App types
			types.add(new TypeInfo("applicationtype", MApplicationType.class));
			types.add(new TypeInfo("spacetype", MSpaceType.class));
			types.add(new TypeInfo("agenttype", MAgentType.class));
			types.add(new TypeInfo("application", MApplicationInstance.class));
			types.add(new TypeInfo("space", MSpaceInstance.class));
			types.add(new TypeInfo("agent", MAgentInstance.class));
			types.add(new TypeInfo("parameter", MArgument.class));
			types.add(new TypeInfo("parameterset", MArgumentSet.class));
			types.add(new TypeInfo("value", String.class));
			types.add(new TypeInfo("import", String.class));
			types.add(new TypeInfo("property", String.class));
			
			// AGR types
			types.add(new TypeInfo("agrspacetype", MAGRSpaceType.class));
			types.add(new TypeInfo("grouptype", MGroupType.class));
			types.add(new TypeInfo("role", MRoleType.class));
			
			Reader	reader = new Reader(new BeanObjectHandler(), types, null, null);
			Object	ret	= reader.read(new FileInputStream(file), null, null);
			System.out.println(ret);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
