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
	//-------- static initialization ----------
	
	/** The xstream. */
	protected static XStream xstream;
	
	static
	{
		xstream = new XStream(new PureJavaReflectionProvider());
		xstream.alias("applicationtype", ApplicationType.class);
		xstream.alias("structuringtype", StructuringType.class);
		xstream.alias("agenttype", AgentType.class);
		
		xstream.alias("application", Application.class);
		xstream.alias("structuring", Structuring.class);
		xstream.alias("agent", Agent.class);
		xstream.alias("parameter", Parameter.class);
		xstream.alias("parameterset", ParameterSet.class);
		xstream.alias("value", String.class);
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
 	 */
	public static ApplicationType readApplication(InputStream input, ClassLoader classloader) throws Exception
	{
		xstream.useAttributeFor("name", String.class); 
		xstream.useAttributeFor("filename", String.class); 
		xstream.useAttributeFor("type", String.class); 
		xstream.useAttributeFor("start", boolean.class); 
		xstream.useAttributeFor("number", int.class);
		
		xstream.registerConverter(new ParameterConverter());
		xstream.registerConverter(new ParameterSetConverter());
		xstream.registerConverter(new StructuringTypeConverter());
		
		ApplicationType apptype = (ApplicationType)xstream.fromXML(input);
		
		return apptype;
	}
	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args) throws Exception
	{
		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: "C:/projects/jadexv2/jadex-platform-base/src/main/java/jadex/adapter/base/appdescriptor/Test.application.xml");
		ApplicationType props = readApplication(input, null);
		System.out.println(props);
	}
}
