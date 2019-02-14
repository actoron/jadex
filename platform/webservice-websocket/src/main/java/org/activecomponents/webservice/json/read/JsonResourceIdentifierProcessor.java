package org.activecomponents.webservice.json.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import jadex.bridge.ClassInfo;
import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Json processor for reading Jadex component identifiers.
 */
public class JsonResourceIdentifierProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(IResourceIdentifier.class, clazz);
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
		
		LocalResourceIdentifier lid = null;
		GlobalResourceIdentifier gid = null;

		JsonObject li = (JsonObject)obj.get("localIdentifier");
		if(li!=null)
		{
			IComponentIdentifier cid = null;
			URI uri = null;
			String hi = null;
			if(li.get("hostIdentifier")!=null)
				hi = li.get("hostIdentifier").asString();
			if(li.get("componentIdentifier")!=null)
				cid = (IComponentIdentifier)traverser.traverse(li.get("componentIdentifier"), IComponentIdentifier.class, conversionprocessors,  processors, mode, targetcl, context);
			if(li.get("uri")!=null)
				uri = (URI)traverser.traverse(li.get("uri"), URI.class, conversionprocessors, processors, mode, targetcl, context);
			
			lid = new LocalResourceIdentifier(cid, uri, hi);
		}
		JsonObject gi = (JsonObject)obj.get("globalIdentifier");
		if(gi!=null)
		{
			String id = null;
			URI uri = null;
			String versioninfo = null;
			if(gi.get("resourceId")!=null)
				id = gi.get("resourceId").asString();
			if(gi.get("repositoryInfo")!=null)
				uri = (URI)traverser.traverse(li.get("repositoryInfo"), URI.class, conversionprocessors, processors, mode, targetcl, context);
			if(gi.get("versionInfo")!=null)
				versioninfo = gi.get("versionInfo").asString();
			gid = new GlobalResourceIdentifier(id, uri, versioninfo);
		}
		
		ResourceIdentifier rid = new ResourceIdentifier(lid, gid);
		return rid;
	}
}
