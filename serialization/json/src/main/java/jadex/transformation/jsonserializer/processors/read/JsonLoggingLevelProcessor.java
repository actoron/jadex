package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */
public class JsonLoggingLevelProcessor extends AbstractJsonProcessor
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
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Level.class, clazz);
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
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
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		JsonObject obj = (JsonObject)object;
		
		Level ret;
//		traversed.put(object, ret);

		boolean subcl = obj.getBoolean("subclassed", false);
		
		// Check if default level
		if(!subcl)
		{
			int num = obj.getInt("id", 0);
			ret = DEFAULT_LEVELS[num];
		}
		else
		{
			// Subclassed Level object
			String name = obj.getString("name", null);
			int val = obj.getInt("value", 0);

			try
			{
				// Let's hope the Level subclass has this constructor...
				Class<?> clazz = SReflect.getClass(type);
				Constructor<?> c = clazz.getDeclaredConstructor(new Class[]{String.class, int.class});
				c.setAccessible(true);
				ret = (Level) c.newInstance(new Object[] {name, val} );
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
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

