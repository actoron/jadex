package jadex.transformation.jsonserializer;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser;

/**
 *  The JsonTraverser converts a preparsed JsonValue object to
 *  a corresponding Java object.
 */
public class JsonTraverser extends Traverser
{
	public final static String  CLASSNAME_MARKER = "__classname";

	/**
	 *  Find the class of an object.
	 *  @param object The object.
	 *  @return The objects class.
	 */
	protected Class<?> findClazz(Object object, ClassLoader targetcl)
	{
		Class<?> ret = null;
		if(object instanceof JsonObject)
		{
			JsonObject jo = (JsonObject)object;
			String clname = jo.getString(CLASSNAME_MARKER, null);
			if(clname!=null)
				ret = SReflect.classForName0(clname, targetcl);
		}
		return ret;
	}
}

