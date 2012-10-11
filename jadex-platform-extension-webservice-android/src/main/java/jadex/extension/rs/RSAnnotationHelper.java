package jadex.extension.rs;

import jadex.extension.rs.annotations.DELETE;
import jadex.extension.rs.annotations.GET;
import jadex.extension.rs.annotations.HEAD;
import jadex.extension.rs.annotations.OPTIONS;
import jadex.extension.rs.annotations.POST;
import jadex.extension.rs.annotations.PUT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Helper class to extract annotations.
 */
public class RSAnnotationHelper
{
	private static RSAnnotationHelper INSTANCE = new RSAnnotationHelper();
	
	private static Class<?>[] POSSIBLE_ANNOTATIONTYPES = new Class[] {
		GET.class, POST.class, PUT.class, DELETE.class, HEAD.class, OPTIONS.class
	};
	/**
 	 *  Get the declared rest type.
	 *  @param method The method.
	 *  @return The rest type.
	 */
	public static Class<?> getDeclaredRestType(Method method)
	{
		Annotation result = INSTANCE.findDeclaredRestType(method);
		return result==null? null: result.annotationType();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Annotation findDeclaredRestType(Method method) {
		Annotation ret = null;
		
		for (Class annotation : POSSIBLE_ANNOTATIONTYPES)
		{
			ret = method.getAnnotation(annotation);
			if (ret != null) {
				break;
			}
		}
		return ret;
	}
}
