package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonPrimitiveObjectProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		boolean ret = false;
		if(object instanceof JsonObject)
		{
			Class<?> cl = getClazz(object, targetcl);
			if(cl!=null)
				ret = SReflect.isStringConvertableType(cl);
		}
		return ret;
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
		Object ret = null;
		
		JsonObject obj = (JsonObject)object;
		Class<?> cl = getClazz(object, targetcl);
		JsonValue oval = obj.get("value");
		
		if(oval.isString())
		{
			String val = oval.asString();
			if(Byte.class.equals(cl) || byte.class.equals(cl))
			{
				ret = Byte.parseByte(val);
			}
			else if(Character.class.equals(cl) || char.class.equals(cl))
			{
				ret = val.charAt(0);
			}
			else if(Integer.class.equals(cl) || int.class.equals(cl))
			{
				ret = Integer.parseInt(val);
			}
			else if(Long.class.equals(cl) || long.class.equals(cl))
			{
				ret = Long.parseLong(val);
			}
			else if(Short.class.equals(cl) || short.class.equals(cl))
			{
				ret = Short.parseShort(val);
			}
			else if(Float.class.equals(cl) || float.class.equals(cl))
			{
				ret = Float.parseFloat(val);
			}
			else if(Double.class.equals(cl) || double.class.equals(cl))
			{
				ret = Double.parseDouble(val);
			}
			else if(Boolean.class.equals(cl) || boolean.class.equals(cl))
			{
				ret = Boolean.parseBoolean(val);
			}
		}
		else if(oval.isBoolean())
		{
			ret = oval.asBoolean();
		}
		else if(oval.isNumber())
		{
			if(Byte.class.equals(cl) || byte.class.equals(cl))
			{
				ret = (byte)oval.asInt();
			}
			else if(Integer.class.equals(cl) || int.class.equals(cl))
			{
				ret = oval.asInt();
			}
			else if(Long.class.equals(cl) || long.class.equals(cl))
			{
				ret = oval.asLong();
			}
			else if(Short.class.equals(cl) || short.class.equals(cl))
			{
				ret = (short)oval.asInt();
			}
			else if(Float.class.equals(cl) || float.class.equals(cl))
			{
				ret = oval.asFloat();
			}
			else if(Double.class.equals(cl) || double.class.equals(cl))
			{
				ret = oval.asDouble();
			}
		}
		else
		{
			throw new RuntimeException("Unknown primitive type");
		}
		
		traversed.put(object, ret);
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected static Class<?> getClazz(Object object, ClassLoader targetcl)
	{
		Class<?> ret = null;
		String clname = (String)((JsonObject)object).getString(JsonTraverser.CLASSNAME_MARKER, null);
		if(clname!=null)
		{
			try
			{
				ret = SReflect.findClass(clname, null, targetcl);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
}

