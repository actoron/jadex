package jadex.commons.transformation.binaryserializer;

import jadex.commons.MethodInfo;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.List;
import java.util.Map;

public class LegacyMethodInfoEncoder implements ITraverseProcessor
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
		return object instanceof MethodInfo;
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
		EncodingContext ec = (EncodingContext) context;
		
		traversed.put(object, traversed.size());
		
		ec.writeClass(MethodInfo.class);
		
		MethodInfo mi = (MethodInfo) object;
		
		ec.writeVarInt(2);
		ec.writeString("name");
		traverser.doTraverse(mi.getName(), String.class, traversed, processors, clone, targetcl, context);
		ec.writeString("ParameterTypes");
		Class<?>[] paramclasses = mi.getParameterTypes(targetcl);
		traverser.doTraverse(paramclasses, null, traversed, processors, clone, targetcl, context);
		
		return object;
	}
}
