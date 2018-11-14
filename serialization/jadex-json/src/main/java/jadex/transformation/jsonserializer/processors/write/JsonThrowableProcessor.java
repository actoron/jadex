package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Throwable.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(wr.getCurrentInputObject());
		
		Throwable t = (Throwable)object;
		
		wr.write("{");
		
		boolean first = true;
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}
		
		if(t.getMessage()!=null)
		{
			if(!first)
				wr.write(",");
			wr.write("\"msg\":");
			traverser.doTraverse(t.getMessage(), String.class, conversionprocessors, processors, mode, targetcl, context);
			first = false;
		}
		if(t.getCause()!=null)
		{
			if(!first)
				wr.write(",");
			wr.write("\"cause\":");
			Object val = t.getCause();
			traverser.doTraverse(val, val!=null? val.getClass(): Throwable.class, conversionprocessors, processors, mode, targetcl, context);
			first = false;
		}
		
		JsonBeanProcessor.traverseProperties(object, conversionprocessors, processors, mode, traverser, targetcl, context, intro, first);
		
		wr.write("}");
		
		return object;
	}
}


