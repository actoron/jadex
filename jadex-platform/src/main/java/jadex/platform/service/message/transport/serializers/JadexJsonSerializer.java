package jadex.platform.service.message.transport.serializers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.read.JsonReadContext;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 *  The Jadex JSON serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexJsonSerializer implements ISerializer
{
	//-------- constants --------
	
	/** The serializer id. */
	public static final int SERIALIZER_ID = 1;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	public static List<ITraverseProcessor> writeprocs;
	public static List<ITraverseProcessor> readprocs;
	static
	{
		writeprocs = new ArrayList<ITraverseProcessor>();
		writeprocs.add(new JsonByteArrayWriteProcessor());
		writeprocs.addAll(JsonTraverser.writeprocs);
		
		readprocs = new ArrayList<ITraverseProcessor>();
		readprocs.add(new JsonByteArrayReadProcessor());
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
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs)
	{
		byte[] ret = JsonTraverser.objectToByteArray(val, classloader, null, true, true, null, preprocs!=null?Arrays.asList(preprocs):null, writeprocs);
		
		if(DEBUG)
		{
			System.out.println("encode message: "+(new String(ret, SUtil.UTF8)));
		}
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep)
	{
		if(DEBUG)
		{
			System.out.println("decode message: "+(new String((byte[])bytes, SUtil.UTF8)));
		}
		return JsonTraverser.objectFromByteArray(bytes, classloader, rep, null, null, readprocs, postprocs!=null?Arrays.asList(postprocs):null);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep)
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
		
		
		return decode(bytes, classloader, postprocs, rep);
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
			wr.write("\"").write(JsonTraverser.CLASSNAME_MARKER).write("\":\"bytearray\",");
			
			if(wr.isWriteId())
			{
				wr.writeId();
				wr.write(",");
			}
			
			wr.write("\"__base64\":");
			wr.write("\"" + Base64.encode((byte[]) object) + "\"");
			
			wr.write("}");
			return object;
		}
	}
	
	protected static class JsonByteArrayReadProcessor implements ITraverseProcessor
	{
		public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
		{
			return (object instanceof JsonObject) && ("bytearray".equals(((JsonObject) object).get(JsonTraverser.CLASSNAME_MARKER).asString()));
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
}