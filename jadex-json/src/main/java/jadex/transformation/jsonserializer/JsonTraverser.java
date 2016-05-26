package jadex.transformation.jsonserializer;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.DefaultErrorReporter;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.processors.read.JsonReadContext;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 *  The JsonTraverser converts a preparsed JsonValue object to
 *  a corresponding Java object.
 */
public class JsonTraverser extends Traverser
{
	public final static String  CLASSNAME_MARKER = "__classname";
	public final static String  ID_MARKER = "__id";
	public final static String  REFERENCE_MARKER = "__ref";
	public final static String  ARRAY_MARKER = "__array";
	public final static String  COLLECTION_MARKER = "__collection";

	protected static Traverser writetraverser;
	protected static JsonTraverser readtraverser;

	protected static List<ITraverseProcessor> writeprocs;
	protected static List<ITraverseProcessor> readprocs;
	
	static
	{
		writeprocs = new ArrayList<ITraverseProcessor>();
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonRectangleProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonImageProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonColorProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonDateProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonTimestampProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonTupleProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonInetAddressProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonLogRecordProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonLoggingLevelProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonUUIDProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonClassProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonClassInfoProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonMultiCollectionProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonEnumProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonCertificateProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonArrayProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonStackTraceElementProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonThrowableProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonCalendarProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonCollectionProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonToStringProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonLRUProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonMapProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.write.JsonBeanProcessor());
		
		readprocs = new ArrayList<ITraverseProcessor>();
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonReferenceProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonRectangleProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonImageProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonColorProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonDateProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonTimestampProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonTupleProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonInetAddressProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonLogRecordProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonLoggingLevelProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonUUIDProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonMultiCollectionProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonEnumProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonCertificateProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonStackTraceElementProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonThrowableProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonCalendarProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonCollectionProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonArrayProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonURIProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonURLProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonClassProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonClassInfoProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonPrimitiveObjectProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonLRUProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonMapProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonBeanProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonPrimitiveProcessor());
	}
	
	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @return The objects class.
	 */
	protected Class<?> findClazz(Object object, ClassLoader targetcl)
	{
		return object instanceof JsonObject? findClazzOfJsonObject((JsonObject)object, targetcl): null;
	}
	
	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @return The objects class.
	 */
	public static Class<?> findClazzOfJsonObject(JsonObject object, ClassLoader targetcl)
	{
		Class<?> ret = null;
		String clname = object.getString(CLASSNAME_MARKER, null);
		if(clname!=null)
			ret = SReflect.classForName0(clname, targetcl);
		return ret;
	}
	
	/**
	 * 
	 *  @return
	 */
	protected static synchronized Traverser getWriteTraverser()
	{
		if(writetraverser==null)
		{
			writetraverser = new Traverser()
			{
				public Object handleNull(Class<?> clazz, List<ITraverseProcessor> processors, boolean clone, Object context) 
				{
					JsonWriteContext wr = (JsonWriteContext)context;
					wr.write("null");
					return null;
				}
				
				public void handleDuplicate(Object object, Class<?> clazz, Object match,
					List<ITraverseProcessor> processors, boolean clone, Object context)
				{
					JsonWriteContext wr = (JsonWriteContext)context;
					wr.write("{");
					int ref = ((Integer)match).intValue();
					wr.writeNameValue(REFERENCE_MARKER, ref);
					wr.write("}");
				}
			};
		}
		return writetraverser;
	}
	
	/**
	 * 
	 *  @return
	 */
	protected static synchronized JsonTraverser getReadTraverser()
	{
		if(readtraverser==null)
			readtraverser = new JsonTraverser();
		return readtraverser;
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader)
	{
		return objectToByteArray(val, classloader, null);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc)
	{
		return objectToByteArray(val, classloader, enc, true);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc, boolean writeclass)
	{
		return objectToByteArray(val, classloader, enc, writeclass, null);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc, boolean writeclass, Map<Class<?>, Set<String>> excludes)
	{
		return objectToByteArray(val, classloader, enc, writeclass, null, null);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc, boolean writeclass, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> processors)
	{
		Traverser traverser = getWriteTraverser();
		JsonWriteContext wr = new JsonWriteContext(writeclass, excludes);
		
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			traverser.traverse(val, null, processors!=null? processors: writeprocs, classloader, wr);
			byte[] ret = enc==null? wr.getString().getBytes(): wr.getString().getBytes(enc);
			bos.close();
			return ret;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader)
	{
		return objectToString(val, classloader, true);
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass)
	{
		return objectToString(val, classloader, writeclass, null);
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass, Map<Class<?>, Set<String>> excludes)
	{
		return objectToString(val, classloader, writeclass, null, null);
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> processors)
	{
		String ret = null;
		Traverser traverser = getWriteTraverser();
		JsonWriteContext wr = new JsonWriteContext(writeclass, excludes);
		
		try
		{
			traverser.traverse(val, null, processors, classloader, wr);
			ret = wr.getString();
			return ret;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader)
	{
		return objectFromByteArray(val, classloader, (IErrorReporter)null);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader, String enc)
	{
		return objectFromByteArray(val, classloader, null, enc, null);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep)
	{
		return objectFromByteArray(val, classloader, rep, null, null);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep, String enc, Class<T> clazz)
	{
		try
		{
			return objectFromString(enc==null? new String(val): new String(val, enc), classloader, rep, clazz);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromString(String val, ClassLoader classloader, Class<T> clazz)
	{
		return objectFromString(val, classloader, null, clazz);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromString(String val, ClassLoader classloader, IErrorReporter rep, Class<T> clazz)
	{
		return objectFromString(val, classloader, null, clazz, null);
	}
	
	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromString(String val, ClassLoader classloader, IErrorReporter rep, Class<T> clazz, List<ITraverseProcessor> processors)
	{
		rep = rep==null? DefaultErrorReporter.DEFAULT_ERROR_REPORTER: rep;
		
		try
		{
			JsonValue value = Json.parse(val);
			JsonTraverser traverser = getReadTraverser();
			JsonReadContext rc = new JsonReadContext();
			Object ret = traverser.traverse(value, clazz, processors!=null? processors: readprocs, classloader, rc);
	//		System.out.println("rc: "+rc.knownobjects);
			return (T)ret;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			// System.out.println("Exception writing: "+val);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get a copy of the default read processors.
	 *  @return A copy of the read processor list.
	 */
	public static List<ITraverseProcessor> getDefaultReadProcessorsCopy()
	{
		return new ArrayList<ITraverseProcessor>(readprocs);
	}
	
	/**
	 *  Get a copy of the default read processors.
	 *  @return A copy of the read processor list.
	 */
	public static List<ITraverseProcessor> getDefaultWriteProcessorsCopy()
	{
		return new ArrayList<ITraverseProcessor>(writeprocs);
	}
}

