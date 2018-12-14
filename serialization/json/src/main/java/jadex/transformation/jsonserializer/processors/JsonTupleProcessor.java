package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonTupleProcessor extends AbstractJsonProcessor
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
		return SReflect.isSupertype(Tuple.class, clazz);
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
		return SReflect.isSupertype(Tuple.class, clazz);
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
		Class<?> clazz = SReflect.getClass(type);
		JsonObject obj = (JsonObject)object;
		
		Tuple ret = null;
		if(clazz.equals(Tuple3.class))
		{
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Tuple3 t3 = new Tuple3(null, null, null);
			ret = t3;
		}
		else if (clazz.equals(Tuple2.class))
		{
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Tuple2 t2 = new Tuple2(null, null);
			ret = t2;
		}
		else
		{
			ret =  new Tuple(null);
		}
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		Object[] entities = (Object[])traverser.doTraverse(obj.get("values"), Object[].class, conversionprocessors, processors, mode, targetcl, context);
//		Object[] entities = (Object[])traverser.doTraverse(obj.get("values"), Object[].class, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
		try
		{
			Field fentities = SReflect.getField(ret.getClass(), "entities");
			fentities.setAccessible(true);
			fentities.set(ret, entities);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
		wr.addObject(wr.getCurrentInputObject());

		Object[] entities = ((Tuple)object).getEntities();
		wr.write("{");
		wr.write("\"values\":");
		traverser.doTraverse(entities, entities.getClass(), conversionprocessors, processors, mode, targetcl, wr);
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		if(wr.isWriteId())
			wr.write(",").writeId();
		wr.write("}");
	
		return object;
	}
}
