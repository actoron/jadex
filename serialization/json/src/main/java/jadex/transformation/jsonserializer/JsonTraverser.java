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
import jadex.transformation.jsonserializer.processors.JsonReadContext;
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
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonRectangleProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonImageProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonColorProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonDateProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonTimestampProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonTupleProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonInetAddressProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonLogRecordProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonLoggingLevelProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonUUIDProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonClassProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonClassInfoProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonMultiCollectionProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonEnumProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonCertificateProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonArrayProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonStackTraceElementProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonThrowableProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonCalendarProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonCurrencyProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonSimpleDateFormatProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonCollectionProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonToStringProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonLRUProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonMapProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonLocalDateTimeProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonBigIntegerProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonOptionalProcessor());
		writeprocs.add(new jadex.transformation.jsonserializer.processors.JsonBeanProcessor());
		
		readprocs = new ArrayList<ITraverseProcessor>();
		// JsonArrayProcessor needs to be first, because others don't check array marker:
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonArrayProcessor());
		//readprocs.add(new jadex.transformation.jsonserializer.processors.read.JsonReferenceProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonRectangleProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonImageProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonColorProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonDateProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonTimestampProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonTupleProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonInetAddressProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonLogRecordProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonLoggingLevelProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonUUIDProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonMultiCollectionProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonEnumProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonCertificateProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonStackTraceElementProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonThrowableProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonCalendarProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonCurrencyProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonSimpleDateFormatProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonCollectionProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonURIProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonURLProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonClassProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonClassInfoProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonPrimitiveObjectProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonLRUProcessor());
		nestedreadprocs = new ArrayList<ITraverseProcessor>(readprocs);
		nestedreadprocs.add(new jadex.transformation.jsonserializer.processors.JsonNestedMapProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonMapProcessor());
		int pos = readprocs.size();
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonBigIntegerProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonLocalDateTimeProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonOptionalProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonBeanProcessor());
		readprocs.add(new jadex.transformation.jsonserializer.processors.JsonPrimitiveProcessor());
		
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

