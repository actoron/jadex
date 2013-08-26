package jadex.platform.service.message.transport.codecs;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  The Jadex Binary codec. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexBinaryCodec implements ICodec
{
	//-------- constants --------
	
	/** The JadexBinary codec id. */
	public static final byte CODEC_ID = 6;
	
	/** Available encoder chains. */
	protected static final List<Tuple2<Date, List<ITraverseProcessor>>> ENCODER_CHAINS = Collections.synchronizedList(new ArrayList<Tuple2<Date,List<ITraverseProcessor>>>());
	static
	{
		// Current chain
		List<ITraverseProcessor> encoders = BinarySerializer.ENCODER_HANDLERS;
		Date since = new Date(1375912800000L);
		Tuple2<Date, List<ITraverseProcessor>> chain = new Tuple2<Date, List<ITraverseProcessor>>(since, encoders);
		ENCODER_CHAINS.add(chain);
		
		// Oldest / Undeclared chain
		encoders = new ArrayList<ITraverseProcessor>(BinarySerializer.ENCODER_HANDLERS);
		encoders.add(new LegacyMethodInfoEncoder());
		since = null;
		chain = new Tuple2<Date, List<ITraverseProcessor>>(since, encoders);
		ENCODER_CHAINS.add(chain);
		
//		Collections.sort(ENCODER_CHAINS, new Comparator<Tuple2<Date, List<ITraverseProcessor>>>()
//		{
//			public int compare(Tuple2<Date, List<ITraverseProcessor>> o1,
//					Tuple2<Date, List<ITraverseProcessor>> o2)
//			{
//				return o1.getFirstEntity().before(o2.getFirstEntity())? 1 : -1;
//			}
//		});
	}
	
	/** Encoder chain cache. */
	protected static Map<Date, List<ITraverseProcessor>> ENCODER_CHAIN_CACHE = Collections.synchronizedMap(new LRU<Date, List<ITraverseProcessor>>(100));
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	
	//-------- methods --------
	
	/**
	 *  Get the codec id.
	 *  @return The codec id.
	 */
	public byte getCodecId()
	{
		return CODEC_ID;
	}
	
	/**
	 *  Encode an object.
	 *  @param obj The object.
	 *  @throws IOException
	 */
	public Object encode(Object val, ClassLoader classloader, IEncodingContext context)
	{
		byte[] ret = BinarySerializer.objectToByteArray(val, null, getEncoderChain(context), null, classloader);
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret)));
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(Object bytes, ClassLoader classloader, IErrorReporter rep)
	{
		Object ret = bytes instanceof byte[]
			? BinarySerializer.objectFromByteArray((byte[])bytes, null, null, classloader, rep)
			: BinarySerializer.objectFromByteArrayInputStream((ByteArrayInputStream)bytes, null, null, classloader, rep);
		if(DEBUG)
			System.out.println("decode message: "+(new String((byte[])bytes)));
		return ret;
	}
	
	/**
	 *  Returns the encoder chain for the given Jadex version.
	 * 
	 *  @param context Context providing the Jadex version.
	 *  @return Correct encoder chain, newest chain returned if context is null,
	 *  		 oldest chain returned if version is null.
	 */
	public static final List<ITraverseProcessor> getEncoderChain(IEncodingContext context)
	{
		List<ITraverseProcessor> enchandlers = BinarySerializer.ENCODER_HANDLERS;
		if (context != null)
		{
			enchandlers = ENCODER_CHAIN_CACHE.get(context.getTargetReleaseDate());
			if (enchandlers == null)
			{
				synchronized (ENCODER_CHAIN_CACHE)
				{
					enchandlers = ENCODER_CHAIN_CACHE.get(context.getTargetReleaseDate());
					if (enchandlers == null)
					{
						synchronized (ENCODER_CHAINS)
						{
							for (Tuple2<Date, List<ITraverseProcessor>> chain : ENCODER_CHAINS)
							{
								if (context.getTargetReleaseDate() == null)
								{
									if (chain.getFirstEntity() == null)
									{
										enchandlers = chain.getSecondEntity();
										break;
									}
								}
								else if (context.getTargetReleaseDate().after(chain.getFirstEntity()))
								{
									enchandlers = chain.getSecondEntity();
									break;
								}
							}
							
							ENCODER_CHAIN_CACHE.put(context.getTargetReleaseDate(), enchandlers);
						}
					}
				}
			}
		}
		return enchandlers;
	}
}