package jadex.commons.transformation.binaryserializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class EncodingContext extends AbstractEncodingContext
{	
	/** Cache for class names. */
	protected Map<Class<?>, String> classnamecache = new HashMap<Class<?>, String>();
	
	/** The binary output */
	protected GrowableByteBuffer buffer;
		
	/** The string pool. */
	protected Map<String, Integer> stringpool;
	
	/** Cache for class IDs. */
	protected Map<Class<?>, Integer> classidcache;
	
	/** The class name pool. */
	protected Map<String, Integer> classnamepool;
	
	/** The package fragment pool. */
	protected Map<String, Integer> pkgpool;
	
	/** The current bit position within the bitfield */
	protected byte bitpos;
	
	/** The current bitfield position in the buffer*/
	protected int bitfieldpos;
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public EncodingContext(Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader)
	{
		super(rootobject, usercontext, preprocessors, classloader);
		buffer = new GrowableByteBuffer();
		classidcache = new HashMap<Class<?>, Integer>();
		stringpool = new HashMap<String, Integer>();
		//for (int i = 0; i < BinarySerializer.DEFAULT_STRINGS.size(); ++i)
			//stringpool.put(BinarySerializer.DEFAULT_STRINGS.get(i), i);
		classnamepool = new HashMap<String, Integer>();
		pkgpool = new HashMap<String, Integer>();
		bitpos = 0;
		bitfieldpos = -1;
	}
	
	/**
	 *  Returns the encoded bytes.
	 *  @return The bytes.
	 */
	public byte[] getBytes()
	{
		return buffer.toByteArray();
	}
	
	/**
	 *  Writes a byte.
	 *  @param b The byte.
	 */
	public void writeByte(byte b)
	{
		buffer.write(b);
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write(byte[] b)
	{
		buffer.write(b);
	}
	
	/**
	 *  Reserves a byte buffer on the stream.
	 *  
	 */
	public ByteBuffer getByteBuffer(int length)
	{
		return buffer.getByteBuffer(length);
	}
	
	/**
	 * 
	 * @param bool
	 */
	public void writeBoolean(boolean bool)
	{
		//byte val = (byte) ((Boolean.TRUE.equals(bool))? 1 : 0);
		byte val = (byte) (bool? 1 : 0);
		if (bitfieldpos < 0)
		{
			bitfieldpos = buffer.getPosition();
			buffer.reserveSpace(1);
		}
		if (bitpos > 7)
		{
			//buffer.writeTo(bitfieldpos, bitfield);
			bitpos = 0;
			bitfieldpos = buffer.getPosition();
			buffer.reserveSpace(1);
		}
		buffer.getBufferAccess()[bitfieldpos] |= (byte) (val << bitpos);
		++bitpos;
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
			
			/*String classname = classnamecache.get(clazz);
			if (classname == null)
			{
				classname = SReflect.getClassName(clazz);
				classnamecache.put(clazz, classname);
			}
			writeString(classname);*/
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
			try
			{
				byte[] encodedString = string.getBytes("UTF-8");
				
				writeVarInt(encodedString.length);
				buffer.write(encodedString);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
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
		int size = VarInt.getEncodedSize(value);
		int pos = buffer.getPosition();
		buffer.reserveSpace(size);
		VarInt.encode(value, buffer.getBufferAccess(), pos, size);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeSignedVarInt(long value)
	{
		boolean neg = value < 0;
		writeBoolean(neg);
		writeVarInt(Math.abs(value));
	}
}
