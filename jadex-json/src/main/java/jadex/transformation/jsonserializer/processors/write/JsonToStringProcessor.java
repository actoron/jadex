package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonToStringProcessor implements ITraverseProcessor
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
		return SReflect.isStringConvertableType(clazz) 
			|| SReflect.isSupertype(URL.class, clazz)
			|| SReflect.isSupertype(URI.class, clazz);
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
		
		Object ret = object.toString();

		if(object instanceof String)
		{
			wr.writeString(object.toString());
		}
		else
		{
			if(object instanceof URI || object instanceof URL)
				wr.addObject(traversed, object);
			
			if(!wr.isWriteClass())
			{
				wr.write(object.toString());
			}
			else
			{
				wr.write("{");
				wr.writeNameString("value", object.toString());
				wr.write(",");
				wr.writeClass(object.getClass());
				wr.write("}");
			}
		}
		
		return object;
	}
}
