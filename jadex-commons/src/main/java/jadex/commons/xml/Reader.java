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
		List stack = new ArrayList();
		String comment = null;
		String content = null;
		
		while(parser.hasNext())
		{
			int	next = parser.next();
			
			if(next==XMLStreamReader.COMMENT)
			{
				comment = parser.getText();
//				System.out.println("Found comment: "+comment);
			}
			
			else if(next==XMLStreamReader.CHARACTERS)// || next==XMLStreamReader.CDATA)
			{
				content += parser.getText(); 
				
//				System.out.println("content: "+parser.getLocalName()+" "+content);
			}
			
			else if(next==XMLStreamReader.START_ELEMENT)
			{
				content = "";	

				Object elem = handler.createObject(parser, comment, context, stack);
				stack.add(new StackElement(parser.getLocalName(), elem));
				
				comment = null;
				
//				System.out.println("start: "+parser.getLocalName());
			}
			
			else if(next==XMLStreamReader.END_ELEMENT)
			{
//				System.out.println("end: "+parser.getLocalName());
				
				// Pop element from stack if there is one for the tag.
				StackElement se = (StackElement)stack.get(stack.size()-1);
				
				// Hack. Add content when it is element of its own.
				if(content.trim().length()>0 && se.getObject()==null)
				{
					StackElement tmp = new StackElement(parser.getLocalName(), content);
					stack.set(stack.size()-1, tmp);
					se = tmp;
					content = "";
				}
				
				if(se.getObject()!=null)
				{
					if(content.trim().length()>0)
					{
						handler.handleContent(parser, se.getObject(), content, context, stack);
						content = "";
					}
					
					if(stack.size()==1)
					{
						root = se.getObject();
					}
					else
					{
						StackElement	pse = (StackElement)stack.get(stack.size()-2);
						for(int i=stack.size()-3; i>=0 && pse.getObject()==null; i--)
						{
							pse = (StackElement)stack.get(i);
						}
						
						handler.linkObject(parser, se.getObject(), pse.getObject(), context, stack);
					}
				}
				
				stack.remove(stack.size()-1);
			}
		}
		parser.close();
		
		return root;
	}
}
