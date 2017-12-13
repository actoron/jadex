package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  Codec for encoding and decoding Java 8 optional objects.
 *
 */
public class OptionalCodec extends AbstractCodec
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
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return (clazz != null) && OPTIONAL_CLASSNAME.equals(clazz.getName());
	}

	/**
	 * Creates the object during decoding.
	 *
	 * @param clazz   The class of the object.
	 * @param context The decoding context.
	 * @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context) {
		init();
		boolean isPresent = context.readBoolean();
		Object o = null;
		try {
			if (isPresent) {
				Object subobject = SBinarySerializer.decodeObject(context);
				o =	ofMethod.invoke(optionalClass, subobject);
			} else {
				o = emptyMethod.invoke(optionalClass);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return o;
	}


	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, Traverser.MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		init();
		try {
			Boolean isPresent = (Boolean) isPresentMethod.invoke(object, null);
			ec.writeBoolean(isPresent);

			if (isPresent) {
				Object subObject = getMethod.invoke(object, null);
				traverser.doTraverse(subObject, null, preprocessors, processors, mode, targetcl, ec);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return object;
	}

}
