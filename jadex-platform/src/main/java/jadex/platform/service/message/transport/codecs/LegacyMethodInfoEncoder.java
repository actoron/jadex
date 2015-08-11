package jadex.platform.service.message.transport.codecs;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.MethodInfo;
import jadex.commons.transformation.binaryserializer.AbstractCodec;
import jadex.commons.transformation.binaryserializer.IDecodingContext;
import jadex.commons.transformation.binaryserializer.IEncodingContext;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

public class LegacyMethodInfoEncoder extends AbstractCodec
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type clazz, boolean clone, ClassLoader targetcl)
	{
		return object instanceof MethodInfo;
	}

	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		throw new UnsupportedOperationException("Encoder mode only.");
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz,
			List<ITraverseProcessor> processors, Traverser traverser,
			Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		ec.writeBoolean(false);
		
		MethodInfo mi = (MethodInfo) object;
		
		ec.writeVarInt(2);
		ec.writeString("name");
		traverser.traverse(mi.getName(), String.class, traversed, processors, clone, ec.getClassLoader(), ec);
		ec.writeString("parameterTypes");
		Class<?>[] paramclasses = mi.getParameterTypes(ec.getClassLoader());
		traverser.traverse(paramclasses, null, traversed, processors, clone, ec.getClassLoader(), ec);
		
		return object;
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
		throw new UnsupportedOperationException("Encoder mode only.");
	}
}
