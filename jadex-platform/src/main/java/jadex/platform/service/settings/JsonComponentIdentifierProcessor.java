package jadex.platform.service.settings;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.read.JsonReadContext;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */ 
public class JsonComponentIdentifierProcessor implements ITraverseProcessor
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
		return object instanceof IComponentIdentifier || (object instanceof JsonObject && SReflect.isSupertype(IComponentIdentifier.class, clazz));
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
		Object ret = object;
		if (object instanceof IComponentIdentifier)
		{
			// write
			IComponentIdentifier cid = (IComponentIdentifier) object;
			JsonWriteContext wr = (JsonWriteContext)context;
			JsonWriteContext.TryWrite tw = new JsonWriteContext.TryWrite(wr);
			wr.addObject(wr.getCurrentInputObject());
			
			wr.write("{");
			if(wr.isWriteClass())
				tw.write(",").writeClass(object.getClass());
			if(wr.isWriteId())
				tw.write(",").writeId();
			tw.write(",").writeNameString("cid", cid.getName());
			wr.write("}");
		}
		else
		{
			//read
			JsonObject obj = (JsonObject)object;
			String cidstr = obj.getString("cid", "");
			ret = new BasicComponentIdentifier(cidstr);
			
			JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			return ret;
		}
		return ret;
	}
}
