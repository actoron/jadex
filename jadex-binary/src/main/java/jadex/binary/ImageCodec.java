package jadex.binary;

import java.awt.Image;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.ImageProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;


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
		
		// This is correct (encoding during creation) because this byte array
		// is a technical object specific to the image and not part of the object graph proper.
		byte[] encimage = (byte[])SBinarySerializer.decodeObject(context);
		ret = ImageProcessor.imageFromBytes(encimage, clazz);
		
		return ret;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		byte[] encimg = ImageProcessor.imageToStandardBytes((Image)object, "image/png");
		traverser.doTraverse(encimg, encimg.getClass(), preprocessors, processors, mode, targetcl, ec);
		
		return object;
	}
}
