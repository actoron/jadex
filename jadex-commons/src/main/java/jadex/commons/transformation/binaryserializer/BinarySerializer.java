package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *  Object serializer for encoding to and decoding from a compact binary format.
 *
 */
public class BinarySerializer
{
	/** Null handler */
	public static final NullCodec NULL_HANDLER = new NullCodec();
	
	/** 
	 *  Handlers for encoding.
	 */
	protected static final List<ITraverseProcessor> ENCODER_HANDLERS;
	static
	{
		ENCODER_HANDLERS = new ArrayList<ITraverseProcessor>();
		ENCODER_HANDLERS.add(new NumberCodec());
		ENCODER_HANDLERS.add(new StringCodec());
		ENCODER_HANDLERS.add(new ArrayCodec());
		ENCODER_HANDLERS.add(new ClassCodec());
		ENCODER_HANDLERS.add(new CollectionCodec());
		ENCODER_HANDLERS.add(new EnumerationCodec());
		ENCODER_HANDLERS.add(new MultiCollectionCodec());
		ENCODER_HANDLERS.add(new MapCodec());
		/*if[android]
		  else[android]*/
		/* $if !android $ */
		ENCODER_HANDLERS.add(new ColorCodec());
		ENCODER_HANDLERS.add(new ImageCodec());
		/* $endif $ */
		/*end[android]*/
		ENCODER_HANDLERS.add(new URLCodec());
		ENCODER_HANDLERS.add(new TupleCodec());
		ENCODER_HANDLERS.add(new InetAddressCodec());
		ENCODER_HANDLERS.add(new LoggingLevelCodec());
		ENCODER_HANDLERS.add(new LogRecordCodec());
		ENCODER_HANDLERS.add(new EnumCodec());
		ENCODER_HANDLERS.add(new BeanCodec());
	}
	
	/** 
	 *  Handlers for decoding.
	 */
	protected static final List<IDecoderHandler> DECODER_HANDLERS;
	static
	{
		DECODER_HANDLERS = new ArrayList<IDecoderHandler>();
		DECODER_HANDLERS.add(NULL_HANDLER);
		for (int i = 0; i < ENCODER_HANDLERS.size(); ++i)
			DECODER_HANDLERS.add((IDecoderHandler) ENCODER_HANDLERS.get(i));
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Encoded byte array.
	 */
	public static byte[] objectToByteArray(Object val, List<ITraverseProcessor> preprocessors, Object usercontext, ClassLoader classloader)
	{
		EncodingContext context = new EncodingContext(usercontext, preprocessors, classloader);
		
		Traverser.traverseObject(val, ENCODER_HANDLERS, false, context);
		
		
		return context.getBytes();
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader)
	{
		DecodingContext context = new DecodingContext(val, postprocessors, usercontext, classloader);
		return decodeObject(context);
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArrayInputStream(ByteArrayInputStream bais, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader)
	{
		byte[] buffer = null;
		int offset = 0;
		try
		{
			// TODO: Hack! BAIS does not match cleanly with offset-based access,
			// remapping to offset-based access using introspection for performance reasons.
			Field buffield = ByteArrayInputStream.class.getField("buf");
			buffield.setAccessible(true);
			buffer = (byte[]) buffield.get(bais);
			Field posfield = ByteArrayInputStream.class.getField("pos");
			posfield.setAccessible(true);
			offset = posfield.getInt(bais);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		//List<List<IDecoderHandler>> handlers = new ArrayList<List<IDecoderHandler>>();
		//handlers.add(DECODER_HANDLERS);
		//if (postprocessors != null)
			//handlers.addAll(postprocessors);
		
		DecodingContext context = new DecodingContext(buffer, postprocessors, usercontext, classloader, offset);
		return decodeObject(context);
	}
	
	/**
	 *  Helper method for decoding an object (used for recursion).
	 *  @param context The decoding context.
	 *  @return Decoded object.
	 */
	protected static Object decodeObject(DecodingContext context)
	{
		String classname = context.readString();
		
		Class clazz = null;
		try
		{
			if (!classname.equals("NULL"))
				clazz = SReflect.classForName(classname, context.getClassloader());
			else
				clazz = Void.class;
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		
		return decodeRawObject(clazz, context);
	}
	
	/**
	 *  Method for decoding a raw object where the class is known.
	 *  
	 *  @param clazz The object's class.
	 *  @param context The decoding context.
	 *  @return Decoded object.
	 */
	protected static Object decodeRawObject(Class clazz, DecodingContext context)
	{
		// TODO: Special treatment of the actual decoders, need interface change?
		Object dobject = null;
		for (int i = 0; i < DECODER_HANDLERS.size(); ++i)
		{
			if (DECODER_HANDLERS.get(i).isApplicable(clazz))
			{
				dobject = DECODER_HANDLERS.get(i).decode(clazz, context);
				break;
			}
		}
		
		context.setLastObject(dobject);
		
		if (context.getPostProcessors() != null)
		{
			for (int i = 0; i < context.getPostProcessors().size(); ++i)
			{
				IDecoderHandler pp = context.getPostProcessors().get(i);
				if (pp.isApplicable(clazz))
					context.setLastObject(pp.decode(clazz, context));
			}
		}
		
		return context.getLastObject();
	}
}
