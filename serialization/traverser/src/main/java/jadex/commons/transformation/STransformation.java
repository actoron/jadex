package jadex.commons.transformation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.transformation.annotations.Alias;

/**
 *  Static helper class for transformation issues.
 */
public class STransformation
{
	//-------- constants --------
	
	public static class MediaType
	{
		public static final String APPLICATION_JSON_JADEX = "application/x.json+jadex";
	}
	
	/**
	 *  Static map of known aliases used for decoding.
	 */
	protected static final Map<String, String>	ALIASES	= Collections.synchronizedMap(new HashMap<String, String>());
	
	//-------- methods --------
	
	/**
	 *  Register a class to be able to handle its alias on decoding.
	 *  @return The class name or alias, if any.
	 */
	public static String	registerClass(Class<?> clazz)
	{
		String	ret;
		if(clazz.isAnnotationPresent(Alias.class))
		{
			ret	= clazz.getAnnotation(Alias.class).value();
			ALIASES.put(ret, SReflect.getClassName(clazz));
		}
		else
		{
			ret	= SReflect.getClassName(clazz);
		}
		return ret;
	}
	
	/**
	 *  Get the actual class name for an alias namr.
	 */
	public static String	getClassname(String name)
	{
		if(ALIASES.containsKey(name))
		{
			name	= ALIASES.get(name);
		}
		return name;
	}
}
