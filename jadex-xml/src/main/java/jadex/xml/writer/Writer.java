package jadex.xml.writer;

import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.xml.IPreProcessor;
import jadex.xml.SXML;
import jadex.xml.StackElement;
import jadex.xml.TypeInfo;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* $if !android $ */
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
/* $else $
import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.XMLOutputFactory;
import javaxx.xml.stream.XMLStreamWriter;
$endif $ */

/**
 *  XML writer for conversion of objects to XML.
 */
public class Writer
{
	//-------- static part --------
	
	/** Constant for indicating if public fields should be written. 
		The field has to be declared as public and its value
		will be used to determine if fields should be included. */
	public static final String XML_INCLUDE_FIELDS = "XML_INCLUDE_FIELDS";
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");

	/** The default encoding. */
	public static String DEFAULT_ENCODING = "utf-8";
	
	/** The xml output factory. */
	protected static final XMLOutputFactory	FACTORY = XMLOutputFactory.newInstance();
//	factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectWriterHandler handler;
	
	/** Control flag for generating ids. */
	protected boolean genids;	
	
	/** Control flag for generating indention. */
	protected boolean indent;
	
	//-------- constructors --------

	/**
	 *  Create a new reader (with genids=true and indent=true).
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler)
	{
		this(handler, true);
	}
	
	/**
	 *  Create a new reader (with genids=true and indent=true).
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler, boolean genids)
	{
		this(handler, genids, true);
	}
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler, boolean genids, boolean indent)
	{
		this.handler = handler;
		this.genids = genids;
		this.indent = indent;
	}
	
	//-------- methods --------
	
	/**
	 *  Write the properties to an xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public void write(Object object, OutputStream out, ClassLoader classloader, final Object context) throws Exception
	{
		write(object, DEFAULT_ENCODING, out, classloader, context);
	}
		
	/**
	 *  Write the properties to an xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public void write(Object object, String encoding, OutputStream out, ClassLoader classloader, final Object context) throws Exception
	{
		XMLStreamWriter	writer;
		synchronized(FACTORY)
		{
			writer	= FACTORY.createXMLStreamWriter(out, encoding);
		}
		
		writer.writeStartDocument(encoding, "1.0"); 
		writer.writeCharacters(lf);
		
		WriteContext wc = new WriteContext(writer, context, object, classloader);
		writeObject(wc, object, null);
		writer.writeEndDocument();
		writer.close();
	}
	
	/**
	 *  Write an object to xml.
	 *  
	 *  Note:
	 *  Must have tag parameter to support writing an object using an arbitrary tag.
	 *  Cannot write tag in beforehand, because it must be written in one pass. 
	 */
	public void writeObject(WriteContext wc, Object object, QName tag) throws Exception
	{
		XMLStreamWriter writer = wc.getWriter();
		List stack = wc.getStack();
		
		// Special case null
		if(object==null)
		{
//			writeStartObject(writer, tag==null? SXML.NULL: tag, stack.size());
			writeStartObject(writer, SXML.NULL, stack.size());
			writeEndObject(writer, stack.size());
			return;
		}
		
//		if(tagname!=null)
//			System.out.println("tagname: "+tagname);
//		if(object.getClass().getName().indexOf("ComponentIdentifier")!=-1)
//			System.out.println("cfs");
		
		TypeInfo typeinfo = handler.getTypeInfo(object, getXMLPath(stack), wc); 
		QName[] path = new QName[0];
		
		// Preprocess object.
		IPreProcessor[] preprocs = handler.getPreProcessors(object, typeinfo);
		if(preprocs!=null && preprocs.length>0)
		{
//			System.out.println("found: "+object);
			for(int i=0; i<preprocs.length; i++)
			{
				object = preprocs[i].preProcess(wc, object);
			}
		}

		// Only use typeinfo for getting tag (path) when not set in method call (subobject)
		// Generated tag names (that start with 'protocol typeinfo' are overruled by typeinfo spec.
		if((tag==null || tag.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO)) && typeinfo!=null)
//		if(typeinfo!=null)
		{
			tag = typeinfo.getXMLTag();
			if(typeinfo.getXMLInfo()!=null)
			{	
				path = typeinfo.getXMLInfo().getXMLPathElements();
				// Write path to tag
				for(int i=0; i+1<path.length; i++)
				{
					writeStartObject(writer, path[i], stack.size());
					stack.add(new StackElement(path[i], object));
					writer.writeCharacters(lf);
				}
			}
		}

		if(tag==null)
			tag = handler.getTagName(object, wc);
		
		// Create tag with prefix if it has a namespace but no prefix.
		if(!XMLConstants.NULL_NS_URI.equals(tag.getNamespaceURI()) && XMLConstants.DEFAULT_NS_PREFIX.equals(tag.getPrefix()))
		{
			tag = handler.getTagWithPrefix(tag, wc);
		}
		
