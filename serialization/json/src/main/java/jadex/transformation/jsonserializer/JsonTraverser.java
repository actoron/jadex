package jadex.transformation.jsonserializer;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.PrettyPrint;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.DefaultErrorReporter;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.processors.JsonArrayProcessor;
import jadex.transformation.jsonserializer.processors.JsonBeanProcessor;
import jadex.transformation.jsonserializer.processors.JsonBigIntegerProcessor;
import jadex.transformation.jsonserializer.processors.JsonCalendarProcessor;
import jadex.transformation.jsonserializer.processors.JsonCertificateProcessor;
import jadex.transformation.jsonserializer.processors.JsonClassInfoProcessor;
import jadex.transformation.jsonserializer.processors.JsonClassProcessor;
import jadex.transformation.jsonserializer.processors.JsonCollectionProcessor;
import jadex.transformation.jsonserializer.processors.JsonColorProcessor;
import jadex.transformation.jsonserializer.processors.JsonCurrencyProcessor;
import jadex.transformation.jsonserializer.processors.JsonDateProcessor;
import jadex.transformation.jsonserializer.processors.JsonEnumProcessor;
import jadex.transformation.jsonserializer.processors.JsonImageProcessor;
import jadex.transformation.jsonserializer.processors.JsonInetAddressProcessor;
import jadex.transformation.jsonserializer.processors.JsonJsonStringProcessor;
import jadex.transformation.jsonserializer.processors.JsonLRUProcessor;
import jadex.transformation.jsonserializer.processors.JsonLocalDateTimeProcessor;
import jadex.transformation.jsonserializer.processors.JsonLogRecordProcessor;
import jadex.transformation.jsonserializer.processors.JsonLoggingLevelProcessor;
import jadex.transformation.jsonserializer.processors.JsonMapProcessor;
import jadex.transformation.jsonserializer.processors.JsonMultiCollectionProcessor;
import jadex.transformation.jsonserializer.processors.JsonNestedMapProcessor;
import jadex.transformation.jsonserializer.processors.JsonOptionalProcessor;
import jadex.transformation.jsonserializer.processors.JsonPrimitiveObjectProcessor;
import jadex.transformation.jsonserializer.processors.JsonPrimitiveProcessor;
import jadex.transformation.jsonserializer.processors.JsonReadContext;
import jadex.transformation.jsonserializer.processors.JsonRectangleProcessor;
import jadex.transformation.jsonserializer.processors.JsonSimpleDateFormatProcessor;
import jadex.transformation.jsonserializer.processors.JsonStackTraceElementProcessor;
import jadex.transformation.jsonserializer.processors.JsonThrowableProcessor;
import jadex.transformation.jsonserializer.processors.JsonTimestampProcessor;
import jadex.transformation.jsonserializer.processors.JsonToStringProcessor;
import jadex.transformation.jsonserializer.processors.JsonTupleProcessor;
import jadex.transformation.jsonserializer.processors.JsonURIProcessor;
import jadex.transformation.jsonserializer.processors.JsonURLProcessor;
import jadex.transformation.jsonserializer.processors.JsonUUIDProcessor;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

