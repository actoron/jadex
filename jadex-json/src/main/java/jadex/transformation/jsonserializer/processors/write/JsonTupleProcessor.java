package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonTupleProcessor implements ITraverseProcessor
{	
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
		return SReflect.isSupertype(Tuple.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.JsonWriteContext wr = (JsonWriteContext)context;
	
		UUID uuid = (UUID)object;
		
		wr.write("{\"msb\":").write(""+uuid.getMostSignificantBits()).write(",");
		wr.write("\"lsb\":").write(""+uuid.getLeastSignificantBits());
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		wr.write("}");
	
//			traversed.put(object, ret);
	
		return object;g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(traversed, object);

		Object[] entities = ((Tuple)object).getEntities();
		wr.write("{");
		wr.write("\"values\":");
		traverser.doTraverse(entities, entities.getClass(), traversed, processors, clone, targetcl, context);
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		wr.write("}");
	
		return object;
	}
}
