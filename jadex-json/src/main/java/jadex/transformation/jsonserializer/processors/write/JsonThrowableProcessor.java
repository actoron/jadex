package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonThrowableProcessor implements ITraverseProcessor
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(500);
	
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
		return SReflect.isSupertype(Throwable.class, clazz);
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
		
		Throwable t = (Throwable)object;
		
		wr.write("{");
		
		if(wr.isWriteClass())
			wr.writeClass(object.getClass());
		
		boolean first = true;
		if(t.getMessage()!=null)
		{
			if(wr.isWriteClass())
				wr.write(",");
			wr.write("\"msg\":");
			traverser.doTraverse(t.getMessage(), String.class, traversed, processors, clone, targetcl, context);
			first = false;
		}
		if(t.getCause()!=null)
		{
			if(!first)
				wr.write(",");
			wr.write("\"cause\":");
			Object val = t.getCause();
			traverser.doTraverse(val, val!=null? val.getClass(): Throwable.class, 
				traversed, processors, clone, targetcl, context);
			first = false;
		}
		
		JsonBeanProcessor.traverseProperties(object, traversed, processors, traverser, clone, targetcl, context, intro, first);
		
		wr.write("}");
		
		return object;
	}
}


