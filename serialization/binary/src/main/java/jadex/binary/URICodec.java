package jadex.binary;

import java.net.URI;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.IStringConverter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding URI objects.
 *
 */
public class URICodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(URI.class, clazz);
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
		URI ret = null;
		try
		{
			ret = new URI(context.readString());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, IStringConverter converter, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		ec.writeString(((URI)object).toString());
		
		return object;
	}
}
