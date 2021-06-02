package jadex.binary;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Encoding context with framing output.
 *
 */
public class FramingEncodingContext extends AbstractEncodingContext
{
	/** The buffer. */
	protected byte[] buffer = new byte[32];
	
	/** The frame stack, buffer position and fixed size marker. */
	protected Deque<Tuple2<Integer, Boolean>> framestack = new ArrayDeque<>();
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public FramingEncodingContext(Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader, SerializationConfig config)
	{
		super(rootobject, usercontext, preprocessors, classloader, config);
	}
	
	/**
	 *  Starts an object frame
	 *  when using a context with framing support.
	 *  
	 *  @param fixedsize If true, use fixed-size (integer) framing.
	 *  				 Set true if the object being framed is expected
	 *  				 to be larger than 127 bytes (same type of object MUST use
	 *  				 either fixed OR variable framing).
	 */
	public void startObjectFrame(boolean fixedsize)
	{
		if (fixedsize)
		{
			ensureSpace(4);
			writtenbytes += 4;
		}
		else
		{
			ensureSpace(1);
			++writtenbytes;
		}
		Tuple2<Integer, Boolean> frameinfo = new Tuple2<Integer, Boolean>((int) writtenbytes, fixedsize);
		framestack.push(frameinfo);
	}
	
	/**
	 *  Stops an object frame
	 *  when using a context with framing support.
	 */
	public void stopObjectFrame()
	{
		Tuple2<Integer, Boolean> frameinfo = framestack.pop();
		int framesize = (int) (writtenbytes - frameinfo.getFirstEntity());
		
		if (frameinfo.getSecondEntity())
		{
			SUtil.intIntoBytes(framesize, buffer, frameinfo.getFirstEntity() - 4);
		}
		else
		{
			int encsize = VarInt.getEncodedSize(framesize);
			if (encsize > 1)
			{
				// Size is bigger than expected, we need to shuffle the frame forward to make room.
				ensureSpace(encsize - 1);
				System.arraycopy(buffer, frameinfo.getFirstEntity(), buffer, frameinfo.getFirstEntity() + encsize - 1, framesize);
				writtenbytes += (encsize - 1);
			}
			VarInt.encode(framesize, buffer, frameinfo.getFirstEntity() - 1, encsize);
		}
	}
	
	
	public void writeByte(byte b)
	{
		ensureSpace(1);
		buffer[(int) writtenbytes] = b;
		++writtenbytes;
	}

	public void write(byte[] b)
	{
		ensureSpace(b.length);
		System.arraycopy(b, 0, buffer, (int) writtenbytes, b.length);
		writtenbytes += b.length;
	}
	
	/**
	 *  Returns the current data buffer, trimmed to size.
	 *  
	 *  @return The buffer.
	 */
	public byte[] toByteArray()
	{
		if (buffer.length > writtenbytes)
			buffer = Arrays.copyOf(buffer, (int) writtenbytes);
		return buffer;
	}
	
	/**
	 *  Ensures sufficient space in buffer.
	 *  
	 *  @param requiredsize Required space in buffer.
	 */
	protected void ensureSpace(int requiredsize)
	{
		if (buffer.length - writtenbytes < requiredsize)
		{
			int newsize = buffer.length;
			do
			{
				newsize <<= 1;
			}
			while (newsize - writtenbytes < requiredsize);
			buffer = Arrays.copyOf(buffer, newsize);
		}
	}
}
