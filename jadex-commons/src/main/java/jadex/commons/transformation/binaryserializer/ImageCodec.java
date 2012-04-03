package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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
	public boolean isApplicable(Class clazz)
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
	public Object createObject(Class clazz, DecodingContext context)
	{
		Image ret = null;
		
		// This is correct because this byte array is a technical object specific to the image and
		// is not part of the object graph proper.
		byte[] encimage = (byte[]) BinarySerializer.decodeObject(context);
		ret = SGUI.imageFromBytes(encimage, clazz);
		
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		byte[] encimg = SGUI.imageToStandardBytes((Image) object, "image/png");
		traverser.traverse(encimg, encimg.getClass(), traversed, processors, clone, ec);
		
		return object;
	}
}
