package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

public class MethodCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return Method.class.equals(clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class clazz, IDecodingContext context)
	{
		Method ret = null;
		try
		{
			Class<?> methodclass = SReflect.classForName(context.readClassname(), context.getClassloader());
			String methodname = context.readString();
			Class<?>[] params = (Class<?>[]) BinarySerializer.decodeObject(context);
			ret = methodclass.getMethod(methodname, params);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return Method.class.equals(clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, IEncodingContext ec)
	{
		Method method = (Method) object;
		ec.writeClass(method.getDeclaringClass());
		ec.writeString(method.getName());
		Class<?>[] params = method.getParameterTypes();
		traverser.doTraverse(params, params.getClass(), traversed, processors, clone, null, ec);
		
		return object;
	}
}
