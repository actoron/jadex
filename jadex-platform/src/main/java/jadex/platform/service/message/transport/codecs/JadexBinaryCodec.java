package jadex.platform.service.message.transport.codecs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jadex.bridge.service.types.message.ICodec;
import jadex.bridge.service.types.message.IEncodingContext;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

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
		List<ITraverseProcessor> currentencoders = new ArrayList<ITraverseProcessor>(BinarySerializer.ENCODER_HANDLERS);
		Date since = new Date(1375912800000L);
		Tuple2<Date, List<ITraverseProcessor>> chain = new Tuple2<Date, List<ITraverseProcessor>>(since, currentencoders);
		ENCODER_CHAINS.add(chain);
		
		// Oldest chain
		List<ITraverseProcessor> encoders = new ArrayList<ITraverseProcessor>();
		encoders.add(new LegacyMethodInfoEncoder());
		encoders.addAll(BinarySerializer.ENCODER_HANDLERS);
		since = new Date(0);
		chain = new Tuple2<Date, List<ITraverseProcessor>>(since, encoders);
		ENCODER_CHAINS.add(chain);
		
		// Undeclared chain
		chain = new Tuple2<Date, List<ITraverseProcessor>>(null, currentencoders);
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
	
//	public static void main(String[] args) throws NoSuchMethodException, SecurityException
//	{
//		System.out.println("Start");
//		List<ITraverseProcessor> newchain = new ArrayList<ITraverseProcessor>(BinarySerializer.ENCODER_HANDLERS);
//		
//		List<ITraverseProcessor> oldchain = new ArrayList<ITraverseProcessor>();
//		oldchain.add(new LegacyMethodInfoEncoder());
//		oldchain.addAll(BinarySerializer.ENCODER_HANDLERS);
//		
//		Method m = JadexBinaryCodec.class.getMethod("encode", Object.class, ClassLoader.class, IEncodingContext.class);
//		
//		MethodInfo mi = new MethodInfo(m);
//		
//		byte[] b = BinarySerializer.objectToByteArray(mi, null, oldchain, null, null);
//		
//		BinarySerializer.objectFromByteArray(b, null, null, null, null);
//		
//		b = BinarySerializer.objectToByteArray(mi, null, newchain, null, null);
//		
//		BinarySerializer.objectFromByteArray(b, null, null, null, null);
//		
//		RemoteIntermediateResultCommand rirc = new RemoteIntermediateResultCommand(new ComponentIdentifier("blablabla"), new Object(), "callid", false, "getClass", true, new HashMap<String, Object>(), new Future(), 5);
//		b = BinarySerializer.objectToByteArray(mi, null, oldchain, null, null);
//		BinarySerializer.objectFromByteArray(b, null, null, null, null);
//		System.out.println("End");
//	}
	
	/** Encoder chain cache. */
	protected static final Map<Date, List<ITraverseProcessor>> ENCODER_CHAIN_CACHE = Collections.synchronizedMap(new LRU<Date, List<ITraverseProcessor>>(100));
	
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
		{
			try
			{
				System.out.println("encode message: "+(new String(ret, "UTF-8")));
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
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
		{
			try
			{
				System.out.println("decode message: "+(new String((byte[])bytes, "UTF-8")));
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
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
//								System.out.println(context.getTargetReleaseDate());
								if (context.getTargetReleaseDate() == null || chain.getFirstEntity() == null)
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