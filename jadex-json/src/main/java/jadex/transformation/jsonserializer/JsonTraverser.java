package jadex.transformation.jsonserializer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.processors.read.JsonArrayProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonClassProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonCollectionProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonPrimitiveObjectProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonPrimitiveProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonReadContext;
import jadex.transformation.jsonserializer.processors.read.JsonURIProcessor;
import jadex.transformation.jsonserializer.processors.read.JsonURLProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonBeanProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonCalendarProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonMapProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonToStringProcessor;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 *  The JsonTraverser converts a preparsed JsonValue object to
 *  a corresponding Java object.
 */
public class JsonTraverser extends Traverser
{
	public final static String  CLASSNAME_MARKER = "__classname";
	public final static String  ARRAY_MARKER = "__array";
	public final static String  COLLECTION_MARKER = "__collection";

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
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader)
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonMultiCollectionProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonEnumProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonCertificateProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonArrayProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonStackTraceElementProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonThrowableProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonCalendarProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonCollectionProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonToStringProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonMapProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.write.JsonBeanProcessor());
		Traverser traverser = new Traverser();
		JsonWriteContext wr = new JsonWriteContext(true);
		
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			traverser.traverse(val, null, procs, null, wr);
			byte[] ret = wr.getString().getBytes();
			bos.close();
			return ret;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
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
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader, IErrorReporter rep)
	{
		List<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>();
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonMultiCollectionProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonEnumProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonCertificateProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonStackTraceElementProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonThrowableProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonCalendarProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonCollectionProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonArrayProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonURIProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonURLProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonClassProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonPrimitiveObjectProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonMapProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonBeanProcessor());
		procs.add(new jadex.transformation.jsonserializer.processors.read.JsonPrimitiveProcessor());
		
		JsonValue value = Json.parse(new String(val));
		JsonTraverser traverser = new JsonTraverser();
		Object ret = traverser.traverse(value, null, procs, null, new JsonReadContext());
		return ret;
	}
}

