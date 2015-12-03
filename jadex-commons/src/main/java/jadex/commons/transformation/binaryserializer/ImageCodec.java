package jadex.commons.transformation.binaryserializer;

import java.awt.Image;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;


/**
 *  Codec for encoding and decoding Image objects.
 *
 */
public class ImageCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Image.class, clazz);
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
		Image ret = null;
		
		// This is correct because this byte array is a technical object specific to the image and
		// is not part of the object graph proper.
		byte[] encimage = (byte[])BinarySerializer.decodeObject(context);
		ret = SGUI.imageFromBytes(encimage, clazz);
		
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
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		byte[] encimg = SGUI.imageToStandardBytes((Image)object, "image/png");
		traverser.doTraverse(encimg, encimg.getClass(), traversed, processors, clone, null, ec);
		
		return object;
	}
}
