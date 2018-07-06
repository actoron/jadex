package org.activecomponents.webservice.json.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Json processor for reading Jadex services to proxies.
 */
public class JsonServiceIdentifierProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(IServiceIdentifier.class, clazz);
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
		
		IComponentIdentifier pid = (IComponentIdentifier)traverser.traverse(obj.get("providerId"), IComponentIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
		IResourceIdentifier rid = (IResourceIdentifier)traverser.traverse(obj.get("resourceIdentifier"), IResourceIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
		
		// todo: supertypes, networknames, unrestricted???
//		public ServiceIdentifier(IComponentIdentifier providerid, ClassInfo type, ClassInfo[] supertypes, String servicename, IResourceIdentifier rid, String scope, Set<String> networknames, boolean unrestricted)
		ServiceIdentifier sid = new ServiceIdentifier(pid, new ClassInfo(obj.get("type").asString()), null, obj.get("name").asString(), rid, obj.get("scope").asString(), null, false);
		return sid;
	}
}
