package jadex.platform.service.serialization.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.IStringConverter;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.platform.service.serialization.serializers.jsonread.JsonComponentIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonResourceIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceProcessor;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.JsonReadContext;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

/**
 *  The Jadex JSON serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexJsonSerializer implements ISerializer, IStringConverter
{
	//-------- constants --------
	
	/** The serializer id. */
	public static final int SERIALIZER_ID = 1;
	
	public static final String TYPE = IStringConverter.TYPE_JSON;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/** The write processors. */
	public List<ITraverseProcessor> writeprocs;
	
	/** The read processors. */
	public List<ITraverseProcessor> readprocs;
	
	/**
	 *  Create a new serializer.
	 */
	public JadexJsonSerializer()
	{
		writeprocs = Collections.synchronizedList(new ArrayList<ITraverseProcessor>());
		writeprocs.add(new JsonByteArrayWriteProcessor());
		writeprocs.add(new JsonResourceIdentifierProcessor());
		writeprocs.add(new JsonServiceIdentifierProcessor());
		writeprocs.add(new JsonServiceProcessor());
		writeprocs.addAll(JsonTraverser.writeprocs);
		
		readprocs = Collections.synchronizedList(new ArrayList<ITraverseProcessor>());
		readprocs.add(new JsonByteArrayReadProcessor());
		readprocs.add(new JsonComponentIdentifierProcessor());
		readprocs.add(new jadex.platform.service.serialization.serializers.jsonread.JsonResourceIdentifierProcessor());
		readprocs.add(new jadex.platform.service.serialization.serializers.jsonread.JsonServiceIdentifierProcessor());
		readprocs.add(new jadex.platform.service.serialization.serializers.jsonread.JsonServiceProcessor());
		readprocs.addAll(JsonTraverser.readprocs);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext)
	{
		boolean writeclass = true;
		boolean writeid = true;
		
		if(usercontext instanceof Map)
		{
			Map conv = (Map)usercontext;
			writeclass = conv.get("writeclass") instanceof Boolean? (Boolean)conv.get("writeclass"): true;
			writeid = conv.get("writeid") instanceof Boolean? (Boolean)conv.get("writeid"): true;
		}
		
		byte[] ret = JsonTraverser.objectToByteArray(val, classloader, null, writeclass, writeid, null, preprocs!=null?Arrays.asList(preprocs):null, writeprocs, usercontext);
		
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret, SUtil.UTF8)));
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		if(DEBUG)
			System.out.println("decode message: "+(new String((byte[])bytes, SUtil.UTF8)));
		return JsonTraverser.objectFromByteArray(bytes, classloader, rep, null, null, readprocs, postprocs!=null?Arrays.asList(postprocs):null, usercontext);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		
		byte[] bytes = null;
		try
		{
			bytes = SUtil.readStream(is);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		
		return decode(bytes, classloader, postprocs, rep, usercontext);
	}
	
	/**
	 *  Convert a string to an object.
	 *  @param val The string.
	 *  @param type The target type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public Object convertString(String val, Class<?> type, ClassLoader cl, Object context)
	{
		return JsonTraverser.objectFromString(val, cl, null, type, readprocs, null, context);
	}
	
	/**
	 *  Convert an object to a string.
	 *  @param val The object.
	 *  @param type The encoding type.
	 *  @param context The context.
	 *  @return The object.
	 */
	public String convertObject(Object val, Class<?> type, ClassLoader cl, Object context)
	{
		// does not use type currently?!
		return JsonTraverser.objectToString(val, cl, true, true, null, null, writeprocs, context);
	}
	
	/**
	 *  Get the type of string that can be processed (xml, json, plain).
	 *  @return The object.
	 */
	public String getType()
	{
		return TYPE;
	}
	
	protected static class JsonByteArrayWriteProcessor implements ITraverseProcessor
	{
		public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
		{
			return object instanceof byte[];
		}
		
		public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
		{
			JsonWriteContext wr = (JsonWriteContext)context;
			wr.addObject(wr.getCurrentInputObject());
			
			wr.write("{");
			
//			wr.writeClass(byte[].class);
			if(wr.isWriteClass())
				wr.write("\"").write(JsonTraverser.CLASSNAME_MARKER).write("\":\"bytearray\",");
			
			if(wr.isWriteId())
			{
				wr.writeId();
				wr.write(",");
			}
			
			wr.write("\"__base64\":");
			wr.write("\"" +new String(Base64.encode((byte[])object), SUtil.UTF8)+ "\"");
			wr.write("}");
			return object;
		}
	}
	
	protected static class JsonByteArrayReadProcessor implements ITraverseProcessor
	{
		public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
		{
			Class<?> clazz = SReflect.getClass(type);
			return object instanceof JsonObject && (SReflect.isSupertype(byte[].class, clazz) || SReflect.isSupertype(Byte[].class, clazz));
			/*boolean ret = false;
			if(object instanceof JsonObject)
			{
				JsonValue c = ((JsonObject)object).get(JsonTraverser.CLASSNAME_MARKER);
				String cl = c!=null? c.asString(): null;
				ret = "bytearray".equals(cl);
			}
			return ret;*/
		}

		public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
		{
			JsonObject obj = (JsonObject)object;
			
			byte[] ret = Base64.decode(obj.get("__base64").asString().getBytes(SUtil.UTF8));
			
			JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			return ret;
		}
	}
	
	/**
	 *  Add a read/write processor pair.
	 */
	public void addProcessor(ITraverseProcessor read, ITraverseProcessor write)
	{
		readprocs.add(0, read);
		writeprocs.add(0, write);
	}
	
	/**
	 *  Test if the type can be converted.
	 *  @param clazz The class.
	 *  @return True if can be converted.
	 */
	public boolean isSupportedType(Class<?> clazz)
	{
		return true;
	}
}