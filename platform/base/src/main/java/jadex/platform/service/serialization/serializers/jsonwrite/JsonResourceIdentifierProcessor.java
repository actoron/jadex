package jadex.platform.service.serialization.serializers.jsonwrite;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

/**
 *  Json processor for writing Jadex service identifiers.
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
		return SReflect.isSupertype(IResourceIdentifier.class, clazz);
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
//		wr.addObject(traversed, object);
		wr.addObject(object);
	
		IResourceIdentifier rid = (IResourceIdentifier)object;
		
		wr.write("{");
		
		ILocalResourceIdentifier lid = rid.getLocalIdentifier();
		if(lid!=null)
		{
			wr.writeString("localIdentifier").write(":");
			wr.write("{");
			
			boolean first = true;
			
			String hi = lid.getHostIdentifier();
			if(hi!=null)
			{
				wr.writeNameString("hostIdentifier", lid.getHostIdentifier());
				first = false;
			}
			
			IComponentIdentifier cid = lid.getComponentIdentifier();
			if(cid!=null)
			{
				if(!first)
					wr.write(", ");
				wr.writeString("componentIdentifier");
				wr.write(":");
				traverser.traverse(cid, IComponentIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
				first = false;
			}
			
			URI uri = lid.getUri();
			if(uri!=null)
			{
				if(!first)
					wr.write(", ");
				wr.writeString("uri");
				wr.write(":");
				traverser.traverse(uri, URI.class, conversionprocessors, processors, mode, targetcl, context);
			}
			
			wr.write("}");
		}
		
		IGlobalResourceIdentifier gid = rid.getGlobalIdentifier();
		if(gid!=null)
		{
			if(lid!=null)
				wr.write(", ");
			wr.writeString("globalIdentifier").write(":");
			wr.write("{");
			
			boolean first = true;
			
			String ri = gid.getResourceId();
			if(ri!=null)
			{
				wr.writeNameString("resourceId", gid.getResourceId());
				first = false;
			}
			
			URI rif = gid.getRepositoryInfo();
			if(rif!=null)
			{
				if(!first)
					wr.write(", ");
				wr.writeString("repositoryInfo");
				wr.write(":");
				traverser.traverse(rif, IComponentIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
				first = false;
			}
			
			String vi = gid.getVersionInfo();
			if(vi!=null)
			{
				if(!first)
					wr.write(", ");
				wr.writeNameString("versionInfo", vi);
			}
			
			wr.write("}");
		}

		if(wr.isWriteClass())
			wr.write(",").writeClass(IServiceIdentifier.class);
		wr.write("}");
	
		return object;
	}
}
