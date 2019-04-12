package jadex.extension.rs.publish.binary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import jadex.binary.AbstractCodec;
import jadex.binary.BeanCodec;
import jadex.binary.IDecodingContext;
import jadex.binary.IEncodingContext;
import jadex.binary.SBinarySerializer;
import jadex.commons.SReflect;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding response objects.
 */
public class BinaryResponseProcessor extends AbstractCodec
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(500);
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Response.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Response ret = null;
		int status = (int)context.readVarInt();
		String entity = context.readString();
		
		Map<String, Object> headers = (Map)SBinarySerializer.decodeObject(context);
		
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
		ret = rb.build();
		
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return isApplicable(clazz);
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Response r = (Response)object;
		
		ec.writeVarInt(r.getStatus());
		ec.writeString(""+r.getEntity());
		
		MultivaluedMap<String, Object> hs = r.getHeaders();
		traverser.doTraverse(hs, Map.class, preprocessors, processors, mode, targetcl, ec);

		BeanCodec.writeBeanProperties(object, clazz, preprocessors, processors, traverser, mode, ec, intro);
		
		return object;
	}

}