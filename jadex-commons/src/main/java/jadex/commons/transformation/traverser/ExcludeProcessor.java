package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  The exclude processor allows for excluding specific classes from further traversing.
 *  Similar to the filter processor but based on predefined classes.
 */
public class ExcludeProcessor implements ITraverseProcessor
{
	/** The static excluded types. */
	protected static final Set<Class<?>> excluded;
	
	static
	{
		excluded = new HashSet<Class<?>>();
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
	
	protected static final Class<?>[] excludedsupertypes;
	static
	{
		List<Class<?>> types = new ArrayList<Class<?>>();
		
		// Java 8 stuff
		String[] reflectionclasses = new String[] { "java.time.chrono.ChronoLocalDate",
													"java.time.LocalTime",
													"java.time.chrono.ChronoLocalDateTime" };
		for(int i = 0; i < reflectionclasses.length; ++i)
		{
			try
			{
				Class<?> reflectionclass = Class.forName(reflectionclasses[i]);
				types.add(reflectionclass);
			}
			catch (Exception e)
			{
			}
		}
		
		// Only used when serializing
		types.add(ITransformableObject.class);
		
		excludedsupertypes = types.toArray(new Class<?>[types.size()]);
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		boolean supermatch = false;
		for (int i = 0; i < excludedsupertypes.length && !supermatch; ++i)
		{
			supermatch = SReflect.isSupertype(excludedsupertypes[i], clazz);
		}
		return object == null || supermatch || excluded.contains(clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		return object;
	}
}
