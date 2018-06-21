package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return clazz!=null && clazz.isArray();
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
		
		Class<?> clazz = SReflect.getClass(type);
		Class<?> compclazz = clazz.getComponentType();
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			// just increase reference count because these helper objects do not count on read side
//			wr.incObjectCount();
			wr.write("{");
			if(wr.isWriteClass())
			{
				wr.writeClass(compclazz);
				wr.write(",");
			}
			if(wr.isWriteId())
			{
				wr.writeId();
				wr.write(",");
			}
			wr.writeString(JsonTraverser.ARRAY_MARKER);
			wr.write(":");
		}
		
		wr.write("[");
		
		for(int i=0; i<Array.getLength(object); i++) 
		{
			if(i>0)
				wr.write(",");
			Object val = Array.get(object, i);
			traverser.doTraverse(val, val!=null? val.getClass(): null, conversionprocessors, processors, mode, targetcl, context);
		}
		
		wr.write("]");
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			wr.write("}");
		}
		
		return object;
	}
}
