package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonArrayProcessor implements ITraverseProcessor
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
		return clazz.isArray();
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
		JsonWriteContext wr = (JsonWriteContext)context;
		
		Class<?> compclazz = clazz.getComponentType();
		if(wr.isWriteClass())
		{
			wr.write("{");
			wr.writeClass(compclazz);
			wr.write(",\"").write(JsonTraverser.ARRAY_MARKER).write("\":");
		}
		
		wr.write("[");
		
		for(int i=0; i<Array.getLength(object); i++) 
		{
			if(i>0)
				wr.write(",");
			Object val = Array.get(object, i);
			traverser.doTraverse(val, val.getClass(), traversed, processors, clone, targetcl, context);
		}
		
		wr.write("]");
		
		if(wr.isWriteClass())
		{
			wr.write("}");
		}
		
//		traversed.put(object, ret);
		
		return object;
	}
}
