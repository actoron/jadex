package jadex.binary;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.commons.SUtil;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class DataOutputEncodingContext extends AbstractEncodingContext
{	
	/** Cache for class names. */
	protected Map<Class<?>, String> classnamecache = new HashMap<Class<?>, String>();
	
	/** The binary output */
	protected DataOutput dato;
		
	/** The string pool. */
	protected Map<String, Integer> stringpool;
	
	/** Cache for class IDs. */
	protected Map<Class<?>, Integer> classidcache;
	
	/** The class name pool. */
	protected Map<String, Integer> classnamepool;
	
	/** The package fragment pool. */
	protected Map<String, Integer> pkgpool;
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public DataOutputEncodingContext(DataOutput dato, Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader)
	{
		super(rootobject, usercontext, preprocessors, classloader);
		this.dato = dato;
		classidcache = new HashMap<Class<?>, Integer>();
	}
	
	/**
	 *  Writes a byte.
	 *  @param b The byte.
	 */
	public void writeByte(byte b)
	{
		try
		{
			dato.writeByte(b);
			++writtenbytes;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write(byte[] b)
	{
		try
		{
			dato.write(b);
			writtenbytes += b.length;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param bool
	 */
	public void writeBoolean(boolean bool)
	{
		try
		{
			dato.writeByte(bool? 1 : 0);
			++writtenbytes;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 */
	public void writeClass(Class<?> clazz)
	{
		if(!isIgnoreNextClassWrite())
		{
			Integer classid = classidcache.get(clazz);
			if (classid == null)
			{
				String	classname	= STransformation.registerClass(clazz);
				classid = writeClassname(classname);
				classidcache.put(clazz, classid);
			}
			else
			{
				writeVarInt(classid.intValue());
			}
		}
		else
		{
			setIgnoreNextClassWrite(false);
		}
	}
	
	/**
	 * 
	 */
	public int writeClassname(String name)
	{
		Integer classid = classnamepool.get(name);
		if (classid == null)
		{
			classid = classnamepool.size();
			classnamepool.put(name, classid);
			writeVarInt(classid);
			
			int lppos = name.lastIndexOf('.');
			if (lppos < 0)
			{
				writeVarInt(0);
				writeString(name);
			}
			else
			{
				String pkgname = name.substring(0, lppos);
				String classname = name.substring(lppos + 1);
				
				StringTokenizer tok = new StringTokenizer(pkgname, ".");
				writeVarInt(tok.countTokens());
				while (tok.hasMoreElements())
				{
					String frag = tok.nextToken();
					Integer pkgfragid = pkgpool.get(frag);
					if (pkgfragid == null)
					{
						pkgfragid = pkgpool.size();
						pkgpool.put(frag, pkgfragid);
						writeVarInt(pkgfragid);
						writeString(frag);
						
					}
					else
					{
						writeVarInt(pkgfragid);
					}
				}
				writeString(classname);
			}
		}
		else
		{
			writeVarInt(classid.intValue());
		}
		
		
		return classid.intValue();
	}
	
	/**
	 * 
	 * @param string
	 */
	public void writeString(String string)
	{
		Integer sid = stringpool.get(string);
		if(sid == null)
		{
			sid = stringpool.size();
			stringpool.put(string, sid);
			writeVarInt(sid);
			
			if (string != null)
			{
				byte[] encodedString = string.getBytes(SUtil.UTF8);
				
				writeVarInt(encodedString.length);
				write(encodedString);
			}
			else
			{
				writeVarInt(Integer.MAX_VALUE + 1L);
			}
			
		}
		else
		{
			writeVarInt(sid);
		}
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeVarInt(long value)
	{
		try
		{
		write(VarInt.encode(value));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeSignedVarInt(long value)
	{
		boolean neg = value < 0;
		value = Math.abs(value);
		long mask = Long.highestOneBit(value) << 2;
		if (neg)
		{
			mask |= mask >> 1;
		}
		value = value | (mask);
		writeVarInt(value);
	}
}
