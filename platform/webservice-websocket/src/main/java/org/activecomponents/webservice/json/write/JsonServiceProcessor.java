package org.activecomponents.webservice.json.write;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 *  Json processor for writing Jadex services to proxies.
 */
public class JsonServiceProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(IService.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(object);
	
		IService service = (IService)object;
		Set<String> ms = new HashSet<String>();
		Class<?> clazz = service.getServiceId().getServiceType().getType(targetcl);
		while(clazz!=null)
		{
			Method[] methods = clazz.getDeclaredMethods();
		
			for(Method m: methods)
			{
				String name = m.getName();
				ms.add(name);
			}
				
			clazz = clazz.getSuperclass();
		}
		
		wr.write("{");
//		wr.writeNameValue(com.actoron.webservice.json.read.JsonServiceProcessor.SERVICE_MARKER, 0).write(", ");
//		wr.writeNameString("serviceIdentifier", service.getId().toString()).write(", ");
		
		wr.write("\"serviceIdentifier\":");
		traverser.traverse(service.getServiceId(), IServiceIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
		
		wr.write(",");
		wr.write("\"methodNames\":");
		traverser.traverse(ms, Set.class, conversionprocessors, processors, mode, targetcl, context);

		if(wr.isWriteClass())
			wr.write(",").writeClass(IService.class);
		wr.write("}");
	
		return object;
	}
}
