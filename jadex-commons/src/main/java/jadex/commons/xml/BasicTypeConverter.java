package jadex.commons.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Converter for basic types.
 */
public class BasicTypeConverter //implements ITypeConverter
{
	//-------- static part --------
	
	public static ITypeConverter STRING_CONVERTER = new StringTypeConverter();
	public static ITypeConverter INTEGER_CONVERTER = new IntegerTypeConverter();
	public static ITypeConverter LONG_CONVERTER = new LongTypeConverter();
	public static ITypeConverter FLOAT_CONVERTER = new FloatTypeConverter();
	public static ITypeConverter DOUBLE_CONVERTER = new DoubleTypeConverter();
	public static ITypeConverter BOOLEAN_CONVERTER = new BooleanTypeConverter();
	public static ITypeConverter SHORT_CONVERTER = new ShortTypeConverter();
	public static ITypeConverter BYTE_CONVERTER = new ByteTypeConverter();
	public static ITypeConverter CHARACTER_CONVERTER = new CharacterTypeConverter();
	
	/** Converter instance. */
//	protected static BasicTypeConverter CONVERTER = new BasicTypeConverter();
	
	/** The built-in types. */
//	protected static Set builtintypes;
	
	protected static Map basicconverters;
	
	
	static
	{
//		builtintypes = new HashSet();
//		builtintypes.add(String.class);
//		builtintypes.add(int.class);
//		builtintypes.add(Integer.class);
//		builtintypes.add(long.class);
//		builtintypes.add(Long.class);
//		builtintypes.add(float.class);
//		builtintypes.add(Float.class);
//		builtintypes.add(double.class);
//		builtintypes.add(Double.class);
//		builtintypes.add(boolean.class);
//		builtintypes.add(Boolean.class);
//		builtintypes.add(short.class);
//		builtintypes.add(Short.class);
//		builtintypes.add(byte.class);
//		builtintypes.add(Byte.class);
//		builtintypes.add(char.class);
//		builtintypes.add(Character.class);
		
		basicconverters = new HashMap();
		basicconverters.put(String.class, STRING_CONVERTER);
		basicconverters.put(int.class, INTEGER_CONVERTER);
		basicconverters.put(Integer.class, INTEGER_CONVERTER);
		basicconverters.put(long.class, LONG_CONVERTER);
		basicconverters.put(Long.class, LONG_CONVERTER);
		basicconverters.put(float.class, FLOAT_CONVERTER);
		basicconverters.put(Float.class, FLOAT_CONVERTER);
		basicconverters.put(double.class, DOUBLE_CONVERTER);
		basicconverters.put(Double.class, DOUBLE_CONVERTER);
		basicconverters.put(boolean.class, BOOLEAN_CONVERTER);
		basicconverters.put(Boolean.class, BOOLEAN_CONVERTER);
		basicconverters.put(short.class, SHORT_CONVERTER);
		basicconverters.put(Short.class, SHORT_CONVERTER);
		basicconverters.put(byte.class, BYTE_CONVERTER);
		basicconverters.put(Byte.class, BYTE_CONVERTER);
		basicconverters.put(char.class, CHARACTER_CONVERTER);
		basicconverters.put(Character.class, CHARACTER_CONVERTER);
	}
	
	/**
	 *  Convert a string value to a built-in type.
	 *  @param clazz The target clazz.
	 *  @param val The string valut to convert.
	 * /
	public static Object convertType(Class clazz, String val)
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
	}*/
	
	/**
	 *  Test if a clazz is a built-in type.
	 *  @param clazz The clazz.
	 *  @return True, if built-in type.
	 */
	public static boolean isBuiltInType(Class clazz)
	{
		return basicconverters.get(clazz)!=null;
	}
	
	/**
	 * 
	 */
	public static ITypeConverter getBasicConverter(Class clazz)
	{
		return (ITypeConverter)basicconverters.get(clazz);
	}
}

/**
 * 
 */
class StringTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return val;
	}
}

/**
 * 
 */
class IntegerTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Integer(val);
	}
}

/**
 * 
 */
class LongTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Long(val);
	}
}

/**
 * 
 */
class FloatTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Float(val);
	}
}

/**
 * 
 */
class DoubleTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Double(val);
	}
}

/**
 * 
 */
class BooleanTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Boolean(val);
	}
}

/**
 * 
 */
class ShortTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Short(val);
	}
}

/**
 * 
 */
class ByteTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Byte(val);
	}
}

/**
 * 
 */
class CharacterTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(String val, Object root, ClassLoader classloader)
	{
		return new Character(val.charAt(0)); //?
	}
}

