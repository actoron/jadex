package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonSimpleDateFormatProcessor extends JsonBeanProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return object instanceof JsonObject && SReflect.isSupertype(SimpleDateFormat.class, clazz);
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(SimpleDateFormat.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?>	clazz	= JsonPrimitiveObjectProcessor.getClazz(object, targetcl);
		SimpleDateFormat ret = (SimpleDateFormat)getReturnObject(object, clazz, targetcl);
		
		JsonObject obj = (JsonObject)object;
		String pattern = obj.getString("pattern", null);
		ret.applyPattern(pattern);
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		// Include bean properties of superclass DateFormat
		readProperties(object, clazz, conversionprocessors, processors, mode, traverser, targetcl, ret, context, intro);
		
		return ret;
	}
	
	/**
	 *  Add pattern property from toPattern method
	 */
	@Override
	protected void writeProperties(Object object, List<ITraverseProcessor> conversionprocessors,
			List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, JsonWriteContext wr,
			IBeanIntrospector intro, boolean first)
	{
		Class<?> clazz = object.getClass();
		if(!wr.isPropertyExcluded(clazz, "pattern"))
		{	
			Object val = ((SimpleDateFormat)object).toPattern();
			if(val!=null) 
			{
				if(!first)
					wr.write(",");
				first = false;
				wr.writeString("pattern");
				wr.write(":");
				
				traverser.doTraverse(val, String.class, conversionprocessors, processors, mode, targetcl, wr);
			}
		}

		super.writeProperties(object, conversionprocessors, processors, mode, traverser, targetcl, wr, intro, first);
	}
}
