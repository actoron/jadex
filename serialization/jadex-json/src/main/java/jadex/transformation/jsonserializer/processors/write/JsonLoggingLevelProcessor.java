package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Level.class, clazz);
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
		JsonWriteContext wr = (JsonWriteContext)context;
	
		Level level = (Level)object;
		wr.addObject(wr.getCurrentInputObject());
		
		int id = 0;

		// Check for applicable default Level
		while(id < DEFAULT_LEVELS.length && !DEFAULT_LEVELS[id].equals(level))
			++id;
		
		if(id < DEFAULT_LEVELS.length)
		{
			wr.write("{");
			wr.writeNameValue("subclassed", false);
			wr.write(",");
			wr.writeNameValue("id", id);
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			if(wr.isWriteId())
				wr.write(",").writeId();
			wr.write("}");
		}
		else
		{
			// Subclassed Level object
			wr.write("{");
			wr.writeNameValue("subclassed", true);
			wr.write(",");
			wr.writeNameString("name", level.getName());
			wr.write(",");
			wr.writeNameValue("value", level.intValue());
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			if(wr.isWriteId())
				wr.write(",").writeId();
			wr.write("}");
		}
	
		return object;
	}
}
