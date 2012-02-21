package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class EncodingContext
{	
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
	
	/** The current bitfield */
	protected byte bitfield;
	
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
		//stringPool.put("java.lang.String", 0);
		//stringPool.put("byte[]", 1);
		bitfield = 0;
		bitpos = 8;
		bitfieldpos = 0;
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
	 *  Runs the preprocessors.
	 */
	public Object runPreProcessors(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		if (preprocessors != null)
		{
			for (ITraverseProcessor preproc : preprocessors)
			{
				if (preproc.isApplicable(object, clazz, clone))
				{
					object = preproc.process(object, clazz, processors, traverser, traversed, clone, context);
				}
			}
		}
		return object;
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
	public void write (byte[] b)
	{
		buffer.write(b);
	}
	
	public void writeBoolean(Boolean bool)
	{
		byte val = (byte) ((Boolean.TRUE.equals(bool))? 1 : 0);
		if (bitpos > 7)
		{
			bitfield = 0;
			bitpos = 0;
			bitfieldpos = buffer.getPosition();
			buffer.write(new byte[] { bitfield });
		}
		bitfield |= (byte) (val << bitpos);
		++bitpos;
		buffer.writeTo(bitfieldpos, bitfield);
	}
	
	public void writeClass(Class clazz)
	{
		if (writeclass)
		{
			String classname = SReflect.getClassName(clazz);
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
			buffer.write(VarInt.encode(sid));
			try
			{
				byte[] encodedString = string.getBytes("UTF-8");
				
				buffer.write(VarInt.encode(encodedString.length));
				buffer.write(encodedString);
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
			buffer.write(VarInt.encode(sid));
	}
	
	public void writeSignedVarInt(long value)
	{
		boolean neg = value < 0;
		writeBoolean(neg);
		write(VarInt.encode(Math.abs(value)));
	}
}
