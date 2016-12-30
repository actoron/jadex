package jadex.commons.transformation.binaryserializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Object serializer for encoding to and decoding from a compact binary format.
 *
 */
public class SBinarySerializer2
{
	/** The magic byte identifying it as a binary serializer 2 stream. */
	protected static final byte MAGIC_BYTE = -13;
	
	/** Serializer version */
	protected static final int VERSION = 2;
	
	/** Marker for null values */
	protected static final String NULL_MARKER = "0";
	
	/** Marker for references */
	protected static final String REFERENCE_MARKER = "1";
	
	/** 
	 *  Handlers for encoding.
	 */
	public static final List<ITraverseProcessor> ENCODER_HANDLERS;
	static
	{
		ENCODER_HANDLERS = new ArrayList<ITraverseProcessor>();
		ENCODER_HANDLERS.add(new NumberCodec2());
		ENCODER_HANDLERS.add(new StringCodec());
		ENCODER_HANDLERS.add(new ArrayCodec2());
		ENCODER_HANDLERS.add(new ClassCodec());
		ENCODER_HANDLERS.add(new CollectionCodec());
		ENCODER_HANDLERS.add(new EnumerationCodec());
		ENCODER_HANDLERS.add(new MultiCollectionCodec());
		ENCODER_HANDLERS.add(new LRUCodec());
		ENCODER_HANDLERS.add(new MapCodec());
		if(!SReflect.isAndroid())
		{
			ENCODER_HANDLERS.add(new ColorCodec());
			ENCODER_HANDLERS.add(new ImageCodec());
			ENCODER_HANDLERS.add(new RectangleCodec());
		}
		ENCODER_HANDLERS.add(new URLCodec());
		ENCODER_HANDLERS.add(new URICodec());
		ENCODER_HANDLERS.add(new TupleCodec());
		ENCODER_HANDLERS.add(new DateCodec());
		ENCODER_HANDLERS.add(new CalendarCodec());
		ENCODER_HANDLERS.add(new InetAddressCodec());
		ENCODER_HANDLERS.add(new LoggingLevelCodec());
		ENCODER_HANDLERS.add(new LogRecordCodec());
		ENCODER_HANDLERS.add(new EnumCodec());
		ENCODER_HANDLERS.add(new UUIDCodec());
		ENCODER_HANDLERS.add(new TimestampCodec());
		ENCODER_HANDLERS.add(new CertificateCodec());
		ENCODER_HANDLERS.add(new StackTraceElementCodec());
		ENCODER_HANDLERS.add(new ThrowableCodec());
		ENCODER_HANDLERS.add(new LocalDateTimeCodec());
		ENCODER_HANDLERS.add(new BigIntegerCodec());
		ENCODER_HANDLERS.add(new BeanCodec());
	}
	
