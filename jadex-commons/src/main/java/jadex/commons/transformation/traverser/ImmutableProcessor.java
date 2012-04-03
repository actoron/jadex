package jadex.commons.transformation.traverser;


import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * 
 */
public class ImmutableProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object instanceof Enum || object instanceof URL || object instanceof Level 
			|| object instanceof InetAddress;
	}
//	SReflect.isSupertype(Enum.class, object.getClass())
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		return object;
	}
}
