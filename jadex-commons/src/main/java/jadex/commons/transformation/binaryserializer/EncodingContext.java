package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class EncodingContext
{	
	/** Cache for class names. */
	protected Map<Class, String> classnamecache = new HashMap<Class, String>();
	
	/** The binary output */
	protected GrowableByteBuffer buffer;
		
	/** The string pool. */
	protected Map<String, Integer> stringPool;
	
	/** A user context. */
	protected Object usercontext;
	
	/** The preprocessors. */
	protected List<ITraverseProcessor> preprocessors;
	
	/** The classloader */
	protected ClassLoader classloader;
	
	/** The current bit position within the bitfield */
	protected byte bitpos;
	
	/** The current bitfield position in the buffer*/
	protected int bitfieldpos;
	
	/** Flag indicating class names should be written (can be temporarily disable for one write). */
	protected boolean writeclass;
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public EncodingContext(Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader)
	{
		this.usercontext = usercontext;
		this.preprocessors = preprocessors;
		this.classloader = classloader;
		this.writeclass = true;
		buffer = new GrowableByteBuffer();
		stringPool = new HashMap<String, Integer>();
		for (int i = 0; i < BinarySerializer.DEFAULT_STRINGS.size(); ++i)
			stringPool.put(BinarySerializer.DEFAULT_STRINGS.get(i), i);
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
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}
	
	/**
	 *  Returns the preprocessors.
	 *  @return The preprocessors
	 */
	public List<ITraverseProcessor> getPreprocessors()
	{
		return preprocessors;
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Puts the context in a state where the next call to
	 *  writeClass is ignored.
	 */
	public void ignoreNextClassWrite()
	{
		writeclass = false;
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
	
	public void writeClass(Class clazz)
	{
		if (writeclass)
		{
			String classname = classnamecache.get(clazz);
			if (classname == null)
			{
				classname = SReflect.getClassName(clazz);
				classnamecache.put(clazz, classname);
			}
			writeString(classname);
		}
		else
		{
			writeclass = true;
		}
	}
	
	public void writeString(String string)
	{
		Integer sid = stringPool.get(string);
		if (sid == null)
		{
			sid = stringPool.size();
			stringPool.put(string, sid);
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
			writeVarInt(sid);
	}
	
	public void writeVarInt(long value)
	{
		int size = VarInt.getEncodedSize(value);
		int pos = buffer.getPosition();
		buffer.reserveSpace(size);
		VarInt.encode(value, buffer.getBufferAccess(), pos, size);
	}
	
	public void writeSignedVarInt(long value)
	{
		boolean neg = value < 0;
		writeBoolean(neg);
		writeVarInt(Math.abs(value));
	}
}
