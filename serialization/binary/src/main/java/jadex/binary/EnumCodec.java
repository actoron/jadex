package jadex.binary;

import java.util.List;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	 *  Encode the object.
	 */
	@SuppressWarnings("rawtypes")
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		ec.writeString(((Enum) object).name());
		
		return object;
	}
}
