package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *  Codec for encoding and decoding Color objects.
 */
public class ColorCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return Color.class.equals(clazz);
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
		byte[] ccomps = new byte[4]; 
		context.read(ccomps);
		Color ret = new Color(ccomps[0] & 0xFF, ccomps[1] & 0xFF, ccomps[2] & 0xFF, ccomps[3] & 0xFF);
		return ret;
	}
	
//	/**
//	 *  Test if the processor is applicable.
//	 *  @param object The object.
//	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
//	 *    e.g. by cloning the object using the class loaded from the target class loader.
//	 *  @return True, if is applicable. 
//	 */
//	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
//	{
//		Class<?> clazz = SReflect.getClass(type);
//		return isApplicable(clazz);
//	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		Color c = (Color)object;
				
		byte[] ccomps = new byte[4];
		ccomps[0] = (byte)c.getRed();
		ccomps[1] = (byte)c.getGreen();
		ccomps[2] = (byte)c.getBlue();
		ccomps[3] = (byte)c.getAlpha();
		
		ec.write(ccomps);
		
		return object;
	}
}
