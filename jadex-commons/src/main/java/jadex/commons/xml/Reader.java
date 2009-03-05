package jadex.commons.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 *  Stax XML reader.
 */
public class Reader
{
	//-------- static part --------
	
	/** The debug flag. */
	public static boolean DEBUG = false;
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectHandler handler;
	
	/** The type mappings. */
	protected Map typeinfos;
	
	/** The link object infos. */
	protected Map linkinfos;
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Reader(IObjectHandler handler, Set typeinfos, Set linkinfos, Set ignoredattrs)
	{
		this.handler = handler;
		this.typeinfos = typeinfos!=null? createTypeInfos(typeinfos): Collections.EMPTY_MAP;
		this.linkinfos = linkinfos!=null? createLinkInfos(linkinfos): Collections.EMPTY_MAP;
		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;
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

				String fullpath = getXMLPath(stack)+"/"+parser.getLocalName();
				TypeInfo typeinfo = getTypeInfo(parser.getLocalName(), fullpath);
				
				// Create object.
				Object object = null;
				if(typeinfo!=null && typeinfo.getType()!=null)
				{
					object = handler.createObject(parser, typeinfo.getType(), stack.isEmpty(), context);
				}
				else
				{
					if(DEBUG)
						System.out.println("No mapping found: "+parser.getLocalName());
				}
				stack.add(new StackElement(parser.getLocalName(), object));
				
				// Handle attributes.
				if(parser.getAttributeCount()>0)
				{
					// If no type use last element from stack to map attributes.
					if(object==null)	
					{
						for(int i=stack.size()-1; i>=0 && object==null; i--)
						{
							object = ((StackElement)stack.get(i)).getObject();
						}
						
						if(object==null)
							throw new RuntimeException("No element on stack for attributes"+stack);
					}
					
					// Handle attributes
					for(int i=0; i<parser.getAttributeCount(); i++)
					{
						String attrname = parser.getAttributeLocalName(i);
						String attrval = parser.getAttributeValue(i);
						
						if(!ignoredattrs.contains(attrname))
						{
							Object attrinfo = typeinfo.getAttributeType(attrname);
							handler.handleAttributeValue(object, attrname, attrval, attrinfo, context);
						}
					}
				}
				
				// Handle comment.
				if(comment!=null && typeinfo!=null)
				{
					Object commentinfo = typeinfo.getComment();
					if(commentinfo!=null)
					{
						handler.handleAttributeValue(object, null, comment, commentinfo, context);
					}
				}
				
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
					TypeInfo typeinfo = getTypeInfo(parser.getLocalName(), getXMLPath(stack));

					if(content.trim().length()>0)
					{
						if(typeinfo.getContent()!=null) 
						{
							handler.handleAttributeValue(se.getObject(), null, content, typeinfo.getContent(), context);
						}
						else
						{
							throw new RuntimeException("No content mapping for: "+stack);
						}
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
												
						// Handle postprocessing
						IPostProcessor postprocessor = typeinfo.getPostProcessor();
						if(postprocessor!=null)
						{
							postprocessor.postProcess(context, se.getObject(), ((StackElement)stack.get(0)).getObject());
						}
						
						// Handle linking
						LinkInfo linkinfo = getLinkInfo(parser.getLocalName(), getXMLPath(stack));
						handler.linkObject(se.getObject(), pse.getObject(), linkinfo==null? null: linkinfo.getLinkAttribute(), parser.getLocalName(), context);
					}
				}
				
				stack.remove(stack.size()-1);
			}
		}
		parser.close();
		
		return root;
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	protected TypeInfo getTypeInfo(String tag, String fullpath)
	{
		TypeInfo ret = null;
		Set maps = (Set)typeinfos.get(tag);
		if(maps!=null)
		{
			for(Iterator it=maps.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo tmp = (TypeInfo)it.next();
				if(fullpath.endsWith(tmp.getXMLPath()))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the most specific link info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific link info.
	 */
	protected LinkInfo getLinkInfo(String tag, String fullpath)
	{
		LinkInfo ret = null;
		Set links = (Set)linkinfos.get(tag);
		if(links!=null)
		{
			for(Iterator it=links.iterator(); ret==null && it.hasNext(); )
			{
				LinkInfo tmp = (LinkInfo)it.next();
				if(fullpath.endsWith(tmp.getXMLPath()))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	protected String getXMLPath(List stack)
	{
		StringBuffer ret = new StringBuffer();
		for(int i=0; i<stack.size(); i++)
		{
			ret.append(((StackElement)stack.get(i)).getTag());
			if(i<stack.size()-1)
				ret.append("/");
		}
		return ret.toString();
	}
	
	/**
	 *  Create type infos for each tag sorted by specificity.
	 *  @param linkinfos The mapping infos.
	 *  @return Map of mapping infos.
	 */
	protected Map createTypeInfos(Set typeinfos)
	{
		Map ret = new HashMap();
		
		for(Iterator it=typeinfos.iterator(); it.hasNext(); )
		{
			TypeInfo mapinfo = (TypeInfo)it.next();
			TreeSet maps = (TreeSet)ret.get(mapinfo.getXMLTag());
			if(maps==null)
			{
				maps = new TreeSet(new AbstractInfo.SpecificityComparator());
				ret.put(mapinfo.getXMLTag(), maps);
			}
			maps.add(mapinfo);
		}
		
		return ret;
	}
	
	/**
	 *  Create link infos for each tag sorted by specificity.
	 *  @param linkinfos The link infos.
	 *  @return Map of link infos.
	 */
	protected Map createLinkInfos(Set linkinfos)
	{
		Map ret = new HashMap();
		
		for(Iterator it=linkinfos.iterator(); it.hasNext(); )
		{
			LinkInfo linkinfo = (LinkInfo)it.next();
			TreeSet links = (TreeSet)ret.get(linkinfo.getXMLTag());
			if(links==null)
			{
				links = new TreeSet(new AbstractInfo.SpecificityComparator());
				ret.put(linkinfo.getXMLTag(), links);
			}
			links.add(linkinfo);
		}
		
		return ret;
	}
}
