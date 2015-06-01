package jadex.xml.reader;

import jadex.xml.SXML;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

import jadex.xml.stax.QName;

/**
 * XML Reader abstract class.
 */
public abstract class AReader
{
	//-------- constants --------
	
	/** The debug flag. */
	public static final boolean DEBUG = false;
	
	/** The string marker object. */
	public static final Object STRING_MARKER = new Object();

	/** This thread local variable provides access to the read context,
	 *  e.g. from the XML reporter, if required. */
	public static final ThreadLocal<AReadContext> READ_CONTEXT = new ThreadLocal<AReadContext>();
	
	/** The null object. */
	public static final Object NULL = new Object();
	
	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
	 */
	public abstract Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, java.io.Reader input, final ClassLoader classloader,
			final Object callcontext) throws Exception;

	/**
	 *  Read properties from xml.
	 *  @param input The input stream.
	 *  @param classloader The classloader.
	 * 	@param context The context.
	 */
	public abstract Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, InputStream input, final ClassLoader classloader,
			final Object callcontext) throws Exception;
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromXML(AReader reader, String val, ClassLoader classloader, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		return objectFromXML(reader, val, classloader, null, manager, handler);
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromXML(AReader reader, String val, ClassLoader classloader, 
		Object context, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
//		return objectFromByteArray(reader, val.getBytes(), classloader, context);
		java.io.Reader rd = null;
		try
		{
			rd = new StringReader(val);
			Object ret = reader.read(manager, (IObjectReaderHandler)handler, rd, classloader, context);
			return ret;
		}
		catch(Exception e)
		{
//			t.printStackTrace();
//			System.out.println("problem: "+new String(val));
			throw new RuntimeException(e);
		}
		finally
		{
			if(rd!=null)
			{
				try
				{
					rd.close();
				}
				catch(Exception e)
				{
				}
			}
		}
	}
		
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromByteArray(AReader reader, byte[] val, ClassLoader classloader, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		return objectFromInputStream(reader, new ByteArrayInputStream(val), classloader, null, manager, handler);
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromByteArray(AReader reader, byte[] val, ClassLoader classloader, Object context, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		return objectFromInputStream(reader, new ByteArrayInputStream(val), classloader, context, manager, handler);		
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromInputStream(AReader reader, InputStream val, ClassLoader classloader, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		return objectFromInputStream(reader, val, classloader, null, manager, handler);
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromInputStream(AReader reader, InputStream bis, ClassLoader classloader, Object context, TypeInfoPathManager manager, IObjectReaderHandler handler)
	{
		try
		{
			Object ret = reader.read(manager, (IObjectReaderHandler)handler, bis, classloader, context);
			return ret;
		}
		catch(Exception e)
		{
//			t.printStackTrace();
//			System.out.println("problem: "+new String(val));
			throw new RuntimeException(e);
		}
		finally
		{
			if(bis!=null)
			{
				try
				{
					bis.close();
				}
				catch(Exception e)
				{
				}
			}
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
	 *  Get a subobject info for reading.
	 */
	public static SubobjectInfo getSubobjectInfoRead(QName localname, QName[] fullpath, TypeInfo patypeinfo, Map<String, String> attrs)
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