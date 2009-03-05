package jadex.commons.xml;

import java.util.HashSet;
import java.util.Set;

/**
 *  Converter for basic types.
 */
public class BasicTypeConverter
{
	//-------- static part --------
	
	/** The built-in types. */
	protected static Set builtintypes;
	
	static
	{
		builtintypes = new HashSet();
		builtintypes.add(String.class);
		builtintypes.add(int.class);
		builtintypes.add(Integer.class);
		builtintypes.add(long.class);
		builtintypes.add(Long.class);
		builtintypes.add(float.class);
		builtintypes.add(Float.class);
		builtintypes.add(double.class);
		builtintypes.add(Double.class);
		builtintypes.add(boolean.class);
		builtintypes.add(Boolean.class);
		builtintypes.add(short.class);
		builtintypes.add(Short.class);
		builtintypes.add(byte.class);
		builtintypes.add(Byte.class);
		builtintypes.add(char.class);
		builtintypes.add(Character.class);
	}
	
	/**
	 *  Convert a string value to a built-in type.
	 *  @param clazz The target clazz.
	 *  @param val The string valut to convert.
	 */
	public static Object convertBuiltInTypes(Class clazz, String val)
	{
		Object ret;
		
		if(clazz.equals(String.class))
		{
			ret = val;
		}
		else if(clazz.equals(int.class) || clazz.equals(Integer.class))
		{
			ret = new Integer(val);
		}
		else if(clazz.equals(long.class) || clazz.equals(Long.class))
		{
			ret = new Long(val);
		}
		else if(clazz.equals(float.class) || clazz.equals(Float.class))
		{
			ret = new Float(val);
		}
		else if(clazz.equals(double.class) || clazz.equals(Double.class))
		{
			ret = new Double(val);
		}
		else if(clazz.equals(boolean.class) || clazz.equals(Boolean.class))
		{
			ret = new Boolean(val);
		}
		else if(clazz.equals(short.class) || clazz.equals(Short.class))
		{
			ret = new Short(val);
		}
		else if(clazz.equals(byte.class) || clazz.equals(Byte.class))
		{
			ret = new Byte(val);
		}
		else if(clazz.equals(char.class) || clazz.equals(Character.class))
		{
			ret = new Character(val.charAt(0)); // ?
		}
		else
		{
			throw new RuntimeException("Unknown argument type: "+clazz);
		}
		
		return ret;
	}
	
	/**
	 *  Test if a clazz is a built-in type.
	 *  @param clazz The clazz.
	 *  @return True, if built-in type.
	 */
	public static boolean isBuiltInType(Class clazz)
	{
		return builtintypes.contains(clazz);
	}
}
