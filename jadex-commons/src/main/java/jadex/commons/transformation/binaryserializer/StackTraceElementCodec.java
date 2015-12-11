package jadex.commons.transformation.binaryserializer;

import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Codec for encoding and decoding stacktrace element.
 */
public class StackTraceElementCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(StackTraceElement.class, clazz);
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
		return new StackTraceElement((String)BinarySerializer.decodeObject(context), (String)BinarySerializer.decodeObject(context), 
				(String)BinarySerializer.decodeObject(context), (int)context.readSignedVarInt());
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
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors,
		Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		StackTraceElement ste = (StackTraceElement)object;
		traverser.doTraverse(ste.getClassName(), String.class, traversed, processors, clone, ec.getClassLoader(), ec);
		traverser.doTraverse(ste.getMethodName(), String.class, traversed, processors, clone, ec.getClassLoader(), ec);
		traverser.doTraverse(ste.getFileName(), String.class, traversed, processors, clone, ec.getClassLoader(), ec);
		ec.writeSignedVarInt(ste.getLineNumber());
		
		return object;
	}
}

