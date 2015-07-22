package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonMultiCollectionProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return SReflect.isSupertype(MultiCollection.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		try
		{
			JsonWriteContext wr = (JsonWriteContext)context;
			MultiCollection<?,?> mc = (MultiCollection<?,?>)object;
			
	//		traversed.put(object, null);
	
			wr.write("{");
			
			wr.write("\"type\":");
			Field typefield = MultiCollection.class.getDeclaredField("type");
			typefield.setAccessible(true);
			Class<?> type = (Class)typefield.get(mc);
			wr.write("\"").write(SReflect.getClassName(type)).write("\"");
			
			wr.write(",\"map\":");
			Field mapfield = MultiCollection.class.getDeclaredField("map");
			mapfield.setAccessible(true);
			Map<?,?> map = (Map<?,?>)mapfield.get(mc);
			traverser.doTraverse(map, map.getClass(), traversed, processors, clone, targetcl, context);
			
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			
			wr.write("}");
			
			return object;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
