package jadex.commons.xml;

import jadex.commons.SReflect;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  XML writer for conversion of objects to XML.
 */
public class Writer
{
	//-------- static part --------
	
	public static final String lf = (String)System.getProperty("line.separator");
	
	//-------- attributes --------
	
	/** The object creator. */
	protected IObjectWriterHandler handler;
	
	/** The type mappings. */
	protected Set typeinfos;
	
	/** The ignored attribute types. */
	protected Set ignoredattrs;
	
	/** The id counter. */
	protected int id;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public Writer(IObjectWriterHandler handler, Set typeinfos, Set ignoredattrs)
	{
		this.handler = handler;
		this.typeinfos = typeinfos!=null? typeinfos: Collections.EMPTY_SET;
		this.ignoredattrs = ignoredattrs!=null? ignoredattrs: Collections.EMPTY_SET;
		
	}
	
	//-------- methods --------
	
	/**
	 *  Write the properties to an xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
 	 */
	public void write(Object object, OutputStream output, final Object context)
	{
		PrintWriter writer = new PrintWriter(output);
		
		Map writtenobs = new HashMap();
		writeHeader(writer);
		writeObject(writer, object, writtenobs, null, 0);
		writer.close();
	}
	
	/**
	 *  Write an object to xml.
	 */
	public void writeObject(PrintWriter writer, Object object, Map writtenobs, String tagname, int level)
	{
		tagname = tagname==null? handler.getTag(object, null): tagname;
		
		writeIdentation(writer, level);
		writeStartObject(writer, object, tagname);
		
		if(writtenobs.containsKey(object))
		{
			writeIdref(writer, (String)writtenobs.get(object));
		}
		else
		{
			writeId(writer, ""+id);
			writtenobs.put(object, ""+id);
			id++;
			
			Object[] tmp = handler.getAttributesAndSubobjects(object, null); // todo: typeinfo
			Map attrs = (Map)tmp[0];
			Map subobs = (Map)tmp[1]; 
			
			if(attrs!=null)
			{
				for(Iterator it=attrs.keySet().iterator(); it.hasNext(); )
				{
					String propname = (String)it.next();
					String value =  (String)attrs.get(propname);
					writeAttribute(writer, propname, value);
				}
			}
			
				
			if(subobs!=null)
			{
				if(subobs.size()==0)
				{
					writeClosingBrace(writer, true);
				}
				else
				{
					writeClosingBrace(writer, false);
					for(Iterator it=subobs.keySet().iterator(); it.hasNext(); )
					{
						String subname = (String)it.next();
						Object obj =  subobs.get(subname);
						if(obj!=null)
						{
							if(SReflect.isIterable(obj))
							{
								// todo: container tags?!
								writeIdentation(writer, level+1);
								writeStartObject(writer, object, subname);
								writeClosingBrace(writer, false);
								for(Iterator it2 = SReflect.getIterator(obj); it2.hasNext(); )
								{
									writeObject(writer, it2.next(), writtenobs, null, level+2);
								}
								writeIdentation(writer, level+1);
								writeEndObject(writer, object, subname);
							}
							else
							{
								writeObject(writer, obj, writtenobs, subname, level+1);
							}
						}
					}
					writeIdentation(writer, level);
					writeEndObject(writer, object, tagname);
				}
			}
		}
	}
	
	/**
	 *  Writer the xml header info.
	 */
	public void writeHeader(PrintWriter writer)
	{
		// todo: namespaces etc
		writer.write("<?xml version=\"1.0\"?>");
		writer.write(lf);
	}
	
	/**
	 *  Write the id of an object.
	 */
	public void writeId(PrintWriter writer, String id)
	{
		// todo: namespaces etc
		writer.write(" ID=\""+id+"\"");
	}
	
	/**
	 *  Write the idref of an object.
	 */
	public void writeIdref(PrintWriter writer, String idref)
	{
		// todo: namespaces etc
		writer.write(" IDREF=\""+idref+"\""+"/>");
		writer.write(lf);
	}
	
	/**
	 *  Write a closing brace.
	 */
	public void writeClosingBrace(PrintWriter writer, boolean sameline)
	{
		if(sameline)
			writer.write("/>");
		else
			writer.write(">");
		writer.write(lf);
	}
	
	/**
	 *  Write the start of an object.
	 */
	public void writeStartObject(PrintWriter writer, Object object, String name)
	{
//		writer.write("<"+handler.getTag(object, null)); // todo: typeinfo
		writer.write("<"+name); // todo: typeinfo
	}
	
	/**
	 *  Write the end of an object.
	 */
	public void writeEndObject(PrintWriter writer, Object object, String name)
	{
//		writer.write("<"+handler.getTag(object, null)); // todo: typeinfo
		writer.write("<"+name+"/>"); // todo: typeinfo
		writer.write(lf);
	}
	
	/**
	 *  Write an attribute value.
	 */
	public void writeAttribute(PrintWriter writer, String name, String value)
	{
		writer.write(" "+name+"=\""+value+"\"");
	}
	
	/**
	 *  Write the identation.
	 */
	public void writeIdentation(PrintWriter writer, int level)
	{
		for(int i=0; i<level; i++)
			writer.write("  ");
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
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
