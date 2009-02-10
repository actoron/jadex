package jadex.bridge;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


/**
 *  Read properties from xml.
 */
public class XMLApplicationReader
{
	/**
	 *  Read properties from xml.
 	 */
	public static Properties readProperties(InputStream input, ClassLoader classloader) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		Properties	root	= null;
		Map idprops = new HashMap();
		List	stack	= new ArrayList();
		List refs = new ArrayList();
		
		while(parser.hasNext())
		{
			int	next	= parser.next();
			switch(next)
			{
				case XMLStreamReader.START_ELEMENT:
					if(parser.getLocalName().equals("applicationtype"))
					{
						String name	= parser.getAttributeValue(null, "name");
//						String type	= parser.getAttributeValue(null, "type");
//						String id	= parser.getAttributeValue(null, "id");
//						Properties	props	= new Properties(name, type, id);
//						if(id!=null && id.length()>0)
//							idprops.put(id, props);
//						System.out.println("Found properties: "+props);
//						
//						if(root==null)
//							root	= props;
//						if(!stack.isEmpty())
//							((Properties)stack.get(stack.size()-1)).addSubproperties(props);
//						stack.add(props);
					}
					else if(parser.getLocalName().equals("agenttype"))
					{
						String	name	= parser.getAttributeValue(null, "name");
						
						System.out.println("Found agenttype: "+name);
						
					}
					else if(parser.getLocalName().equals("application"))
					{
						String	name	= parser.getAttributeValue(null, "name");
						
						System.out.println("Found application: "+name);
					}
					else if(parser.getLocalName().equals("agent"))
					{
						String	name	= parser.getAttributeValue(null, "name");
						
						System.out.println("Found agent: "+name);
					}
					break;
					
				case XMLStreamReader.END_ELEMENT:
					if(parser.getLocalName().equals("properties"))
					{
						stack.remove(stack.size()-1);
					}
					break;
			}
		}
		parser.close();
		
		return root;
	}
	
	/**
	 * 
	 *  @param args
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		InputStream	input	= new FileInputStream(args[0]);
		Properties	props	= readProperties(input, null);
		System.out.println(props);
	}
}