		if(genids && wc.getWrittenObjects().containsKey(object))
		{
			writeStartObject(writer, tag, stack.size());
			writer.writeAttribute(SXML.IDREF, (String)wc.getWrittenObjects().get(object));
			writeEndObject(writer, 0);
		}
		else
		{
			// Check for cycle structures, which are not mappable without ids.
			if(wc.getWrittenObjects().containsKey(object))
			{
				boolean rec = false;
				for(int i=0; i<stack.size() && !rec; i++)
				{
					if(object.equals(((StackElement)stack.get(i)).getObject()))
						throw new RuntimeException("Object structure contains cycles: Enable 'genids' mode for serialization.");
				}
			}
			
			WriteObjectInfo wi = handler.getObjectWriteInfo(object, typeinfo, wc);

			// Comment
			
			String comment = wi.getComment();
			if(comment!=null)
			{
				writeIndentation(writer, stack.size());
				writer.writeComment(comment);
				writer.writeCharacters(lf);
			}
			
			writeStartObject(writer, tag, stack.size());
			
			int curid = wc.getId();
			StackElement topse = new StackElement(tag, object);
			stack.add(topse);
			wc.getWrittenObjects().put(object, ""+curid);
			if(genids)
				writer.writeAttribute(SXML.ID, ""+curid);
			wc.setId(curid+1);
			
			// Attributes
			
			Map attrs = wi.getAttributes();
			if(attrs!=null)
			{
				for(Iterator it=attrs.keySet().iterator(); it.hasNext(); )
				{
					Object propname = it.next();
					String value = (String)attrs.get(propname);
					if(propname instanceof String)
					{
						writer.writeAttribute((String)propname, value);
					}
					else if(propname instanceof QName)
					{
						QName attrname = (QName)propname;
						// Create tag with prefix if it has a namespace but no prefix.
						if(!XMLConstants.NULL_NS_URI.equals(attrname.getNamespaceURI()) && XMLConstants.DEFAULT_NS_PREFIX.equals(attrname.getPrefix()))
						{
							attrname = handler.getTagWithPrefix(tag, wc);
						}
						String uri = attrname.getNamespaceURI();
						String prefix = attrname.getPrefix();
						String localname = attrname.getLocalPart();
						
						if(!XMLConstants.NULL_NS_URI.equals(uri))
						{
							if(!prefix.equals(writer.getPrefix(uri)))
							{
								writer.writeAttribute(prefix, uri, localname, value);
								writer.writeNamespace(prefix, uri);
							}
							else
							{
								writer.writeAttribute(prefix, uri, localname, value);
							}
						}
						else
						{
							writer.writeAttribute(localname, value);
						}
						
						//		System.out.println("name"+tag.getLocalPart()+" prefix:"+prefix+" writerprefix:"+writer.getPrefix(uri)+" uri:"+uri);
					}
				}
			}
			
			// Content
			
			if(wi.getContent()!=null)
			{
				String content = wi.getContent();
				if(content!=null)
				{
					// Uses cdata when contains <, > or &
					// Must not use cdata when content is already cdata (nested cdata are not allowed)
					if((content.indexOf("<")!=-1 || content.indexOf(">")!=-1 || content.indexOf("&")!=-1)
						&& content.indexOf("<![CDATA[")==-1)
					{
						writer.writeCData(content);
					}
					else
					{
						writer.writeCharacters(content);
					}
				}
			}
			
			// Subobjects
			
			boolean subs = wi.getSubobjects()!=null && !wi.getSubobjects().isEmpty();
			if(subs)
			{
				Tree subobs = wi.getSubobjects();
				writer.writeCharacters(lf);
				
				writeSubobjects(wc, subobs.getRootNode(), typeinfo);
			}
			
			writeEndObject(writer, subs? stack.size()-1: 0);
			stack.remove(stack.size()-1);
			
			// Write path to tag.
			for(int i=0; i+1<path.length; i++)
			{
				writeEndObject(writer, stack.size()-1);
				stack.remove(stack.size()-1);
			}
		}
	}
	
	/**
	 *  Write the subobjects of an object.
	 */
	protected void writeSubobjects(WriteContext wc, TreeNode node, TypeInfo typeinfo) throws Exception
	{
		XMLStreamWriter writer = wc.getWriter();
		List stack = wc.getStack();
			
		List children = node.getChildren();
		for(int i=0; i<children.size(); i++)
		{
			TreeNode subnode = (TreeNode)children.get(i);
			Object tmp = subnode.getData();
			if(tmp instanceof QName)
			{
				QName subtag = (QName)tmp; 
				
				writeStartObject(writer, subtag, stack.size());
				writer.writeCharacters(lf);
				stack.add(new StackElement(subtag, null));
 
				writeSubobjects(wc, subnode, typeinfo);
						
				stack.remove(stack.size()-1);
				writeEndObject(writer, stack.size());
			}
			else
			{
				writeObject(wc, ((Object[])tmp)[1], (QName)((Object[])tmp)[0]);
//				writeObject(wc, ((Object[])tmp)[1]);
			}
		}
	}
	
	/**
	 *  Write the subobjects of an object.
	 * /
	protected void writeSubobjects(XMLStreamWriter writer, Map subobs, Map writtenobs, 
		List stack, Object context, ClassLoader classloader, TypeInfo typeinfo) throws Exception
	{
		for(Iterator it=subobs.keySet().iterator(); it.hasNext(); )
		{
			Object tmp = it.next();
//			if(WriteObjectInfo.INTERAL_STRUCTURE.equals(tmp))
//				continue;
				
			QName subtag = (QName)tmp;
			Object subob = subobs.get(subtag);
			if(subob instanceof Map)// && ((Map)subob).containsKey(WriteObjectInfo.INTERAL_STRUCTURE))
			{		
				writeStartObject(writer, subtag, stack.size());
				writer.writeCharacters(lf);
				stack.add(new StackElement(subtag, null));
				
				writeSubobjects(writer, (Map)subob, writtenobs, stack, context, classloader, typeinfo);
				
				stack.remove(stack.size()-1);
				writeEndObject(writer, stack.size());
			}
			else if(subob instanceof List)// && ((List)subob).contains(WriteObjectInfo.INTERAL_STRUCTURE))
			{
				writeStartObject(writer, subtag, stack.size());
				writer.writeCharacters(lf);
				stack.add(new StackElement(subtag, null));
				
				List sos = (List)subob;
				for(int i=0; i<sos.size(); i++)
				{
					Object so = sos.get(i);
//					if(WriteObjectInfo.INTERAL_STRUCTURE.equals(so))
//						continue;
					Object[] info = (Object[])so;
					writeObject(writer, info[1], writtenobs, (QName)info[0], stack, context, classloader);
				}			
				
				stack.remove(stack.size()-1);
				writeEndObject(writer, stack.size());
			}	
			else
			{
//				if(subob instanceof Map || subob instanceof List)
//					System.out.println("here");
				writeObject(writer, subob, writtenobs, subtag, stack, context, classloader);
			}
		}
	}*/

	/**
	 *  Write the start of an object.
	 */
	public void writeStartObject(XMLStreamWriter writer, QName tag, int level) throws Exception
	{
		writeIndentation(writer, level);
			
		String uri = tag.getNamespaceURI();
		String prefix = tag.getPrefix();
//		System.out.println("name"+tag.getLocalPart()+" prefix:"+prefix+" writerprefix:"+writer.getPrefix(uri)+" uri:"+uri);
		
		if(!XMLConstants.NULL_NS_URI.equals(uri))
		{
			if(!prefix.equals(writer.getPrefix(uri)))
			{
				writer.writeStartElement(tag.getPrefix(), tag.getLocalPart(), uri);
				writer.writeNamespace(tag.getPrefix(), tag.getNamespaceURI());
			}
			else
			{
				writer.writeStartElement(tag.getPrefix(), tag.getLocalPart(), uri);
			}
		}
		else
		{
			writer.writeStartElement(tag.getLocalPart());
		}
	}
	
	/**
	 *  Write the end of an object.
	 */
	public void writeEndObject(XMLStreamWriter writer, int level) throws Exception
	{
		writeIndentation(writer, level);
		writer.writeEndElement();
		writer.writeCharacters(lf);
	}
		
	/**
	 *  Write the indentation.
	 */
	public void writeIndentation(XMLStreamWriter writer, int level) throws Exception
	{
		if(indent)
		{
			for(int i=0; i<level; i++)
				writer.writeCharacters("\t");
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
		
//		StringBuffer ret = new StringBuffer();
//		for(int i=0; i<stack.size(); i++)
//		{
//			ret.append(((StackElement)stack.get(i)).getTag());
//			if(i<stack.size()-1)
//				ret.append("/");
//		}
//		return ret.toString();
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToXML(Writer writer, Object val, ClassLoader classloader)
	{
		try
		{
			return new String(objectToByteArray(writer, val, classloader), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Warning: no UTF-8 available");
			return new String(objectToByteArray(writer, val, classloader));
		}
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToXML(Writer writer, Object val, ClassLoader classloader, Object context)
	{
		try
		{
			return new String(objectToByteArray(writer, val, classloader, context), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Warning: no UTF-8 available");
			return new String(objectToByteArray(writer, val, classloader, context));
		}
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Writer writer, Object val, ClassLoader classloader)
	{
		return objectToByteArray(writer, val, classloader, null);
	}
	
	
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Writer writer, Object val, ClassLoader classloader, Object context)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(val, bos, classloader, context);
			byte[] ret = bos.toByteArray();
			bos.close();
			return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Write to output stream.
	 */
	public static void objectToOutputStream(Writer writer, Object val, OutputStream os, ClassLoader classloader, Object context)
	{
		try
		{
			writer.write(val, os, classloader, context);
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}
}
