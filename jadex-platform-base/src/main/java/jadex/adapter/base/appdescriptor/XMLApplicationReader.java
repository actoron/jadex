package jadex.adapter.base.appdescriptor;

import java.io.FileInputStream;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

/**
 *  Read properties from xml.
 */
public class XMLApplicationReader
{
	/** The xstream. */
	protected static XStream xstream;
	
	static
	{
		xstream = new XStream(new PureJavaReflectionProvider());
		xstream.alias("application", Application.class);
		xstream.alias("applicationtype", ApplicationType.class);
		xstream.alias("agent", Agent.class);
		xstream.alias("agenttype", AgentType.class);
		xstream.alias("parameter", Parameter.class);
		xstream.alias("parameterset", ParameterSet.class);
		xstream.alias("value", String.class);
	}
	
	/**
	 * 
	 * /
	public XMLApplicationReader()
	{
		xstream = new XStream();
		xstream.alias("application", Application.class);
		xstream.alias("applicationtype", ApplicationType.class);
		xstream.alias("agent", Agent.class);
		xstream.alias("agenttype", AgentType.class);
		xstream.alias("parameter", AgentType.class);
		xstream.alias("parameterset", AgentType.class);
	}*/
	
	/**
	 *  Read properties from xml.
 	 */
	public static ApplicationType readApplication(InputStream input, ClassLoader classloader) throws Exception
	{
		xstream.useAttributeFor("name", String.class); 
		xstream.useAttributeFor("filename", String.class); 
		xstream.useAttributeFor("type", String.class); 
		xstream.useAttributeFor("value", String.class); 
		xstream.useAttributeFor("start", boolean.class); 
		
		ApplicationType apptype = (ApplicationType)xstream.fromXML(input);
		
		return apptype;
	}
	
	/**
	 * 
	 *  @param args
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
//		XMLApplicationReader reader = new XMLApplicationReader();
		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: "C:/projects/jadexv2/jadex-platform-base/src/main/java/jadex/adapter/base/appdescriptor/Test.application.xml");
		ApplicationType props = readApplication(input, null);
		System.out.println(props);
	}
}
