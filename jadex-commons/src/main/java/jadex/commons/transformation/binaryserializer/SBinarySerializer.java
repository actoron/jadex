package jadex.commons.transformation.binaryserializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Object serializer for encoding to and decoding from a compact binary format.
 *
 */
public class SBinarySerializer
{
	/** The magic byte identifying it as a binary serializer 2+ stream. */
	protected static final byte MAGIC_BYTE = -13;
	
	/** Serializer version */
	protected static final int VERSION = 3;
	
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
		ENCODER_HANDLERS.add(new NumberCodec());
		ENCODER_HANDLERS.add(new StringCodec());
		ENCODER_HANDLERS.add(new ArrayCodec());
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
	 *  @return Encoded byte array.
	 */
	public static byte[] writeObjectToByteArray(Object val, ClassLoader classloader)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeObjectToStream(baos, val, classloader);
		return baos.toByteArray();
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
		return writeObjectToStream(os, val, preprocessors, null, usercontext, classloader, null);
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
	public static long writeObjectToStream(OutputStream os, Object val, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> encoderhandlers, Object usercontext, ClassLoader classloader, SerializationConfig config)
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
		IEncodingContext context = new EncodingContext(os, val, usercontext, preprocessors, classloader, config);
		context.writeVarInt(VERSION);
		
		Traverser traverser = new BinaryWriteTraverser();
		//Traverser.traverseObject(val, ENCODER_HANDLERS, false, context);
		traverser.traverse(val, null, preprocessors, encoderhandlers, Traverser.MODE.PREPROCESS, classloader, context);
//		traverser.traverse(val, null, new IdentityHashMap<Object, Object>(), preprocessors, encoderhandlers, null, false, null, context);
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
		
