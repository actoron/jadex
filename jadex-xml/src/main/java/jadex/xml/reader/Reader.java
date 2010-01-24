package jadex.xml.reader;

import jadex.commons.SUtil;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.ITypeConverter;
import jadex.xml.SXML;
import jadex.xml.StackElement;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 *  Stax XML reader.
 */
public class Reader
{
	//-------- constants --------
	
	/** The debug flag. */
	public static boolean DEBUG = false;
	
	/** The string marker object. */
	public static final Object STRING_MARKER = new String();

	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectReaderHandler handler;
		
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Reader(IObjectReaderHandler handler)
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
	public Object read(InputStream input, final ClassLoader classloader, final Object callcontext) throws Exception
	{
		XMLInputFactory	factory	= XMLInputFactory.newInstance();
		XMLStreamReader	parser	= factory.createXMLStreamReader(input);
		ReadContext readcontext = new ReadContext(parser, callcontext, classloader);
		
		while(parser.hasNext())
		{
			int	next = parser.next();
			
			if(next==XMLStreamReader.COMMENT)
			{
				handleComment(readcontext);
			}
			else if(next==XMLStreamReader.CHARACTERS || next==XMLStreamReader.CDATA)
			{
				handleContent(readcontext);
			}
			else if(next==XMLStreamReader.START_ELEMENT)
			{	
				handleStartElement(readcontext);
			}
			else if(next==XMLStreamReader.END_ELEMENT)
			{
				handleEndElement(readcontext);
			}
		}
		parser.close();
		
		// Handle post-processors.
		for(int i=1; readcontext.getPostProcessors().size()>0; i++)
		{
			List ps = (List)readcontext.getPostProcessors().remove(new Integer(i));
			if(ps!=null)
			{
				for(int j=0; j<ps.size(); j++)
				{
					((Runnable)ps.get(j)).run();
				}
			}
//			System.out.println("i: "+i);
		}
			
		return readcontext.root;
	}
	
