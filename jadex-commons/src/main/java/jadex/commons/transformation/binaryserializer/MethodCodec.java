package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.Method;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
			Class<?>[] params = (Class<?>[]) SBinarySerializer.decodeObject(context);
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
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Method method = (Method) object;
		ec.writeClass(method.getDeclaringClass());
		ec.writeString(method.getName());
		Class<?>[] params = method.getParameterTypes();
		traverser.doTraverse(params, params.getClass(), preprocessors, processors, null, null, ec);
		
		return object;
	}
}
