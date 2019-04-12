package jadex.platform.service.serialization.serializers.jsonwrite;

import java.lang.reflect.Type;
import java.util.List;

import jadex.bridge.IComponentIdentifier;
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
		return SReflect.isSupertype(IServiceIdentifier.class, clazz);
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
	
		IServiceIdentifier sid = (IServiceIdentifier)object;
		
		wr.write("{");
		wr.writeNameString("name", sid.getServiceName()).write(", ");
		wr.writeNameString("scope", sid.getScope().name()).write(", ");
		wr.writeNameString("type", sid.getServiceType().getTypeName()).write(", ");
		wr.write("\"providerId\":");
		traverser.traverse(sid.getProviderId(), IComponentIdentifier.class, conversionprocessors, processors, mode, targetcl, context);
		wr.write(", ");
		wr.write("\"resourceIdentifier\":");
		traverser.traverse(sid.getResourceIdentifier(), IResourceIdentifier.class, conversionprocessors, processors, mode, targetcl, context);

		if(wr.isWriteClass())
			wr.write(",").writeClass(IServiceIdentifier.class);
		wr.write("}");
	
		return object;
	}
}
