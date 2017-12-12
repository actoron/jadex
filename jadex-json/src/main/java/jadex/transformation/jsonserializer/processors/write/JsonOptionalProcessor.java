package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Processor for java util optional.
 */ 
public class JsonOptionalProcessor implements ITraverseProcessor
{

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
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
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, Traverser.MODE mode, ClassLoader targetcl, Object context)
	{
		init();
		JsonWriteContext wr = (JsonWriteContext)context;
		wr.addObject(object);

		wr.write("{");

		boolean first = true;
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}

		try {
			Boolean isPresent = (Boolean) isPresentMethod.invoke(object, null);
			if (!first)
				wr.write(",");
			wr.write("\"isPresent\":");
			traverser.doTraverse(isPresent, Boolean.class, conversionprocessors, processors, mode, targetcl, context);

			Object subobject = null;
			if (isPresent) {
				if (!first)
					wr.write(",");
				wr.write("\"subobject\":");
				subobject = getMethod.invoke(object, null);
				traverser.doTraverse(subobject, subobject.getClass(), conversionprocessors, processors, mode, targetcl, context);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		wr.write("}");

		return object;
	}
}

