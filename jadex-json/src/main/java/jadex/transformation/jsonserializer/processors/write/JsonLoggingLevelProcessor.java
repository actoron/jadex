package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.IDecodingContext;
import jadex.commons.transformation.binaryserializer.IEncodingContext;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonLoggingLevelProcessor implements ITraverseProcessor
{	
	protected Level[] DEFAULT_LEVELS = new Level[] 
	{
		Level.OFF,
		Level.SEVERE,
		Level.WARNING,
		Level.INFO,
		Level.CONFIG,
		Level.FINE,
		Level.FINER,
		Level.FINEST,
		Level.ALL
	};
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Level.class, clazz);
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
		JsonWriteContext wr = (JsonWriteContext)context;
	
		Level level = (Level)object;
		
//		traversed.put(object, ret);
		
		int id = 0;

		// Check for applicable default Level
		while(id < DEFAULT_LEVELS.length && !DEFAULT_LEVELS[id].equals(level))
			++id;
		
		if(id < DEFAULT_LEVELS.length)
		{
			wr.write("{\"subclassed\":false,");
			wr.write("\"id\":").write(""+id);
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			wr.write("}");
		}
		else
		{
			// Subclassed Level object
			wr.write("{\"subclassed\":true,");
			wr.write("\"name\":").write(level.getName()).write("\",");
			wr.write("\"value\":").write(""+level.intValue()).write("\"");
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			wr.write("}");
		}
	
		return object;
	}
}
