package jadex.commons.xml.writer;

import jadex.commons.xml.Namespace;
import jadex.commons.xml.StackElement;
import jadex.commons.xml.TypeInfo;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 *  XML writer for conversion of objects to XML.
 */
public class Writer
{
	//-------- static part --------
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The ID attribute constant. */
	public static final String ID = "__ID";
	
	/** The IDREF attribute constant. */
	public static final String IDREF = "__IDREF";
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectWriterHandler handler;
	
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	/** The id counter. */
	protected int id;
	
	/** Control flag for generating ids. */
	protected boolean genids;	
	
	/** Control flag for generating indention. */
	protected boolean indent;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler)
	{
		this.handler = handler;
		this.genids = true;
		this.indent = true;
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
		Map writtenobs = new HashMap();
		List stack = new ArrayList();
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
//		factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
		XMLStreamWriter	writer	= factory.createXMLStreamWriter(out);
		
		writer.writeStartDocument();//"utf-8", "1.0");
		writer.writeCharacters(lf);
		writeObject(writer, object, writtenobs, null, stack, context, classloader, null);
		writer.writeEndDocument();
		writer.close();
	}
	
	/**
	 *  Write an object to xml.
	 */
	public void writeObject(XMLStreamWriter writer, Object object, Map writtenobs, String tagname, 
		List stack, Object context, ClassLoader classloader, Namespace namespace) throws Exception
	{
//		if(tagname!=null)
//			System.out.println("tagname: "+tagname);
		TypeInfo typeinfo = handler.getTypeInfo(object, getXMLPath(stack), context); 
		if(typeinfo!=null)
		{
			tagname = typeinfo.getXMLTag();
			namespace = typeinfo.getNamespace();
		}
		
		if(tagname==null)
		{
			Object[] tn = handler.getTagName(object, context);
			namespace = (Namespace)tn[0];
			tagname = (String)tn[1];
		}
		
		if(genids && writtenobs.containsKey(object))
		{
			writeStartObject(writer, tagname, typeinfo!=null? typeinfo.getNamespace(): null, stack.size());
			writer.writeAttribute(IDREF, (String)writtenobs.get(object));
			writeEndObject(writer, 0);
		}
		else
		{
			// Check for cycle structures, which are not mappable without ids.
			if(writtenobs.containsKey(object))
			{
				boolean rec = false;
				for(int i=0; i<stack.size() && !rec; i++)
				{
					if(object.equals(((StackElement)stack.get(i)).getObject()))
						throw new RuntimeException("Object structure contains cycles: Enable 'genids' mode for serialization.");
				}
			}
			
			WriteObjectInfo wi = handler.getObjectWriteInfo(object, typeinfo, context, classloader);

			// Comment
			
			String comment = wi.getComment();
			if(comment!=null)
			{
				writeIndentation(writer, stack.size());
				writer.writeComment(comment);
				writer.writeCharacters(lf);
			}
			
			writeStartObject(writer, tagname, typeinfo!=null? typeinfo.getNamespace(): null, stack.size());
			
			StackElement topse = new StackElement(tagname, object);
			stack.add(topse);
			writtenobs.put(object, ""+id);
			if(genids)
				writer.writeAttribute(ID, ""+id);
			id++;
			
			// Attributes
			
			Map attrs = wi.getAttributes();
			if(attrs!=null)
			{
				for(Iterator it=attrs.keySet().iterator(); it.hasNext(); )
				{
					String propname = (String)it.next();
					String value = (String)attrs.get(propname);
					writer.writeAttribute(propname, value);
				}
			}
			
			if(wi.getContent()==null && (wi.getSubobjects()==null || wi.getSubobjects().size()==0))
			{
				writeEndObject(writer, 0);
			}
			else
			{
				// Content
				
				String content = wi.getContent();
				if(content!=null)
				{
					if(content.indexOf("<")!=-1 || content.indexOf(">")!=-1 || content.indexOf("&")!=-1)
						writer.writeCData(content);
					else
						writer.writeCharacters(content);
				}
				
				// Subobjects
				
				Map subobs = wi.getSubobjects();
				if(subobs==null || subobs.size()==0)
				{
					writeEndObject(writer, 0);
				}
				else
				{	
					writer.writeCharacters(lf);
					
					writeSubobjects(writer, subobs, writtenobs, stack, context, classloader, typeinfo);
					
					writeEndObject(writer, stack.size()-1);
				}
			}
			stack.remove(stack.size()-1);
		}
	}
	
	/**
	 *  Write the subobjects of an object.
	 */
	protected void writeSubobjects(XMLStreamWriter writer, Map subobs, Map writtenobs, 
		List stack, Object context, ClassLoader classloader, TypeInfo typeinfo) throws Exception
	{
		for(Iterator it=subobs.keySet().iterator(); it.hasNext(); )
		{
			String subtag = (String)it.next();
			if(WriteObjectInfo.SUBTAGMAP.equals(subtag))
				continue;
				
			Object subob = subobs.get(subtag);
			if(subob instanceof Map && ((Map)subob).containsKey(WriteObjectInfo.SUBTAGMAP))
			{		
				writeStartObject(writer, subtag, typeinfo!=null? typeinfo.getNamespace(): null, stack.size());
				writer.writeCharacters(lf);
				stack.add(new StackElement(subtag, null));
				
				writeSubobjects(writer, (Map)subob, writtenobs, stack, context, classloader, typeinfo);
				
				stack.remove(stack.size()-1);
				writeEndObject(writer, stack.size());
			}
			else if(subob instanceof List && ((List)subob).contains(WriteObjectInfo.SUBTAGMAP))
			{
				List sos = (List)subob;
				for(int i=0; i<sos.size(); i++)
				{
					Object so = sos.get(i);
					if(WriteObjectInfo.SUBTAGMAP.equals(so))
						continue;
					writeObject(writer, so, writtenobs, subtag, stack, context, classloader, null);
				}				
			}	
			else
			{
				writeObject(writer, subob, writtenobs, subtag, stack, context, classloader, null);
			}
		}
	}

	/**
	 *  Write the start of an object.
	 */
	public void writeStartObject(XMLStreamWriter writer, String name, Namespace ns, int level) throws Exception
	{
		writeIndentation(writer, level);
		
//		writer.writeStartElement(name);
		if(ns==null)
		{
			writer.writeStartElement(name);
		}
		else
		{
//			System.out.println("huhu: "+ns.getPrefix()+" "+ns.getURI()+" "+name);
			writer.writeStartElement(ns.getPrefix(), name, ns.getURI());
			writer.writeNamespace(ns.getPrefix(), ns.getURI());
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
	 *  Write content.
	 */
	public void writeContent(PrintWriter writer, String value)
	{
		writer.write(value);
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
	protected String[] getXMLPath(List stack)
	{
		String[] ret = new String[stack.size()];
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
		return new String(objectToByteArray(writer, val, classloader));
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Writer writer, Object val, ClassLoader classloader)
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(val, bos, classloader, null);
			byte[] ret = bos.toByteArray();
			bos.close();
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
