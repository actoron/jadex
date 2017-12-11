package jadex.commons.transformation.traverser;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;

/**
 *  Allows processing java.util.Optional.
 */
public class OptionalProcessor implements ITraverseProcessor
{
	/**
	 * Name of the java 8 optional class.
	 */
	public static final String OPTIONAL_CLASSNAME = "java.util.Optional";

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
		return object.getClass().getName().equals(OPTIONAL_CLASSNAME);
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
		Object result = null;
		if (SCloner.isCloneContext(context)) {
			try {
				Boolean isPresent = (Boolean) isPresentMethod.invoke(object, null);
				if (isPresent) {
					Object subobject = getMethod.invoke(object, null);
					Object newval = traverser.doTraverse(subobject, null, conversionprocessors, processors, mode, targetcl, context);
					if(newval != Traverser.IGNORE_RESULT) {
						result = ofMethod.invoke(optionalClass, newval);
					} else {
						// fallback to not cloning?
						result = ofMethod.invoke(optionalClass, subobject);
					}
				} else {
					result = emptyMethod.invoke(optionalClass, null);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			result = object;
		}
		return result;
	}
}

