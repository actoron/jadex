package jadex.commons.xml.reader;

import jadex.commons.collection.MultiCollection;
import jadex.commons.xml.AbstractInfo;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.IPostProcessor;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.StackElement;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;

import java.io.ByteArrayInputStream;
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
	
	/** The ID attribute constant. */
	public static final String ID = "__ID";
	
	/** The IDREF attribute constant. */
	public static final String IDREF = "__IDREF";
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectReaderHandler handler;
	
	/** The type mappings. */
	protected Map typeinfos;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Reader(IObjectReaderHandler handler, Set typeinfos)
	{
		this.handler = handler;
		this.typeinfos = typeinfos!=null? createTypeInfos(typeinfos): Collections.EMPTY_MAP;
	}
	
	//-------- methods --------
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public Object read(InputStream input, final ClassLoader classloader, final Object context) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		Object root = null;
		List stack = new ArrayList();
		List path = new ArrayList();
		StackElement topse	= null;
		String comment = null;
		MultiCollection postprocs = new MultiCollection();
		Map readobjects = new HashMap();
		
		while(parser.hasNext())
		{
			int	next = parser.next();
			
			if(next==XMLStreamReader.COMMENT)
			{
				comment = parser.getText();
//				System.out.println("Found comment: "+comment);
			}
			
			else if(next==XMLStreamReader.CHARACTERS || next==XMLStreamReader.CDATA)
			{
				topse.addContent(parser.getText()); 				
//				System.out.println("content: "+parser.getLocalName()+" "+content);
			}
			
			else if(next==XMLStreamReader.START_ELEMENT)
			{				
				// Fetch for info when creating attributes.
				Map rawattrs = null;
				if(parser.getAttributeCount()>0)
				{
					rawattrs = new HashMap();
					for(int i=0; i<parser.getAttributeCount(); i++)
					{
						String attrname = parser.getAttributeLocalName(i);
						String attrval = parser.getAttributeValue(i);
						rawattrs.put(attrname, attrval);
					}
				}
				
				Object object = null;
				
//				String[] fullpath = getXMLPath(stack, parser.getLocalName());
				String[] fullpath = (String[])path.toArray(new String[path.size()+1]);
				fullpath[fullpath.length-1] = parser.getLocalName();
				TypeInfo typeinfo = getTypeInfo(parser.getLocalName(), fullpath, rawattrs);

				
				// Test if it is an object reference
				String idref = rawattrs!=null? (String)rawattrs.get(IDREF): null;
				if(idref!=null)
				{
					object = readobjects.get(idref);
					topse	= new StackElement(parser.getLocalName(), object, rawattrs, typeinfo);
					stack.add(topse);
					path.add(parser.getLocalName());
				}
				else
				{	
					// Create object.
					// todo: do not call createObject on every tag?!
					object = handler.createObject(typeinfo!=null? typeinfo: parser.getLocalName(), stack.isEmpty(), context, rawattrs, classloader);
					if(DEBUG && object==null)
						System.out.println("No mapping found: "+parser.getLocalName());
					
					// If object has internal id save it in the readobjects map.
					String id = rawattrs!=null? (String)rawattrs.get(ID): null;
					if(id!=null && object!=null)
					{
						readobjects.put(id, object);
					}
					
					topse	= new StackElement(parser.getLocalName(), object, rawattrs, typeinfo);
					stack.add(topse);
					path.add(parser.getLocalName());
					if(stack.size()==1)
					{
						root = object;
					}
				
					// Handle attributes.
					if(parser.getAttributeCount()>0 && 
						!(parser.getAttributeCount()==1 && rawattrs.get(ID)!=null))
					{
						List	attrpath	= null;
						// If no type use last element from stack to map attributes.
						if(object==null)	
						{
							attrpath = new ArrayList();
							attrpath.add(topse.getTag());
							for(int i=stack.size()-2; i>=0 && object==null; i--)
							{
								StackElement	pse	= (StackElement)stack.get(i);
								attrpath.add(pse.getTag());
								object = pse.getObject();
							}
							
							if(object==null)
								throw new RuntimeException("No element on stack for attributes"+stack);
						}
						
						// Handle attributes
						Set attrs = typeinfo==null? Collections.EMPTY_SET: typeinfo.getXMLAttributeNames();
						for(int i=0; i<parser.getAttributeCount(); i++)
						{
							String attrname = parser.getAttributeLocalName(i);
							if(!attrname.equals(ID))
							{	
								String attrval = parser.getAttributeValue(i);
								attrs.remove(attrname);
								
								Object attrinfo = typeinfo!=null ? typeinfo.getAttributeInfo(attrname) : null;
								if(!(attrinfo instanceof AttributeInfo && ((AttributeInfo)attrinfo).isIgnoreRead()))
								{
		//							ITypeConverter attrconverter = typeinfo!=null ? typeinfo.getAttributeConverter(attrname) : null;
		//							Object val = attrconverter!=null? attrconverter.convertObject(attrval, root, classloader): attrval;
									handler.handleAttributeValue(object, attrname, attrpath, attrval, attrinfo, context, classloader, root);
								}
							}
						}
						// Handle unset attributes (possibly have default value).
						for(Iterator it=attrs.iterator(); it.hasNext(); )
						{
							String attrname = (String)it.next();
							Object attrinfo = typeinfo.getAttributeInfo(attrname);
							
							// Hack. want to read attribute info here
							handler.handleAttributeValue(object, attrname, attrpath, null, attrinfo, context, classloader, root);
						}
					}
					
					// Handle comment.
					if(comment!=null && typeinfo!=null)
					{
						Object commentinfo = typeinfo.getCommentInfo();
						if(commentinfo!=null)
						{
							handler.handleAttributeValue(object, null, null, comment, commentinfo, context, classloader, root);
						}
					}
				}
				
				comment = null;
				
//				System.out.println("start: "+parser.getLocalName());
			}
			
			else if(next==XMLStreamReader.END_ELEMENT)
			{
//				System.out.println("end: "+parser.getLocalName());
				String[] fullpath = (String[])path.toArray(new String[path.size()]);
				final TypeInfo typeinfo = getTypeInfo(parser.getLocalName(), fullpath, topse.getRawAttributes());

				// Hack. Change object to content when it is element of its own.
				if(topse.getContent()!=null && topse.getContent().trim().length()>0 && topse.getObject()==null)
				{
					// Handle possible content type conversion.
					Object val = topse.getContent();
					if(typeinfo!=null && typeinfo.getContentInfo()!=null)
					{
						Object coninfo = typeinfo.getContentInfo();
						if(coninfo!=null && coninfo instanceof AttributeInfo)
						{
							ITypeConverter conv = ((AttributeInfo)coninfo).getConverterRead();
							if(conv!=null)
								val = conv.convertObject(val, root, classloader, context);
						}
					}
					else
					{
						String tagname = parser.getLocalName();
						val = handler.convertContentObject(val, tagname, context, classloader);
					}
					
					topse = new StackElement(topse.getTag(), val, topse.getRawAttributes());
					stack.set(stack.size()-1, topse);
					// If this is the only element on stack, set also root to it
					if(stack.size()==1)
						root = topse.getObject();
				}
				
				// Link current object to parent
				if(topse.getObject()!=null)
				{					
					// Handle content.
					if(topse.getContent()!=null && topse.getContent().trim().length()>0)
					{
						if(typeinfo!=null && typeinfo.getContentInfo()!=null) 
						{
							handler.handleAttributeValue(topse.getObject(), null, null, topse.getContent(), typeinfo.getContentInfo(), context, classloader, root);
						}
						else
						{
							throw new RuntimeException("No content mapping for: "+topse.getContent()+stack);
						}
					}
					
					// Handle post-processing
					
					if(typeinfo!=null && typeinfo.getPostProcessor()!=null)
					{
						final IPostProcessor postproc = typeinfo.getPostProcessor();
						if(postproc.getPass()==1)
						{
//							topse.object = 
							typeinfo.getPostProcessor().postProcess(context, topse.getObject(), root, classloader);
						}
						else
						{
							final Object object = topse.getObject();
							final Object ro = root;
							postprocs.put(new Integer(postproc.getPass()), new Runnable()
							{
								public void run()
								{
									postproc.postProcess(context, object, ro, classloader);
								}
							});
						}
					}

					// Handle linking
					if(stack.size()>1)
					{
						StackElement pse = (StackElement)stack.get(stack.size()-2);
						List pathname = new ArrayList();
						pathname.add(parser.getLocalName());
						for(int i=stack.size()-3; i>=0 && pse.getObject()==null; i--)
						{
							pse = (StackElement)stack.get(i);
							pathname.add(0, ((StackElement)stack.get(i+1)).getTag());
						}
//						System.out.println("here: "+parser.getLocalName()+" "+getXMLPath(stack)+" "+topse.getRawAttributes());
						
						TypeInfo patypeinfo = pse.getTypeInfo();
						SubobjectInfo linkinfo = null;
						if(patypeinfo!=null)
							linkinfo = patypeinfo.getSubobjectInfoRead(parser.getLocalName(), fullpath, topse.getRawAttributes());
						handler.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo.getLinkInfo(), 
							(String[])pathname.toArray(new String[pathname.size()]), context, classloader, root);
					}
				}
				
				stack.remove(stack.size()-1);
				path.remove(path.size()-1);
				if(stack.size()>0)
					topse	= (StackElement)stack.get(stack.size()-1);
				else
					topse	= null;
			}
		}
		parser.close();
		
		// Handle post-processors.
		for(int i=2; postprocs.size()>0; i++)
		{
			List ps = (List)postprocs.remove(new Integer(i));
			if(ps!=null)
			{
				for(int j=0; j<ps.size(); j++)
				{
					((Runnable)ps.get(j)).run();
				}
			}
		}
			
		return root;
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	protected TypeInfo getTypeInfo(String tag, String[] fullpath, Map rawattributes)
	{
		TypeInfo ret = findTypeInfo((Set)typeinfos.get(tag), fullpath);
		return ret;
	}
	
	/**
	 *  Find type find in the set of type infos.
	 */
	protected TypeInfo findTypeInfo(Set typeinfos, String[] fullpath)
	{
		TypeInfo ret = null;
		if(typeinfos!=null)
		{
			for(Iterator it=typeinfos.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo ti = (TypeInfo)it.next();
				String[] tmp = ti.getXMLPathElements();
				boolean ok = tmp==null || tmp.length<=fullpath.length;;
				if(tmp!=null)
				{
					for(int i=1; i<=tmp.length && ok; i++)
					{
						ok = tmp[tmp.length-i].equals(fullpath[fullpath.length-i]);
					}
				}
				if(ok)
					ret = ti;
//				if(fullpath.endsWith(tmp.getXMLPathWithoutElement())) // && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
			}
		}
		return ret;
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 * /
	protected String[] getXMLPath(List stack)
	{
//		String[] ret = new String[stack.size()];
//		for(int i=0; i<stack.size(); i++)
//		{
//			ret[i] = ((StackElement)stack.get(i)).getTag();
//		}
//		return ret;
	}*/
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 * /
	protected String[] getXMLPath(List stack, String tag)
	{
//		String[] ret = new String[stack.size()+1];
//		for(int i=0; i<stack.size(); i++)
//		{
//			ret[i] = ((StackElement)stack.get(i)).getTag();
//		}
//		ret[ret.length-1] = tag;
//		return ret;
		
//		StringBuffer ret = new StringBuffer();
//		for(int i=0; i<stack.size(); i++)
//		{
//			ret.append(((StackElement)stack.get(i)).getTag());
//			if(i<stack.size()-1)
//				ret.append("/");
//		}
//		return ret.toString();
	}*/
	
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
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromXML(Reader reader, String val, ClassLoader classloader)
	{
		return objectFromByteArray(reader, val.getBytes(), classloader);
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromByteArray(Reader reader, byte[] val, ClassLoader classloader)
	{
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(val);
			Object ret = reader.read(bis, classloader, null);
			bis.close();
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
}
