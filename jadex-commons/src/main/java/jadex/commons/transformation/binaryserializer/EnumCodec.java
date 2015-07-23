package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding enum objects.
 *
 */
public class EnumCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return clazz != null && clazz.isEnum();
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	@SuppressWarnings("rawtypes")
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Enum ret = Enum.valueOf((Class<Enum>)clazz, context.readString());
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	@SuppressWarnings("rawtypes")
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		ec.writeString(((Enum) object).name());
		
		return object;
	}
}
