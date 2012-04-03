package jadex.commons.transformation.traverser;

import jadex.commons.gui.SGUI;

import java.awt.Image;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ImageProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object instanceof Image;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		Object ret = object;
		if(clone)
		{
			byte[] data = SGUI.imageToStandardBytes((Image) object, "image/png");
			ret = SGUI.imageFromBytes(data, clazz);
		}
		return ret;
	}
}