		Traverser traverser = new BinaryWriteTraverser();
//		Traverser traverser = new Traverser()
//		{
//			public void handleDuplicate(Object object, Class<?> clazz, Object match,
//				List<ITraverseProcessor> processors, boolean clone, Object context)
//			{
//				IEncodingContext ec = (IEncodingContext)context;
//				int ref = ((Integer)match).intValue();
//				ec.writeClassname(REFERENCE_MARKER);
//				ec.writeVarInt(ref);
//			}
//			
//			/**
//			 *  Special handling for null objects.
//			 */
//			public Object handleNull(Class<?> clazz,
//				List<ITraverseProcessor> processors, boolean clone, Object context)
//			{
//				IEncodingContext ec = (IEncodingContext)context;
//				ec.writeClassname(NULL_MARKER);
//				return null;
//			}
//		};
		//Traverser.traverseObject(val, ENCODER_HANDLERS, false, context);
		traverser.traverse(val, null, preprocessors, encoderhandlers, Traverser.MODE.PREPROCESS, classloader, context);
		
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
	public static Object readObjectFromByteArray(byte[] val, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		return readObjectFromStream(new ByteArrayInputStream(val), postprocessors, usercontext, classloader, errorreporter, null);
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @param errorreporter The error reporter, may be null in which case the default reporter is used.
	 *  @return The decoded object.
	 */
	public static Object readObjectFromStream(InputStream is, ClassLoader classloader)
	{
		return readObjectFromStream(is, null, null, classloader, null, null);
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @param errorreporter The error reporter, may be null in which case the default reporter is used.
	 *  @return The decoded object.
	 */
	public static Object readObjectFromStream(InputStream is, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
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
		IDecodingContext context = new StreamDecodingContext(is, DECODER_HANDLERS, postprocessors, usercontext, classloader, errorreporter, config);
		int streamver = (int) context.readVarInt();
		if (streamver > VERSION)
		{
			throw new RuntimeException("Version mismatch, stream reported version " + streamver + " should be " + VERSION  + " or lower.");
		}
		context.setVersion(streamver);
		
		return decodeObject(context);
	}
	
	/**
	 *  Convert a byte array to an object.
	 *  @param val The byte array.
	 *  @param usercontext A user context, may be null.
	 *  @param classloader The class loader.
	 *  @param errorreporter The error reporter, may be null in which case the default reporter is used.
	 *  @return The decoded object.
	 */
	public static Object readObjectFromDataInput(DataInput di, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
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
		IDecodingContext context = new DataInputDecodingContext(di, DECODER_HANDLERS, postprocessors, usercontext, classloader, errorreporter, config);
		int streamver = (int) context.readVarInt();
		if (streamver > VERSION)
		{
			throw new RuntimeException("Version mismatch, stream reported version " + streamver + " should be " + VERSION + " or lower.");
		}
		context.setVersion(streamver);
		
		return decodeObject(context);
	}
	
	/**
	 *  Helper method for decoding an object (used for recursion).
	 *  @param context The decoding context.
	 *  @return Decoded object.
	 */
	protected static Object decodeObject(IDecodingContext context)
	{
		String classname = context.readClassname();
		
		Class<?> clazz = null;
		try
		{
			if (classname.equals(NULL_MARKER))
				return null;
			else if (classname.equals(REFERENCE_MARKER))
				return context.getObjectForId(context.readVarInt());
			else
				clazz = SReflect.findClass(classname, null, context.getClassloader());
		}
		catch (ClassNotFoundException e)
		{
			//throw new RuntimeException(e);
			clazz = null;
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
	protected static Object decodeRawObject(Class<?> clazz, IDecodingContext context)
	{
		Object dobject = null;
		List<IDecoderHandler> decoderhandlers = context.getDecoderHandlers();
		IDecoderHandler dechandler = null;
		for (int i = 0; i < decoderhandlers.size(); ++i)
		{
			if (decoderhandlers.get(i).isApplicable(clazz))
			{
				dobject = decoderhandlers.get(i).decode(clazz, context);
				dechandler = decoderhandlers.get(i);
				break;
			}
		}
		
		context.setLastObject(dobject);
		
//		if(dobject!=null && dobject.getClass().getName().indexOf("ProxyReference")!=-1)
////			&& clazz.getName().indexOf("ProxyReference")==-1)
//		{
//			System.out.println("sdghsdgrk");
//		}
		
		if (context.getPostProcessors() != null)
		{
			for (int i = 0; i < context.getPostProcessors().size(); ++i)
			{
				ITraverseProcessor pp = context.getPostProcessors().get(i);
				
				if (pp.isApplicable(context.getLastObject(), clazz, context.getClassloader(), context))
					context.setLastObject(pp.process(context.getLastObject(), clazz, null, context.getPostProcessors(), null, Traverser.MODE.POSTPROCESS, context.getClassloader(), context));
				break;
			}
		}
		
		if (context.getLastObject() != dobject)
		{
			context.setObjectForId(context.getObjectId(dobject), context.getLastObject());
		}
		
		//TODO: Do this with BiHashMap?
//		if (context.getLastObject() != dobject)
//		{
//			int ref = -1;
//			for (Map.Entry<Integer, Object> entry : context.getKnownObjects().entrySet())
//			{
//				if (entry.getValue() == dobject)
//				{
//					ref = entry.getKey();
//					break;
//				}
//			}
//			if (ref > -1)
//			{
//				context.getKnownObjects().put(ref, context.getLastObject());
//			}
//		}
		
		return context.getLastObject();
	}
	
	/** Traverser for writing. */
	protected static class BinaryWriteTraverser extends Traverser
	{
		public Object preemptProcessing(Object inputobject, Type inputtype, Object context)
		{
//			if(canReference(orig, clazz, ec))
//				ec.createObjectId(object);
			IEncodingContext ec = (IEncodingContext)context;
			Long id = ec.getObjectId(inputobject);
			Object ret = null;
			
			if (id != null)
			{
				ret = inputobject;
				ec.writeClassname(REFERENCE_MARKER);
				ec.writeVarInt(id);
			}
			else
				ec.setInputObject(inputobject);
			return ret;
		}
		
		public void finalizeProcessing(Object inputobject, Object outputobject, ITraverseProcessor convproc, ITraverseProcessor proc, Object context)
		{
			IEncodingContext ec = (IEncodingContext)context;
			if (outputobject == null)
				ec.writeClassname(NULL_MARKER);
		}
	}
}
