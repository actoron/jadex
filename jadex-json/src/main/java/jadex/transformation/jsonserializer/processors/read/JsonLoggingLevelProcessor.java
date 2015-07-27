package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.LoggingLevelCodec;
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
		((JsonReadContext)context).addKnownObject(ret);
		
		return ret;
	}
}

