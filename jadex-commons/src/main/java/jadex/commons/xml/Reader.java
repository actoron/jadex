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
	
	/** The stack of elements. */
	protected List stack;
	
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
		this.stack = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
 	 */
	public Object read(InputStream input, ClassLoader classloader) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		Object root = null;
		List stack	= new ArrayList();
		String comment = null;
		
		while(parser.hasNext())
		{
			int	next = parser.next();
			
			if(next==XMLStreamReader.COMMENT)
			{
				comment = parser.getText();
				System.out.println("Found comment: "+comment);
			}
			
			if(next==XMLStreamReader.START_ELEMENT)
			{
				Object elem = handler.createObject(parser, comment);
				if(elem!=null)
				{
					stack.add(new Object[]{parser.getLocalName(), elem});
					// Stax spec requires reader to advance cursor when getElementText() is called :-(
					next = parser.getEventType();
					comment = null;
				}
				
				System.out.println("start: "+parser.getLocalName());
			}
			
			if(next==XMLStreamReader.END_ELEMENT)
			{
				System.out.println("end: "+parser.getLocalName());
				
				if(stack.size()>0)
				{
					// Pop element from stack if there is one for the tag.
					Object[] se = (Object[])stack.get(stack.size()-1);
					if(se[0].equals(parser.getLocalName()))
					{
						stack.remove(stack.size()-1);
						if(stack.size()==0)
						{
							root = se[1];
						}
						else
						{
							Object[] pse = (Object[])stack.get(stack.size()-1);
							handler.linkObject(parser, se[1], pse[1]);
						}
					}
				}
			}
		}
		parser.close();
		
		return root;
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