	/** 
	 *  Handlers for decoding.
	 */
	protected static final List<IDecoderHandler> DECODER_HANDLERS;
	static
	{
		DECODER_HANDLERS = new ArrayList<IDecoderHandler>();
		//DECODER_HANDLERS.add(NULL_HANDLER);
		for(int i = 0; i < ENCODER_HANDLERS.size(); ++i)
			DECODER_HANDLERS.add((IDecoderHandler) ENCODER_HANDLERS.get(i));
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToStream(OutputStream os, Object val, ClassLoader classloader)
	{
		return writeObjectToStream(os, val, null, null, classloader);
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToStream(OutputStream os, Object val, List<ITraverseProcessor> preprocessors, Object usercontext, ClassLoader classloader)
	{
		return writeObjectToStream(os, val, preprocessors, null, usercontext, classloader);
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToStream(OutputStream os, Object val, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> encoderhandlers, Object usercontext, ClassLoader classloader)
	{
		try
		{
			os.write(MAGIC_BYTE);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		encoderhandlers = encoderhandlers == null? ENCODER_HANDLERS : encoderhandlers;
		IEncodingContext context = new EncodingContext2(os, val, usercontext, preprocessors, classloader);
		context.writeVarInt(VERSION);
		
		Traverser traverser = new Traverser()
		{
			public void handleDuplicate(Object object, Class<?> clazz, Object match,
				List<ITraverseProcessor> processors, boolean clone, Object context)
			{
				IEncodingContext ec = (IEncodingContext)context;
				int ref = ((Integer)match).intValue();
				ec.writeClassname(REFERENCE_MARKER);
				ec.writeVarInt(ref);
			}
			
			/**
			 *  Special handling for null objects.
			 */
			public Object handleNull(Class<?> clazz,
				List<ITraverseProcessor> processors, boolean clone, Object context)
			{
				IEncodingContext ec = (IEncodingContext)context;
				ec.writeClassname(NULL_MARKER);
				return null;
			}
		};
		//Traverser.traverseObject(val, ENCODER_HANDLERS, false, context);
		traverser.traverse(val, null, new IdentityHashMap<Object, Object>(), encoderhandlers, false, null, context);
		return context.getWrittenBytes();
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToDataOutput(DataOutput dato, Object val, ClassLoader classloader)
	{
		return writeObjectToDataOutput(dato, val, null, null, classloader);
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToDataOutput(DataOutput dato, Object val, List<ITraverseProcessor> preprocessors, Object usercontext, ClassLoader classloader)
	{
		return writeObjectToDataOutput(dato, val, preprocessors, null, usercontext, classloader);
	}
	
	/**
	 *  Convert an object to an encoded byte array.
	 *  
	 *  @param val The object being encoded.
	 *  @param preprocessors List of processors called before the object is encoded, may be null.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader used.
	 *  @return Bytes written.
	 */
	public static long writeObjectToDataOutput(DataOutput dato, Object val, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> encoderhandlers, Object usercontext, ClassLoader classloader)
	{
		try
		{
			dato.writeByte(MAGIC_BYTE);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		encoderhandlers = encoderhandlers == null? ENCODER_HANDLERS : encoderhandlers;
		IEncodingContext context = new DataOutputEncodingContext(dato, val, usercontext, preprocessors, classloader);
		context.writeVarInt(VERSION);
		
		Traverser traverser = new Traverser()
		{
			public void handleDuplicate(Object object, Class<?> clazz, Object match,
				List<ITraverseProcessor> processors, boolean clone, Object context)
			{
				IEncodingContext ec = (IEncodingContext)context;
				int ref = ((Integer)match).intValue();
				ec.writeClassname(REFERENCE_MARKER);
				ec.writeVarInt(ref);
			}
			
			/**
			 *  Special handling for null objects.
			 */
			public Object handleNull(Class<?> clazz,
				List<ITraverseProcessor> processors, boolean clone, Object context)
			{
				IEncodingContext ec = (IEncodingContext)context;
				ec.writeClassname(NULL_MARKER);
				return null;
			}
		};
		//Traverser.traverseObject(val, ENCODER_HANDLERS, false, context);
		traverser.traverse(val, null, new IdentityHashMap<Object, Object>(), encoderhandlers, false, null, context);
		
		return context.getWrittenBytes();
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @param errorreporter The error reporter, may be null in which case the default reporter is used.
	 *  @return The decoded object.
	 */
	public static Object readObjectFromStream(InputStream is, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		try
		{
			byte mbyte = (byte) is.read();
			if (mbyte != MAGIC_BYTE)
			{
				throw new RuntimeException("Decoding failed, magic byte not found., found: " + mbyte);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		
		if (errorreporter == null)
		{
			errorreporter = new DefaultErrorReporter();
		}
		IDecodingContext context = new DecodingContext2(is, DECODER_HANDLERS, postprocessors, usercontext, classloader, errorreporter);
		int streamver = (int) context.readVarInt();
		if (streamver != VERSION)
		{
			throw new RuntimeException("Version mismatch, stream reported version " + streamver + " should be " + VERSION);
		}
		
		return BinarySerializer.decodeObject(context);
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @param errorreporter The error reporter, may be null in which case the default reporter is used.
	 *  @return The decoded object.
	 */
	public static Object readObjectFromDataInput(DataInput di, List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		try
		{
			byte mbyte = (byte) di.readByte();
			if (mbyte != MAGIC_BYTE)
			{
				throw new RuntimeException("Decoding failed, magic byte not found., found: " + mbyte);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		
		if (errorreporter == null)
		{
			errorreporter = new DefaultErrorReporter();
		}
		IDecodingContext context = new DataInputDecodingContext(di, DECODER_HANDLERS, postprocessors, usercontext, classloader, errorreporter);
		int streamver = (int) context.readVarInt();
		if (streamver != VERSION)
		{
			throw new RuntimeException("Version mismatch, stream reported version " + streamver + " should be " + VERSION);
		}
		
		return BinarySerializer.decodeObject(context);
	}
}
