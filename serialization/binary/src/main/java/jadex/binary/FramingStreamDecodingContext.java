package jadex.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Stream decoding context that can handled framed streams.
 *
 */
public class FramingStreamDecodingContext extends StreamDecodingContext
{
	/** The frame stack, frame start position, frame length. */
	protected Deque<Tuple2<Integer, Integer>> framestack = new ArrayDeque<>();
	
	/**
	 * Creates a new DecodingContext.
	 * @param classloader The classloader.
	 * @param content The content being decoded.
	 */
	public FramingStreamDecodingContext(InputStream is, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
	{
		super(is, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config, 0);
	}
	
	/**
	 * Creates a new DecodingContext with specific offset.
	 * @param content The content being decoded.
	 * @param offset The offset.
	 */
	public FramingStreamDecodingContext(InputStream is, List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config, int offset)
	{
		super(is, decoderhandlers, postprocessors, usercontext, classloader, errorreporter, config, offset);
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
		int framesize = -1;
		if (fixedsize)
		{
			byte[] encsize = new byte[4];
			read(encsize);
			framesize = SUtil.bytesToInt(encsize);
		}
		else
		{
			framesize = (int) readVarInt();
		}
		
		Tuple2<Integer, Integer> frameinfo = new Tuple2<Integer, Integer>(offset, framesize);
		framestack.push(frameinfo);
	}
	
	/**
	 *  Stops an object frame
	 *  when using a context with framing support.
	 */
	public void stopObjectFrame()
	{
		Tuple2<Integer, Integer> frameinfo = framestack.pop();
		int remaining = frameinfo.getSecondEntity() - (offset - frameinfo.getFirstEntity());
		
		if (remaining > 0)
		{
			try
			{
				is.skip(remaining);
			}
			catch (IOException e)
			{
				SUtil.throwUnchecked(e);
			}
		}
	}
}
