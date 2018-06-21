package jadex.extension.rs.invoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 
 */
public class RSJAXAnnotationHelper
{
	
	private static RSJAXAnnotationHelper INSTANCE = new RSJAXAnnotationHelper();
	
	private static Class<?>[] JAX_ANNOTATIONTYPES = new Class[] 
	{
		javax.ws.rs.GET.class, javax.ws.rs.POST.class, javax.ws.rs.PUT.class, 
		javax.ws.rs.DELETE.class, javax.ws.rs.HEAD.class, javax.ws.rs.OPTIONS.class
	}; 
	
//	private Map<Class<?>,Annotation> jaxToJadexMap = new HashMap<Class<?>, Annotation>();

	public RSJAXAnnotationHelper() {
//		jaxToJadexMap.put(javax.ws.rs.GET.class, new GETimpl());
//		jaxToJadexMap.put(javax.ws.rs.POST.class, new POSTimpl());
//		jaxToJadexMap.put(javax.ws.rs.PUT.class, new PUTimpl());
//		jaxToJadexMap.put(javax.ws.rs.DELETE.class, new DELETEimpl());
//		jaxToJadexMap.put(javax.ws.rs.HEAD.class, new HEADimpl());
//		jaxToJadexMap.put(javax.ws.rs.OPTIONS.class, new OPTIONSimpl());
	}
	
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
	protected Annotation findDeclaredRestType(Method method)
	{
		java.lang.annotation.Annotation ret = null; //super.findDeclaredRestType(method);
		if(ret == null) 
		{
			for(Class annotation : JAX_ANNOTATIONTYPES)
			{
				ret = method.getAnnotation(annotation);
				if(ret != null) 
				{
//					ret = jaxToJadexMap.get(ret.annotationType());
					break;
				}
			}
		}
		return ret;
	}
	
}

//class GETimpl implements GET {
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return GET.class;
//	}
//}
//
//class POSTimpl implements POST {
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return POST.class;
//	}
//}
//
//class PUTimpl implements PUT {
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return PUT.class;
//	}
//}
//
//class DELETEimpl implements DELETE {
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return DELETE.class;
//	}
//}
//
//class HEADimpl implements HEAD {
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return HEAD.class;
//	}
//}
//
//class OPTIONSimpl implements OPTIONS {
//
//	@Override
//	public Class<? extends Annotation> annotationType()
//	{
//		return null;
//	}
//}
