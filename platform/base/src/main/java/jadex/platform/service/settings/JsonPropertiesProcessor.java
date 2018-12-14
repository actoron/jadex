package jadex.platform.service.settings;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.JsonReadContext;
import jadex.transformation.jsonserializer.processors.JsonWriteContext;

public class JsonPropertiesProcessor implements ITraverseProcessor
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
		return object instanceof Properties || (object instanceof JsonObject && SReflect.isSupertype(Properties.class, clazz));
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
		Object ret = object;
		if (object instanceof Properties)
		{
			// write
			Properties props = (Properties) object;
			JsonWriteContext wr = (JsonWriteContext)context;
			JsonWriteContext.TryWrite tw = new JsonWriteContext.TryWrite(wr);
			wr.addObject(wr.getCurrentInputObject());
			
			wr.write("{");
			if (props.getName() != null)
				tw.write(",").writeNameString("name", props.getName());
			if (props.getType() != null)
				tw.write(",").writeNameString("type", props.getType());
			if (props.getId() != null)
				tw.write(",").writeNameString("id", props.getId());
			if(wr.isWriteClass())
				tw.write(",").writeClass(object.getClass());
			if(wr.isWriteId())
				tw.write(",").writeId();
			if (props.getProperties() != null)
			{
				tw.write(",");
				wr.writeString("properties");
				wr.write(":");
				traverser.doTraverse(props.getProperties(), null, conversionprocessors, processors, mode, targetcl, context);
			}
			if (props.getSubproperties() != null)
			{
				tw.write(",");
				wr.writeString("subproperties");
				wr.write(":");
				traverser.doTraverse(props.getSubproperties(), null, conversionprocessors, processors, mode, targetcl, context);
			}
			wr.write("}");
		}
		else
		{
			//read
			JsonObject obj = (JsonObject)object;
			String name = obj.getString("name", null);
			String proptype = obj.getString("type", null);
			String id = obj.getString("id", null);
			Properties props = new Properties(name, proptype, id);
			
			if(obj.get("properties")!=null)
				 props.setProperties((Property[]) traverser.doTraverse(obj.get("properties"), Property[].class, conversionprocessors, processors, mode, targetcl, context));
			
			if(obj.get("subproperties")!=null)
				 props.setSubproperties((Properties[]) traverser.doTraverse(obj.get("subproperties"), Properties[].class, conversionprocessors, processors, mode, targetcl, context));
			
			ret = props;
			
			JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			return ret;
		}
		return ret;
	}
}
