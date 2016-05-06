package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonCollectionProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(Collection.class, clazz);
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
		wr.addObject(traversed, object);
		
		Class<?> clazz = SReflect.getClass(type);
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			wr.write("{");
			if(wr.isWriteClass())
			{
				wr.writeClass(clazz);
				wr.write(",");
			}
			if(wr.isWriteId())
			{
				wr.writeId();
				wr.write(",");
			}
			wr.writeString(JsonTraverser.COLLECTION_MARKER);
			wr.write(":");
		}
	
		wr.write("[");
		
		Collection<?> col = (Collection<?>)object;
		
		Iterator<?> it = col.iterator();
		for(int i=0; i<col.size(); i++) 
		{
			if(i>0)
				wr.write(",");
			Object val = it.next();
			traverser.doTraverse(val, val.getClass(), traversed, processors, clone, targetcl, context);
		}
		
		wr.write("]");
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			wr.write("}");
		}
		
		return object;
	}
}
