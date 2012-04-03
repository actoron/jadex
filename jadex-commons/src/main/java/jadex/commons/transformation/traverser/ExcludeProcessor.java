package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The exclude processor allows for excluding specific classes from further traversing.
 *  Similar to the filter processor but based on predefined classes.
 */
public class ExcludeProcessor implements ITraverseProcessor
{
	/** The static excluded types. */
	protected static final Set excluded;
	
	static
	{
		excluded = new HashSet();
		excluded.add(Boolean.class);
		excluded.add(boolean.class);
		excluded.add(Integer.class);
		excluded.add(int.class);
		excluded.add(Double.class);
		excluded.add(double.class);
		excluded.add(Float.class);
		excluded.add(float.class);
		excluded.add(Long.class);
		excluded.add(long.class);
		excluded.add(Short.class);
		excluded.add(short.class);
		excluded.add(Byte.class);
		excluded.add(byte.class);
		excluded.add(Character.class);
		excluded.add(char.class);
		excluded.add(String.class);
		excluded.add(Class.class);
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object == null || excluded.contains(clazz);
	}
	
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
