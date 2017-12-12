package jadex.transformation.jsonserializer.processors.read;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.binaryserializer.BeanIntrospectorFactory;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  java.util.Optional processor for reading json objects.
 */
public class JsonOptionalProcessor implements ITraverseProcessor
{
	/** Bean introspector for inspecting beans. */
	protected IBeanIntrospector intro = BeanIntrospectorFactory.getInstance().getBeanIntrospector(5000);

	/**
	 * Name of the java 8 optional class.
	 */
	private static final String OPTIONAL_CLASSNAME = "java.util.Optional";

	/**
	 * Cached Class.
	 */
	private Class<?> optionalClass;
	/**
	 * Cached Method.
	 */
	private Method ofMethod;
	/**
	 * Cached Method.
	 */
	private Method emptyMethod;
	/**
	 * Cached Method.
	 */
	private Method getMethod;
	/**
	 * Cached Method.
	 */
	private Method isPresentMethod;

	/**
	 * Init caches if not initialized.
	 */
	private void init() {
		if (optionalClass == null) {
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
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
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
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		init();
		Object ret = null;
		try {
			JsonObject obj = (JsonObject) object;
			boolean isPresent = obj.getBoolean("isPresent", false);
			if (isPresent) {
				JsonValue subJson = obj.get("subobject");
				Object subObject;
				if (subJson != null) {
					subObject = traverser.traverse(subJson, Object.class, processors, targetcl, context);
					ret =	ofMethod.invoke(optionalClass, subObject);
				} else {
					ret = emptyMethod.invoke(optionalClass);
				}
			} else {
				ret = emptyMethod.invoke(optionalClass);
			}

			JsonValue idx = ((JsonObject)object).get(JsonTraverser.ID_MARKER);
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ret;
	}
}
