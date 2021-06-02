package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  java.util.Optional processor for reading json objects.
 */
public class JsonOptionalProcessor extends AbstractJsonProcessor
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(500);

	/**
	 * Name of the java 8 optional class.
	 */
	protected static final String OPTIONAL_CLASSNAME = "java.util.Optional";

	/**
	 * Cached Class.
	 */
	protected Class<?> optionalClass;
	/**
	 * Cached Method.
	 */
	protected Method ofMethod;
	/**
	 * Cached Method.
	 */
	protected Method emptyMethod;
	/**
	 * Cached Method.
	 */
	protected Method getMethod;
	/**
	 * Cached Method.
	 */
	protected Method isPresentMethod;

	/**
	 * Init caches if not initialized.
	 */
	protected void init() 
	{
		if (optionalClass == null) 
		{
			optionalClass = SReflect.classForName0(OPTIONAL_CLASSNAME, null);
			ofMethod = SReflect.getMethod(optionalClass, "of", new Class[]{Object.class});
			emptyMethod = SReflect.getMethod(optionalClass, "empty", new Class[]{});
			getMethod = SReflect.getMethod(optionalClass, "get", new Class[]{});
			isPresentMethod = SReflect.getMethod(optionalClass, "isPresent", new Class[]{});
		}
	}

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
		return (clazz != null) && OPTIONAL_CLASSNAME.equals(clazz.getName());
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
		return (clazz != null) && OPTIONAL_CLASSNAME.equals(clazz.getName());
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, Traverser.MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		init();
		Object ret = null;
		try 
		{
			JsonObject obj = (JsonObject) object;
			boolean isPresent = obj.getBoolean("isPresent", false);
			if(isPresent) 
			{
				JsonValue subJson = obj.get("subobject");
				Object subObject;
				if(subJson != null) 
				{
					subObject = traverser.traverse(subJson, Object.class, conversionprocessors, processors, mode, targetcl, context);
					ret =	ofMethod.invoke(optionalClass, subObject);
				} 
				else 
				{
					ret = emptyMethod.invoke(optionalClass);
				}
			} 
			else 
			{
				ret = emptyMethod.invoke(optionalClass);
			}

			JsonValue idx = ((JsonObject)object).get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}

		return ret;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, Traverser.MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
		init();
		wr.addObject(object);

		wr.write("{");

		boolean first = true;
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}

		try {
			Boolean isPresent = (Boolean) isPresentMethod.invoke(object);
			if (!first)
				wr.write(",");
			wr.write("\"isPresent\":");
			traverser.doTraverse(isPresent, Boolean.class, conversionprocessors, processors, mode, targetcl, wr);

			Object subobject = null;
			if (isPresent) {
				if (!first)
					wr.write(",");
				wr.write("\"subobject\":");
				subobject = getMethod.invoke(object);
				traverser.doTraverse(subobject, subobject.getClass(), conversionprocessors, processors, mode, targetcl, wr);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		wr.write("}");

		return object;
	}
}
