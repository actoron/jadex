package jadex.commons.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 *  Stax XML reader.
 */
public class Reader
{
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectHandler handler;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Reader(IObjectHandler handler)
	{
		this.handler = handler;
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public Object read(InputStream input, ClassLoader classloader, Object context) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		Object root = null;
		List objectstack = new ArrayList();
		List xmlstack = new ArrayList();
		String comment = null;
		String content = null;
		
		while(parser.hasNext())
		{
			int	next = parser.next();
			
			if(next==XMLStreamReader.COMMENT)
			{
				comment = parser.getText();
				System.out.println("Found comment: "+comment);
			}
			
			if(next==XMLStreamReader.CHARACTERS || next==XMLStreamReader.CDATA)
			{
				content += parser.getText(); 
				
				System.out.println("content: "+parser.getLocalName()+" "+content+" "+xmlstack);
			}
			
			if(next==XMLStreamReader.START_ELEMENT)
			{
				content = "";	

				xmlstack.add(parser.getLocalName());
				
				Object elem = handler.createObject(parser, comment, context, objectstack);
				if(elem!=null)
				{
					objectstack.add(new Object[]{parser.getLocalName(), elem, getDocumentPosition(xmlstack)});
					// Stax spec requires reader to advance cursor when getElementText() is called :-(
					next = parser.getEventType();
					comment = null;
				}
				
				System.out.println("start: "+parser.getLocalName()+" "+xmlstack);
			}
			
			if(next==XMLStreamReader.END_ELEMENT)
			{
				System.out.println("end: "+parser.getLocalName()+" "+xmlstack);
				
				if(objectstack.size()>0)
				{
					// Pop element from stack if there is one for the tag.
					Object[] se = (Object[])objectstack.get(objectstack.size()-1);
					
					// Hack. Add content when it is element of its own.
					content = content.trim();
					if(content.length()>0 && !se[0].equals(parser.getLocalName()))
					{
						Object[] tmp = new Object[]{parser.getLocalName(), content, getDocumentPosition(xmlstack)};
						objectstack.add(tmp);
						se = tmp;
						content = "";
					}
					
					if(se[0].equals(parser.getLocalName()))
					{
						if(content.length()>0)
						{
							handler.handleContent(parser, se[1], content, context, objectstack);
							content = "";
						}
						
						if(objectstack.size()==1)
						{
							root = se[1];
						}
						else
						{
							Object[] pse = (Object[])objectstack.get(objectstack.size()-2);
							handler.linkObject(parser, se[1], pse[1], context, objectstack);
						}
						objectstack.remove(objectstack.size()-1);
					}
				}
			
				xmlstack.remove(xmlstack.size()-1);
			}
		}
		parser.close();
		
		return root;
	}
	
	/**
	 * 
	 */
	protected String getDocumentPosition(List xmlstack)
	{
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<xmlstack.size(); i++)
		{
			ret.append(xmlstack.get(i));
			if(i<xmlstack.size()-1)
				ret.append("/");
		}
		return ret.toString();
	}
	
	/**
	 *  Main for testing.
	 *  @param args
	 *  @throws Exception
	 * /
	public static void main(String[] args) throws Exception
	{
		Map types = new HashMap();
		types.put("applicationtype", ApplicationType.class);
		types.put("structuringtype", StructuringType.class);
		types.put("agenttype", AgentType.class);
		types.put("application", Application.class);
		types.put("structuring", Structuring.class);
		types.put("agent", Agent.class);
		types.put("parameter", Parameter.class);
		types.put("parameterset", ParameterSet.class);
		types.put("value", String.class);
		types.put("import", String.class);
		types.put("property", String.class);
		
		Reader reader = new Reader(new BeanObjectHandler(types, "setDescription"));
		
//		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: "C:/projects/jadexv2/jadex-platform-base/src/main/java/jadex/adapter/base/appdescriptor/Test.application.xml");
		InputStream	input = new FileInputStream(args!=null && args.length==1? args[0]: "C:/projects/jadexv2/jadex-applications-bdi/src/main/java/jadex/bdi/examples/booktrading/BookTrading.application.xml");
		Object o = reader.read(input, null);
		System.out.println(o);
	}*/
	
}