/**
 *  The JsonTraverser converts a preparsed JsonValue object to
 *  a corresponding Java object.
 *  
 *  todo: introduce boolean in traverser that checks if read a json object in map is ok
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

	public static List<ITraverseProcessor> writeprocs;
	public static List<ITraverseProcessor> readprocs;
	public static List<ITraverseProcessor> nestedreadprocs;
	
	static
	{
		writeprocs = new ArrayList<ITraverseProcessor>();
		writeprocs.add(new JsonRectangleProcessor());
		writeprocs.add(new JsonImageProcessor());
		writeprocs.add(new JsonColorProcessor());
		writeprocs.add(new JsonDateProcessor());
		writeprocs.add(new JsonTimestampProcessor());
		writeprocs.add(new JsonTupleProcessor());
		writeprocs.add(new JsonInetAddressProcessor());
		writeprocs.add(new JsonLogRecordProcessor());
		writeprocs.add(new JsonLoggingLevelProcessor());
		writeprocs.add(new JsonUUIDProcessor());
		writeprocs.add(new JsonClassProcessor());
		writeprocs.add(new JsonClassInfoProcessor());
		writeprocs.add(new JsonMultiCollectionProcessor());
		writeprocs.add(new JsonEnumProcessor());
		writeprocs.add(new JsonCertificateProcessor());
		writeprocs.add(new JsonArrayProcessor());
		writeprocs.add(new JsonStackTraceElementProcessor());
		writeprocs.add(new JsonThrowableProcessor());
		writeprocs.add(new JsonCalendarProcessor());
		writeprocs.add(new JsonCurrencyProcessor());
		writeprocs.add(new JsonSimpleDateFormatProcessor());
		writeprocs.add(new JsonCollectionProcessor());
		writeprocs.add(new JsonToStringProcessor());
		writeprocs.add(new JsonLRUProcessor());
		writeprocs.add(new JsonMapProcessor());
		writeprocs.add(new JsonLocalDateTimeProcessor());
		writeprocs.add(new JsonBigIntegerProcessor());
		writeprocs.add(new JsonJsonStringProcessor());
		writeprocs.add(new JsonOptionalProcessor());
		writeprocs.add(new JsonBeanProcessor());
		
		readprocs = new ArrayList<ITraverseProcessor>();
		// JsonArrayProcessor needs to be first, because others don't check array marker:
		readprocs.add(new JsonArrayProcessor());
		//readprocs.add(new read.JsonReferenceProcessor());
		readprocs.add(new JsonRectangleProcessor());
		readprocs.add(new JsonImageProcessor());
		readprocs.add(new JsonColorProcessor());
		readprocs.add(new JsonDateProcessor());
		readprocs.add(new JsonTimestampProcessor());
		readprocs.add(new JsonTupleProcessor());
		readprocs.add(new JsonInetAddressProcessor());
		readprocs.add(new JsonLogRecordProcessor());
		readprocs.add(new JsonLoggingLevelProcessor());
		readprocs.add(new JsonUUIDProcessor());
		readprocs.add(new JsonMultiCollectionProcessor());
		readprocs.add(new JsonEnumProcessor());
		readprocs.add(new JsonCertificateProcessor());
		readprocs.add(new JsonStackTraceElementProcessor());
		readprocs.add(new JsonThrowableProcessor());
		readprocs.add(new JsonCalendarProcessor());
		readprocs.add(new JsonCurrencyProcessor());
		readprocs.add(new JsonSimpleDateFormatProcessor());
		readprocs.add(new JsonCollectionProcessor());
		readprocs.add(new JsonURIProcessor());
		readprocs.add(new JsonURLProcessor());
		readprocs.add(new JsonClassProcessor());
		readprocs.add(new JsonClassInfoProcessor());
		readprocs.add(new JsonPrimitiveObjectProcessor());
		readprocs.add(new JsonLRUProcessor());
		nestedreadprocs = new ArrayList<ITraverseProcessor>(readprocs);
		nestedreadprocs.add(new JsonNestedMapProcessor());
		readprocs.add(new JsonMapProcessor());
		int pos = readprocs.size();
		readprocs.add(new JsonBigIntegerProcessor());
		readprocs.add(new JsonLocalDateTimeProcessor());
		readprocs.add(new JsonOptionalProcessor());
		readprocs.add(new JsonJsonStringProcessor());
		readprocs.add(new JsonBeanProcessor());
		readprocs.add(new JsonPrimitiveProcessor());
		
		for(int i = pos; i < readprocs.size(); ++i)
			nestedreadprocs.add(readprocs.get(i));
	}
	
	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @return The objects class.
	 */
	public Class<?> findClazz(Object object, ClassLoader targetcl)
	{
		return object instanceof JsonObject? findClazzOfJsonObject((JsonObject)object, targetcl): object instanceof JsonValue?null:object!=null?object.getClass():null;
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
		clname = STransformation.getClassname(clname);
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
				public Object preemptProcessing(Object inputobject, Type inputtype, Object context)
				{
					JsonWriteContext wr = (JsonWriteContext)context;
					Object ret = null;
					
					if(wr.isWriteId())
					{
						wr.setCurrentInputObject(inputobject);
						
						Integer ref = wr.getObjectId(inputobject);
						if(ref != null)
						{
							wr.write("{");
							wr.writeNameValue(REFERENCE_MARKER, ref);
							wr.write("}");
							ret = inputobject;
						}
					}
					return ret;
				}

				public void finalizeProcessing(Object inputobject, Object outputobject, ITraverseProcessor convproc, ITraverseProcessor proc, Object context)
				{
					if (outputobject == null)
					{
						JsonWriteContext wr = (JsonWriteContext)context;
						wr.write("null");
					}
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
			readtraverser = new JsonTraverser()
			{
				public Object preemptProcessing(Object inputobject, Type inputtype, Object context)
				{
					JsonReadContext jrc = (JsonReadContext) context;
					Object ret = null;
					if(inputobject instanceof JsonObject && ((JsonObject)inputobject).get(JsonTraverser.REFERENCE_MARKER)!=null)
					{
						JsonObject obj = (JsonObject)inputobject;
						int num = obj.getInt(JsonTraverser.REFERENCE_MARKER, 0);
						ret = jrc.getKnownObject(num);
					}

					jrc.pushIdStack();
					return ret;
				}

				public void finalizeProcessing(Object inputobject, Object outputobject, ITraverseProcessor convproc, ITraverseProcessor proc, Object context)
				{
					JsonReadContext jrc = (JsonReadContext) context;
					Integer idx = jrc.popIdStack();
					if(convproc != null)
					{
						if (idx != null)
							jrc.setKnownObject(idx, outputobject);
					}
				}
			};
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
		return objectToByteArray(val, classloader, enc, writeclass, true, excludes, null, processors);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc, boolean writeclass, boolean writeid, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors)
	{
		return objectToByteArray(val, classloader, enc, writeclass, writeid, excludes, conversionprocessors, conversionprocessors, null);
	}

	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader, String enc, boolean writeclass, boolean writeid, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, Object usercontext)
	{
		Traverser traverser = getWriteTraverser();
		JsonWriteContext wr = new JsonWriteContext(writeclass, writeid, excludes);
		wr.setUserContext(usercontext);

		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			traverser.traverse(val, null, conversionprocessors, processors!=null? processors: writeprocs, Traverser.MODE.PREPROCESS, classloader, wr);
			byte[] ret = enc==null? wr.getString().getBytes(SUtil.UTF8): wr.getString().getBytes(enc);
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
		return objectToString(val, classloader, writeclass, excludes, null);
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> processors)
	{
		return objectToString(val, classloader, writeclass, excludes, null, processors);
	}

	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors)
	{
		return objectToString(val, classloader, writeclass, true, excludes, null, processors);
	}

	/**
	 *  Convert to a string.
	 */
	public static String objectToString(Object val, ClassLoader classloader, boolean writeclass, boolean writeid, Map<Class<?>, Set<String>> excludes, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors)
	{
		String ret = null;
		Traverser traverser = getWriteTraverser();
		JsonWriteContext wr = new JsonWriteContext(writeclass, writeid, excludes);

		try
		{
			traverser.traverse(val, null, preprocessors, processors!=null? processors: writeprocs, Traverser.MODE.PREPROCESS, classloader, wr);
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
		return objectFromByteArray(val, classloader, rep, enc, clazz, null, null);
	}

	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep, String enc, Class<T> clazz,  List<ITraverseProcessor> procs, List<ITraverseProcessor> postprocs)
	{
		return objectFromByteArray(val, classloader, rep, enc, clazz, procs, postprocs, null);
	}

	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep, String enc, Class<T> clazz,  List<ITraverseProcessor> procs, List<ITraverseProcessor> postprocs, Object usercontext)
	{
		try
		{
			return objectFromString(enc==null? new String(val, SUtil.UTF8): new String(val, enc), classloader, rep, clazz, procs, postprocs, usercontext);
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
		return objectFromString(val, classloader, rep, clazz, processors, null);
	}

	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	public static <T> T objectFromString(String val, ClassLoader classloader, IErrorReporter rep, Class<T> clazz, List<ITraverseProcessor> processors, List<ITraverseProcessor> postprocessors)
	{
		return objectFromString(val, classloader, rep, clazz, processors, postprocessors, null);
	}

	/**
	 *  Convert a byte array (of an xml) to an object.
	 *  @param val The byte array.
	 *  @param classloader The class loader.
	 *  @return The decoded object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T objectFromString(String val, ClassLoader classloader, IErrorReporter rep, Class<T> clazz, List<ITraverseProcessor> processors, List<ITraverseProcessor> postprocessors, Object usercontext)
	{
		rep = rep==null? DefaultErrorReporter.DEFAULT_ERROR_REPORTER: rep;
		
		try
		{
			JsonValue value = Json.parse(val);
			JsonTraverser traverser = getReadTraverser();
			JsonReadContext rc = new JsonReadContext();
			rc.setUserContext(usercontext);
			Object ret = traverser.traverse(value, clazz, postprocessors, processors!=null? processors: readprocs, Traverser.MODE.POSTPROCESS, classloader, rc);
//			Object ret = traverser.traverse(value, clazz, null, processors!=null? processors: readprocs, postprocessors, classloader, rc);
	//		System.out.println("rc: "+rc.knownobjects);
			return (T)ret;
		}
		catch (Exception e)
		{
			throw SUtil.convertToRuntimeException(e);
		}
	}
	
	/**
	 *  Prettifies a JSON source.
	 *  
	 *  @param json The JSON.
	 *  @return Prettified JSON or the original JSON on error.
	 */
	public static String prettifyJson(String json)
	{
		try
		{
			JsonValue jv = Json.parse(json);
			PrettyPrint pp = PrettyPrint.indentWithTabs();
			StringWriter sw = new StringWriter();
			jv.writeTo(sw, pp);
			json = sw.toString();
		}
		catch (Exception e)
		{
		}
		return json;
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

