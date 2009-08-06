package jadex.commons.xml.writer;

import jadex.commons.SReflect;
import jadex.commons.xml.AbstractInfo;
import jadex.commons.xml.StackElement;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanObjectWriterHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectWriterHandler handler;
	
	/** The type mappings. */
	protected Map typeinfos;
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	/** The id counter. */
	protected int id;
	
	/** Control flag for generating ids. */
	protected boolean genids;
	
	/** Control flag for generating container tags. */
	protected boolean gencontainertags;
	
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler, Set typeinfos)//, Set ignoredattrs)
	{
		this.handler = handler;
		this.typeinfos = typeinfos!=null? createTypeInfos(typeinfos): Collections.EMPTY_MAP;
//		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;
		this.genids = false;
		this.gencontainertags = true;
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
		XMLStreamWriter	writer	= factory.createXMLStreamWriter(out);
		writer.writeStartDocument();
		writer.writeCharacters(lf);
		writeObject(writer, object, writtenobs, null, stack, context, classloader);
		writer.writeEndDocument();
		writer.close();
	}
	
	/**
	 *  Write an object to xml.
	 */
	public void writeObject(XMLStreamWriter writer, Object object, Map writtenobs, String tagname, List stack, Object context, ClassLoader classloader) throws Exception
	{
		TypeInfo typeinfo = tagname!=null? getTypeInfo(object, getXMLPath(stack)+"/"+tagname, context, true):
			getTypeInfo(object, getXMLPath(stack), context, false); 
		if(typeinfo!=null)
			tagname = typeinfo.getXMLTag();
		else if(tagname.indexOf("/")!=-1)
			tagname = tagname.substring(tagname.lastIndexOf("/")+1);
		else if(tagname==null)
			tagname = object.getClass().getName();
		
		if(genids && writtenobs.containsKey(object))
		{
			writeStartObject(writer, tagname, stack.size());
			writer.writeAttribute("IDREF", (String)writtenobs.get(object));
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
				writeIdentation(writer, stack.size());
				writer.writeComment(comment);
				writer.writeCharacters(lf);
			}
			
			writeStartObject(writer, tagname, stack.size());
			
			StackElement topse = new StackElement(tagname, object);
			stack.add(topse);
			writtenobs.put(object, ""+id);
			if(genids)
				writer.writeAttribute("ID", ""+id);
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
					
					for(Iterator it=subobs.keySet().iterator(); it.hasNext(); )
					{
						String subpathname = (String)it.next();
						String subtagname = subpathname.indexOf("/")!=-1? subpathname.substring(subpathname.indexOf("/")): subpathname;
						Object obj =  subobs.get(subpathname);
						
						StringTokenizer stok = new StringTokenizer(subpathname, "/");
						String[] subtags = new String[stok.countTokens()-1];
						for(int i=0; i<subtags.length; i++)
							subtags[i] = stok.nextToken();
						
						if(gencontainertags)
						{
							for(int i=0; i<subtags.length; i++)
							{
								writeStartObject(writer, subtags[i], stack.size());
								writer.writeCharacters(lf);
								stack.add(new StackElement(subtags[i], null));
							}
						}
							
						if(SReflect.isIterable(obj))
						{
							// todo: container tags?!
							Iterator it2 = SReflect.getIterator(obj);
							if(it2.hasNext())
							{
								while(it2.hasNext())
								{
									writeObject(writer, it2.next(), writtenobs, subpathname, stack, context, classloader);
								}
							}
						}
						else
						{
							writeObject(writer, obj, writtenobs, subpathname, stack, context, classloader);
						}
						
						if(gencontainertags)
						{
							for(int i=0; i<subtags.length; i++)
							{
								stack.remove(stack.size()-1);
								writeEndObject(writer, stack.size());
							}
						}
					}
					writeEndObject(writer, stack.size()-1);
				}
			}
			
			stack.remove(stack.size()-1);
		}
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	protected TypeInfo getTypeInfo(Object object, String fullpath, Object context, boolean full)//, Map rawattributes)
	{
		TypeInfo ret = null;
		Set maps = (Set)typeinfos.get(handler.getObjectType(object, context));
		if(maps!=null)
		{
			for(Iterator it=maps.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo tmp = (TypeInfo)it.next();
				if(!full && fullpath.endsWith(tmp.getXMLPathWithoutElement()) ||
					(full && fullpath.endsWith(tmp.getXMLPath())))// && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
					ret = tmp;
			}
		}
		return ret;
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
			TreeSet maps = (TreeSet)ret.get(mapinfo.getTypeInfo());
			if(maps==null)
			{
				maps = new TreeSet(new AbstractInfo.SpecificityComparator());
				ret.put(mapinfo.getTypeInfo(), maps);
			}
			maps.add(mapinfo);
		}
		
		return ret;
	}
	
	/**
	 *  Write the start of an object.
	 */
	public void writeStartObject(XMLStreamWriter writer, String name, int level) throws Exception
	{
		writeIdentation(writer, level);
		writer.writeStartElement(name);
	}
	
	/**
	 *  Write the end of an object.
	 */
	public void writeEndObject(XMLStreamWriter writer, int level) throws Exception
	{
		writeIdentation(writer, level);
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
	 *  Write the identation.
	 */
	public void writeIdentation(XMLStreamWriter writer, int level) throws Exception
	{
		for(int i=0; i<level; i++)
			writer.writeCharacters("\t");
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
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		try
		{
			B b1 = new B("test b1");
			B b2 = new B("test b2");
			B b3 = new B("test b3");
			B b4 = new B("test b4");
			A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
			
	//		TypeInfo tia = new TypeInfo("a", A.class);
	//		TypeInfo tib = new TypeInfo("b", B.class);
			
			Writer w = new Writer(new BeanObjectWriterHandler(), null);
			
			w.write(a, System.out, null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static class A
	{
		protected int i;
		
		protected String s;
		
		protected B b;
		
		protected B[] bs;
		
		public A(int i, String s, B b, B[] bs)
		{
			this.i = i;
			this.s = s;
			this.b = b;
			this.bs = bs;
		}

		public int getI()
		{
			return this.i;
		}

		public void setI(int i)
		{
			this.i = i;
		}

		public String getS()
		{
			return this.s;
		}

		public void setS(String s)
		{
			this.s = s;
		}

		public B getB()
		{
			return this.b;
		}

		public void setB(B b)
		{
			this.b = b;
		}

		public B[] getBs()
		{
			return this.bs;
		}

		public void setBs(B[] bs)
		{
			this.bs = bs;
		}
	}
	
	public static class B
	{
		protected String str;

		public B(String str)
		{
			this.str = str;
		}
		
		public String getStr()
		{
			return this.str;
		}

		public void setStr(String str)
		{
			this.str = str;
		}
	}
}
