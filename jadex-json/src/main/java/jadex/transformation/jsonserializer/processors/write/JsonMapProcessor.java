package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonMapProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(Map.class, clazz);
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
		Map map = (Map)object;
		
//		traversed.put(object, null);
		
		wr.write("{");
		boolean first = true;
		
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}
		
		Set keyset = map.keySet();
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		for(int i=0; i<keys.length; i++)
		{
			Object val = map.get(keys[i]);
			Class<?> valclazz = val!=null? val.getClass(): null;
			Object key = keys[i];
//			Class<?> keyclazz = key != null? key.getClass() : null;
			
			if(key!=null && val!=null)
			{
				if(!first)
				{
					wr.write(",");
					first = false;
				}
				// hmm key must be string :-(
				wr.write("\"");
				wr.write(key.toString());
				wr.write("\"");
//				traverser.doTraverse(key, keyclazz, traversed, processors, clone, targetcl, context);
				wr.write(":");
				traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
			}
		}
		
		wr.write("}");
		
		return object;
	}
}
