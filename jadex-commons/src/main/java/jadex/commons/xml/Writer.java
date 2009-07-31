package jadex.commons.xml;

import jadex.commons.SReflect;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public Writer(IObjectWriterHandler handler, Set typeinfos, Set ignoredattrs)
	{
		this.handler = handler;
		this.typeinfos = typeinfos!=null? createTypeInfos(typeinfos): Collections.EMPTY_MAP;
		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;
		this.genids = true;
		this.gencontainertags = false;
	}
	
	//-------- methods --------
	
	/**
	 *  Write the properties to an xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public void write(Object object, OutputStream out, final Object context) throws Exception
	{
		Map writtenobs = new HashMap();
		List stack = new ArrayList();
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter	writer	= factory.createXMLStreamWriter(out);
		writer.writeStartDocument();
		writeObject(writer, object, writtenobs, null, stack);
		writer.writeEndDocument();
		writer.close();
	}
	
	/**
	 *  Write an object to xml.
	 */
	public void writeObject(XMLStreamWriter writer, Object object, Map writtenobs, String tagname, List stack) throws Exception
	{
		TypeInfo typeinfo = getTypeInfo(object, getXMLPath(stack)); 
		if(typeinfo!=null)
			tagname = typeinfo.getXMLTag();
		if(tagname==null)
			tagname = object.getClass().getName();
		
		writeIdentation(writer, stack.size());
//		writeStartObject(writer, object, tagname);
		writer.writeStartElement(tagname);
		
		StackElement topse = new StackElement(tagname, object);
		stack.add(topse);
		
		if(writtenobs.containsKey(object))
		{
			if(genids)
				writer.writeAttribute("IDREF", (String)writtenobs.get(object));
//				writeIdref(writer, (String)writtenobs.get(object));
			else
				throw new RuntimeException("Object structure contains cycles: Enable 'genids' mode for serialization.");
		}
		else
		{
			writtenobs.put(object, ""+id);
			if(genids)
				writer.writeAttribute("ID", ""+id);
//				writeId(writer, ""+id);
			id++;
			
			Object[] tmp = handler.getAttributesContentAndSubobjects(object, typeinfo);
			Map attrs = (Map)tmp[0];
			String content = (String)tmp[1];
			Map subobs = (Map)tmp[2]; 
			
			if(attrs!=null)
			{
				for(Iterator it=attrs.keySet().iterator(); it.hasNext(); )
				{
					String propname = (String)it.next();
					String value = (String)attrs.get(propname);
					writer.writeAttribute(propname, value);
				}
			}
			
			if(content==null && (subobs==null || subobs.size()==0))
			{
				writer.writeEndElement();
//				writeEndObjectSameLine(writer);
			}
			else
			{
//				writer.writeEndElement();
//				writeStartTagClose(writer);
				
				// write content (before subobjects).
				if(content!=null)
				{
					writer.writeCharacters(content);
				}
				
				if(subobs==null || subobs.size()==0)
				{
					writer.writeEndElement();
//					writeEndObject(writer, object, tagname);
				}
				else
				{
					writer.writeCharacters(lf);
//					writeLF(writer);
					
					for(Iterator it=subobs.keySet().iterator(); it.hasNext(); )
					{
						Object subref = (String)it.next();
						Object obj =  subobs.get(subref);
						
						SubobjectInfo subinfo = null;
						if(typeinfo!=null)
							subinfo = typeinfo.getSubobjectInfo(subref);
						String subtagname = subinfo!=null? subinfo.getXMLAttributeName(): ""+subref; 
						
						if(SReflect.isIterable(obj))
						{
							// todo: container tags?!
							Iterator it2 = SReflect.getIterator(obj);
							if(it2.hasNext())
							{
								if(gencontainertags)
								{
									// todo: container tags in subobjectinfo
									writeIdentation(writer, stack.size());
									writer.writeStartElement(subtagname);
//									writeStartObject(writer, object, subtagname);
//									writeStartTagClose(writer);
									writer.writeEndElement();
									while(it2.hasNext())
									{
										writeObject(writer, it2.next(), writtenobs, subtagname, stack);
									}
									writeIdentation(writer, stack.size());
									writer.writeEndElement();
									//writeEndObject(writer, object, subtagname);
								}
								else
								{
									while(it2.hasNext())
									{
										writeObject(writer, it2.next(), writtenobs, subtagname, stack);
									}
								}
							}
						}
						else
						{
							writeObject(writer, obj, writtenobs, subtagname, stack);
						}
					}
					writeIdentation(writer, stack.size()-1);
//					writeEndObject(writer, object, tagname);
					writer.writeEndElement();
				}
			}
		}
		
		stack.remove(stack.size()-1);
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	protected TypeInfo getTypeInfo(Object object, String fullpath)//, Map rawattributes)
	{
		TypeInfo ret = null;
		Set maps = (Set)typeinfos.get(object.getClass());
		if(maps!=null)
		{
			for(Iterator it=maps.iterator(); ret==null && it.hasNext(); )
			{
				TypeInfo tmp = (TypeInfo)it.next();
				if(fullpath.endsWith(tmp.getXMLPathWithoutElement()))// && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
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
	 *  Writer the xml header info.
	 * /
	public void writeHeader(PrintWriter writer)
	{
		// todo: namespaces etc
		writer.write("<?xml version=\"1.0\"?>");
		writer.write(lf);
	}*/
	
	/**
	 *  Write the id of an object.
	 * /
	public void writeId(XMLStreamWriter writer, String id) throws Exception
	{
		// todo: namespaces etc
		writer.writeAttribute("ID", ""+id);
	}*/
	
	/**
	 *  Write the idref of an object.
	 */
	public void writeIdref(XMLStreamWriter writer, String idref) throws Exception
	{
		writer.writeAttribute("IDREF", idref);
		// todo: namespaces etc
		//writer.write(" IDREF=\""+idref+"\""+"/>");
		//writer.write(lf);
	}
	
	/**
	 *  Write a closing brace.
	 * /
	public void writeStartTagClose(XMLStreamWriter writer) throws Exception
	{
		writer.writeCharacters(">");
	}*/
	
	/**
	 *  Write a line feed.
	 * /
	public void writeLF(PrintWriter writer)
	{
		writer.write(lf);
	}*/
	
	/**
	 *  Write the start of an object.
	 * /
	public void writeStartObject(XMLStreamWriter writer, Object object, String name) throws Exception
	{
		writer.writeStartElement(name);
//		writer.write("<"+handler.getTag(object, null)); // todo: typeinfo
//		writer.write("<"+name); // todo: typeinfo
	}*/
	
	/**
	 *  Write the end of an object.
	 * /
	public void writeEndObject(PrintWriter writer, Object object, String name)
	{
//		writer.write("<"+handler.getTag(object, null)); // todo: typeinfo
		writer.write("</"+name+">"); // todo: typeinfo
		writer.write(lf);
	}*/
	
	/**
	 *  Write a closing brace.
	 * /
	public void writeEndObjectSameLine(XMLStreamWriter writer) throws Exception
	{
		writer.writeCharacters("/>");
		writer.writeCharacters(lf);
	}*/
	
	/**
	 *  Write an attribute value.
	 * /
	public void writeAttribute(PrintWriter writer, String name, String value)
	{
		writer.write(" "+name+"=\""+value+"\"");
	}*/
	
	/**
	 *  Write content.
	 */
	public void writeContent(PrintWriter writer, String value)
	{
		writer.write(value);
//		writer.write(lf);
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
			
			Writer w = new Writer(new BeanObjectWriterHandler(), null, null);
			
			w.write(a, System.out, null);
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