	/**
	 *  Handle the comment.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleComment(ReadContext readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
			readcontext.setComment(readcontext.getParser().getText());
		else if(DEBUG)
			System.out.println("Ignoring: "+readcontext.getParser().getText());
		//	System.out.println("Found comment: "+comment);
	}

	/**
	 *  Handle the content.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleContent(ReadContext readcontext) throws Exception
	{
		if(readcontext.getReadIgnore()==0)
			readcontext.getTopStackElement().addContent(readcontext.getParser().getText()); 
		else if(DEBUG)
			System.out.println("Ignoring: "+readcontext.getParser().getText());
//		System.out.println("content: "+parser.getLocalName()+" "+content);
	}
	
	/**
	 *  Handle the start element.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleStartElement(ReadContext readcontext) throws Exception
	{
		int readignore = readcontext.getReadIgnore();
		XMLStreamReader parser = readcontext.getParser();
		
		if(readignore>0)
		{
			readcontext.setReadIgnore(readignore++);
			if(DEBUG)
				System.out.println("Ignoring: "+parser.getLocalName());
		}
		else if(readignore==0)
		{
			List stack = readcontext.getStack();

			// Fetch for info when creating attributes.
			Map rawattrs = null;
			int attrcnt = parser.getAttributeCount();
			if(attrcnt>0)
			{
				rawattrs = new HashMap();
				for(int i=0; i<attrcnt; i++)
				{
					String attrname = parser.getAttributeLocalName(i);
					String attrval = parser.getAttributeValue(i);
					rawattrs.put(attrname, attrval);
				}
			}
			
			Object object = null;
			QName localname = parser.getPrefix()==null || parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getLocalName())
				: new QName(parser.getNamespaceURI(), parser.getLocalName(), parser.getPrefix());
			
			QName[] fullpath = getXMLPath(stack, localname);
			
			TypeInfo typeinfo = handler.getTypeInfo(localname, fullpath, rawattrs);
			
			// Find out if we need to ignore. 
			if(stack.size()>0)
			{
				StackElement pse = (StackElement)stack.get(stack.size()-1);
				List pathname = new ArrayList();
				pathname.add(localname);
				for(int i=stack.size()-2; i>=0 && pse.getObject()==null; i--)
				{
					pse = (StackElement)stack.get(i);
					pathname.add(0, ((StackElement)stack.get(i+1)).getTag());
				}
				
				if(pse!=null)
				{
					TypeInfo patypeinfo = ((StackElement)pse).getTypeInfo();
					SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, 
						readcontext.getTopStackElement()!=null? readcontext.getTopStackElement().getRawAttributes(): null);
					if(linkinfo!=null && linkinfo.getLinkInfo().isIgnoreRead())
					{
						readignore++;
						if(DEBUG)
							System.out.println("Ignoring: "+parser.getLocalName());
					}
				}
			}
			
			if(readignore==0)
			{
				// Test if it is an object reference
				String idref = rawattrs!=null? (String)rawattrs.get(SXML.IDREF): null;
				if(idref!=null)
				{
					if(!readcontext.getReadObjects().containsKey(idref))
						throw new RuntimeException("idref not contained: "+idref);
					object = readcontext.getReadObjects().get(idref);
					StackElement se = new StackElement(localname, object, rawattrs, typeinfo);
//					readcontext.setTopse(se);
					stack.add(se);
				}
				else
				{	
					// Create object.
					// todo: do not call createObject on every tag?!
					Object ti = typeinfo;
					if(localname.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO)
						&& (typeinfo==null || typeinfo.isCreateFromTag()))
					{
						ti = localname;
					}
					object = handler.createObject(ti, readcontext.getStack().isEmpty(), 
						readcontext.getCallContext(), rawattrs, readcontext.getClassLoader());
					if(DEBUG && object==null)
						System.out.println("No mapping found: "+localname);
					
					// Try to search type info via type (when tag contained type information)
					if(typeinfo==null && object!=null)
					{
						typeinfo = handler.getTypeInfo(object, fullpath, readcontext.getCallContext());
					}
					
					// If object has internal id save it in the readobjects map.
					String id = rawattrs!=null? (String)rawattrs.get(SXML.ID): null;
					if(id!=null && object!=null)
					{
//						System.out.println("ID: "+id+", "+object.getClass());
						readcontext.getReadObjects().put(id, object);
					}
					
					stack.add(new StackElement(localname, object, rawattrs, typeinfo));
					if(stack.size()==1)
					{
						readcontext.setRoot(object);
					}
				
					// Handle attributes.
					if(attrcnt>0 && !(attrcnt==1 && rawattrs.get(SXML.ID)!=null))
					{
						List	attrpath	= null;
						// If no type use last element from stack to map attributes.
						if(object==null)	
						{
							attrpath = new ArrayList();
							attrpath.add(readcontext.getTopStackElement().getTag());
							for(int i=stack.size()-2; i>=0 && object==null; i--)
							{
								StackElement pse = (StackElement)stack.get(i);
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
							QName attrname = parser.getAttributePrefix(i)==null || parser.getAttributePrefix(i)==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getAttributeLocalName(i))
								: new QName(parser.getAttributeNamespace(i), parser.getAttributeLocalName(i), parser.getAttributePrefix(i));

//							System.out.println("here: "+attrname);
							
							if(!attrname.getLocalPart().equals(SXML.ID))
							{	
								String attrval = parser.getAttributeValue(i);
								attrs.remove(attrname);
								
								Object attrinfo = typeinfo!=null ? typeinfo.getAttributeInfo(attrname) : null;
								if(!(attrinfo instanceof AttributeInfo && ((AttributeInfo)attrinfo).isIgnoreRead()))
								{
		//							ITypeConverter attrconverter = typeinfo!=null ? typeinfo.getAttributeConverter(attrname) : null;
		//							Object val = attrconverter!=null? attrconverter.convertObject(attrval, root, classloader): attrval;
									
									handler.handleAttributeValue(object, attrname, attrpath, attrval, attrinfo, 
										readcontext.getCallContext(), readcontext.getClassLoader(), readcontext.getRoot(), readcontext.getReadObjects());
								
									if(attrinfo instanceof AttributeInfo && AttributeInfo.ID.equals(((AttributeInfo)attrinfo).getId()))
									{
//										System.out.println("ID: "+attrval+", "+object);
										readcontext.getReadObjects().put(rawattrs.get(attrval), object);
									}
								}
							}
						}
						// Handle unset attributes (possibly have default value).
						for(Iterator it=attrs.iterator(); it.hasNext(); )
						{
							QName attrname = (QName)it.next();
							Object attrinfo = typeinfo.getAttributeInfo(attrname);
							
							// Hack. want to read attribute info here
							handler.handleAttributeValue(object, attrname, attrpath, null, attrinfo, 
								readcontext.getCallContext(), readcontext.getClassLoader(), readcontext.getRoot(), readcontext.getReadObjects());
						}
					}
					
					// Handle comment.
					if(readcontext.getComment()!=null && typeinfo!=null)
					{
						Object commentinfo = typeinfo.getCommentInfo();
						if(commentinfo!=null)
						{
							handler.handleAttributeValue(object, null, null, readcontext.getComment(), commentinfo, 
								readcontext.getCallContext(), readcontext.getClassLoader(), readcontext.getRoot(), readcontext.getReadObjects());
						}
					}
				}
			}
			
			readcontext.setComment(null);
			
//			System.out.println("start: "+parser.getLocalName());
		}
	}
	
	/**
	 *  Handle the end element.
	 *  @param readcontext The context for reading with all necessary information.
	 */
	protected void handleEndElement(final ReadContext readcontext) throws Exception
	{
		int readignore = readcontext.getReadIgnore();
		
		if(readignore==0)
		{
			XMLStreamReader parser = readcontext.getParser();
			List stack = readcontext.getStack();
			StackElement topse = readcontext.getTopStackElement();
			
	//		System.out.println("end: "+parser.getLocalName());
			QName localname = parser.getPrefix()==null || parser.getPrefix()==XMLConstants.DEFAULT_NS_PREFIX? new QName(parser.getLocalName())
				: new QName(parser.getNamespaceURI(), parser.getLocalName(), parser.getPrefix());
			QName[] fullpath = getXMLPath(stack);
			final TypeInfo typeinfo = handler.getTypeInfo(localname, fullpath, topse.getRawAttributes());
	
			// Hack. Change object to content when it is element of its own.
			if((topse.getObject()==null && topse.getContent()!=null && topse.getContent().trim().length()>0) || topse.getObject()==STRING_MARKER)
			{
				// Handle possible content type conversion.
				Object val = topse.getContent()!=null? topse.getContent(): topse.getObject();
				if(typeinfo!=null && typeinfo.getContentInfo()!=null)
				{
					Object coninfo = typeinfo.getContentInfo();
					if(coninfo!=null && coninfo instanceof AttributeInfo)
					{
						ITypeConverter conv = ((AttributeInfo)coninfo).getConverterRead();
						if(conv!=null)
						{
							val = conv.convertObject(val, readcontext.getRoot(), readcontext.getClassLoader(), 
								readcontext.getCallContext());
						}
					}
				}
				else
				{
					val = handler.convertContentObject(val, localname, readcontext.getCallContext(), readcontext.getClassLoader());
				}
				
				topse = new StackElement(topse.getTag(), val, topse.getRawAttributes());
				stack.set(stack.size()-1, topse);
//				readcontext.setTopse(topse);
				// If this is the only element on stack, set also root to it
				if(stack.size()==1)
					readcontext.setRoot(topse.getObject());
	
				// If object has internal id save it in the readobjects map.
				String id = topse.getRawAttributes()!=null? (String)topse.getRawAttributes().get(SXML.ID): null;
				if(id!=null && val!=null)
				{
	//				System.out.println("ID: "+id+", "+val.getClass());
					readcontext.getReadObjects().put(id, val);
				}	
			}
			
			// Link current object to parent
			if(topse.getObject()!=null)
			{					
				// Handle content.
				if(topse.getContent()!=null && topse.getContent().trim().length()>0)
				{
					if(typeinfo!=null && typeinfo.getContentInfo()!=null) 
					{
						handler.handleAttributeValue(topse.getObject(), null, null, topse.getContent(), typeinfo.getContentInfo(), 
							readcontext.getCallContext(), readcontext.getClassLoader(), readcontext.getRoot(), readcontext.getReadObjects());
					}
					else
					{
						throw new RuntimeException("No content mapping for: "+topse.getContent()+" tag="+topse.getTag());
					}
				}
				
				// Handle post-processing
				
				if(typeinfo!=null && typeinfo.getPostProcessor()!=null)
				{
					final IPostProcessor postproc = typeinfo.getPostProcessor();
					if(postproc.getPass()==0)
					{
						Object changed = typeinfo.getPostProcessor().postProcess(readcontext.getCallContext(), topse.getObject(), 
							readcontext.getRoot(), readcontext.getClassLoader());
						if(changed!=null)
							topse.setObject(changed);
					}
					else
					{
						final Object object = topse.getObject();
						final Object ro = readcontext.getRoot();
						readcontext.getPostProcessors().put(new Integer(postproc.getPass()), new Runnable()
						{
							public void run()
							{
								Object check = postproc.postProcess(readcontext.getCallContext(), object, ro, readcontext.getClassLoader());
								if(check!=null)
									throw new RuntimeException("Object replacement only possible in first pass.");
							}
						});
					}
				}
	
				// Handle linking
				if(stack.size()>1)
				{
	//				Object[] tmp = getLastStackElementWithObject(stack, localname);
					StackElement pse = (StackElement)stack.get(stack.size()-2);
					List pathname = new ArrayList();
					pathname.add(localname);
					for(int i=stack.size()-3; i>=0 && pse.getObject()==null; i--)
					{
						pse = (StackElement)stack.get(i);
						pathname.add(0, ((StackElement)stack.get(i+1)).getTag());
					}
					
					if(pse.getObject()==null)
						throw new RuntimeException("No parent object found for: "+SUtil.arrayToString(fullpath));
					
	//						System.out.println("here: "+parser.getLocalName()+" "+getXMLPath(stack)+" "+topse.getRawAttributes());
					
					TypeInfo patypeinfo = pse.getTypeInfo();
					SubobjectInfo linkinfo = getSubobjectInfoRead(localname, fullpath, patypeinfo, topse.getRawAttributes());
					
					handler.linkObject(topse.getObject(), pse.getObject(), linkinfo==null? null: linkinfo.getLinkInfo(), 
						(QName[])pathname.toArray(new QName[pathname.size()]), 
						readcontext.getCallContext(), readcontext.getClassLoader(), readcontext.getRoot());
				}
			}
			
			stack.remove(stack.size()-1);
			if(stack.size()>0)
				topse = (StackElement)stack.get(stack.size()-1);
			else
				topse = null;
		}
		else
		{
			readcontext.setReadIgnore(readignore--);
		}
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	protected QName[] getXMLPath(List stack)
	{
		QName[] ret = new QName[stack.size()];
		for(int i=0; i<stack.size(); i++)
		{
			ret[i] = ((StackElement)stack.get(i)).getTag();
		}
		return ret;
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	protected QName[] getXMLPath(List stack, QName tag)
	{
		QName[] ret = new QName[stack.size()+1];
		for(int i=0; i<stack.size(); i++)
		{
			ret[i] = ((StackElement)stack.get(i)).getTag();
		}
		ret[ret.length-1] = tag;
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
			System.out.println("problem: "+new String(val));
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * /
	public static Object[] getLastStackElementWithObject(List stack, QName localname)
	{
		StackElement pse = (StackElement)stack.get(stack.size()-2);
		List pathname = new ArrayList();
		pathname.add(localname);
		for(int i=stack.size()-3; i>=0 && pse.getObject()==null; i--)
		{
			pse = (StackElement)stack.get(i);
			pathname.add(0, ((StackElement)stack.get(i+1)).getTag());
		}
		return new Object[]{pse, pathname};
	}*/
	
	/**
	 * 
	 */
	public static SubobjectInfo getSubobjectInfoRead(QName localname, QName[] fullpath, TypeInfo patypeinfo, Map attrs)
	{
		SubobjectInfo ret = null;
		if(patypeinfo!=null)
		{
			QName tag = localname;
			QName[] fpath = fullpath;
			// Hack! If localname is classname remove it
			if(localname.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO))
			{
				tag = fullpath[fullpath.length-2];
				fpath = new QName[fullpath.length-1];
				System.arraycopy(fullpath, 0, fpath, 0, fpath.length);
			}
			ret = patypeinfo.getSubobjectInfoRead(tag, fpath, attrs);
		}
		return ret;
	}
}
