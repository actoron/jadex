package jadex.platform.service.serialization.serializers.jsonread;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Json processor for reading Jadex services to proxies.
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
		return object instanceof JsonObject && SReflect.isSupertype(IService.class, clazz);
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
		JsonObject obj = (JsonObject)object;
		
		IServiceIdentifier sid = (IServiceIdentifier)traverser.traverse(obj.get("serviceIdentifier"), IServiceIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
		
		// todo: fetch platform via context
		// a) via search or b) via map
		IExternalAccess platform = null;
		IService service = platform.searchService(new ServiceQuery<>((Class<IService>)null).setServiceIdentifier(sid)).get();
		
		return service;
	}
}
