package jadex.commons.transformation.traverser;

import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ColorProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object instanceof Color;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		
		return clone? new Color(((Color)object).getRGB()): object;
	}
}
