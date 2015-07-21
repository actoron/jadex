package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonArray;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

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
		return object instanceof JsonArray && clazz!=null && clazz.isArray();
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
		Object ret = getReturnObject(object, clazz, clone, targetcl);
		Class<?> type = clazz.getComponentType();
		
		traversed.put(object, ret);
		
		JsonArray vals = (JsonArray)object;
		for(int i=0; i<vals.size(); i++)
		{
			Object val = vals.get(i);
			Object newval = traverser.doTraverse(val, type, traversed, processors, clone, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT && (clone || newval!=val))
			{
				Array.set(ret, i, JsonBeanProcessor.convertBasicType(newval, clazz));	
			}
		}
		
		return ret;
	}

	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		if(targetcl!=null)
			clazz	= SReflect.classForName0(SReflect.getClassName(clazz), targetcl);
		
		int length = ((JsonArray)object).size();
		Class<?> type = clazz.getComponentType();
		return Array.newInstance(type, length);
	}
}
