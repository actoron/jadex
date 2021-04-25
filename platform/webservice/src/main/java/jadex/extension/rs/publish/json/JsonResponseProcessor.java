package jadex.extension.rs.publish.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.AbstractJsonProcessor;
import jadex.transformation.jsonserializer.processors.JsonReadContext;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

/**
 *  jax.rs.Response processor.
 */
public class JsonResponseProcessor extends AbstractJsonProcessor
{
	/**
	 *  Test if the processor is applicable for reading.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return object instanceof JsonObject && SReflect.isSupertype(Response.class, clazz);
	}
	
	/**
	 *  Test if the processor is applicable for writing.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Response.class, clazz);
	}
	
	/**
	 *  Read an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		JsonObject obj = (JsonObject)object;
		Class<?> clazz = SReflect.getClass(type);
		
		int status = obj.getInt("status", 0);
		String entity = obj.getString("entity", null);
		JsonValue hs = obj.get("headers");
		Map<String, Object> headers = (Map)traverser.traverse(hs, Map.class, conversionprocessors, processors, mode, targetcl, context);
		
		ResponseBuilder rb = Response.status(status).entity(entity);
		for(Map.Entry<String, Object> entry: headers.entrySet())
		{
			if(entry.getValue() instanceof Collection)
			{
				for(Object v: (Collection)entry.getValue())
				{
					rb.header(entry.getKey(), (String)v);
				}
			}
		}
		Response ret = rb.build();
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		return ret;
	}
	
	/**
	 *  Write an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(object);
	
		Response r = (Response)object;
		
//		intr.getStatus()
//		r.getStatusInfo()
//		r.getHeaders()
//		r.getEntity()
		
		// todo
		//StatusType st = r.getStatusInfo();
		
		wr.write("{");
		wr.writeNameValue("status", r.getStatus()).write(", ");
		//wr.writeNameString("statusinfo", r.getStatusInfo()).write(", ");
		wr.writeNameString("entity", ""+r.getEntity()).write(", ");
		wr.write("\"headers\":");
		traverser.traverse(r.getHeaders(), Map.class, conversionprocessors, processors, mode, targetcl, context);
		if(wr.isWriteClass())
			wr.write(",").writeClass(IServiceIdentifier.class);
		wr.write("}");
	
		return object;
	}
}

