package jadex.commons.transformation.traverser;

import java.awt.Image;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;

/**
 * 
 */
public class ImageProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		return object instanceof Image;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		if(clone)
		{
			byte[] data = SGUI.imageToStandardBytes((Image) object, "image/png");
			Class<?> clazz = SReflect.getClass(type);
			ret = SGUI.imageFromBytes(data, clazz);
		}
		return ret;
	}
}
