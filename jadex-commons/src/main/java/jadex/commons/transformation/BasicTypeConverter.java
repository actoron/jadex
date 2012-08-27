package jadex.commons.transformation;

import java.util.HashMap;
import java.util.Map;

/**
 *  Converter for basic types.
 */
public class BasicTypeConverter //implements ITypeConverter
{
	//-------- static part --------
	
	/** String -> String converter. (remove?) */
	public static IStringObjectConverter STRING_CONVERTER = new StringTypeConverter();
	
	/** String -> Integer converter.  */
	public static IStringObjectConverter INTEGER_CONVERTER = new IntegerTypeConverter();
	
	/** String -> Long converter. */
	public static IStringObjectConverter LONG_CONVERTER = new LongTypeConverter();
	
	/** String -> Float converter. */
	public static IStringObjectConverter FLOAT_CONVERTER = new FloatTypeConverter();
	
	/** String -> Double converter. */
	public static IStringObjectConverter DOUBLE_CONVERTER = new DoubleTypeConverter();
	
	/** String -> Boolean converter. */
	public static IStringObjectConverter BOOLEAN_CONVERTER = new BooleanTypeConverter();
	
	/** String -> Short converter. */
	public static IStringObjectConverter SHORT_CONVERTER = new ShortTypeConverter();
	
	/** String -> Byte converter. */
	public static IStringObjectConverter BYTE_CONVERTER = new ByteTypeConverter();
	
	/** String -> Character converter. (remove?) */
	public static IStringObjectConverter CHARACTER_CONVERTER = new CharacterTypeConverter();
	
	/** The map of basic converters. */
	protected static Map basicconverters;
	
	static
	{
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
	 *  Test if a clazz is a built-in type.
	 *  @param clazz The clazz.
	 *  @return True, if built-in type.
	 */
	public static boolean isBuiltInType(Class clazz)
	{
		return basicconverters.get(clazz)!=null;
	}
	
	/**
	 *  Get a String -> X converter for a target clazz.
	 *  @param clazz The clazz.
	 *  @return converter The converter.
	 */
	public static IStringObjectConverter getBasicStringConverter(Class<?> clazz)
	{
		return (IStringObjectConverter)basicconverters.get(clazz);
	}
	
	/**
	 *  Get a X -> String converter for a source clazz.
	 *  @param clazz The clazz.
	 *  @return converter The converter.
	 */
	public static IObjectStringConverter getBasicObjectConverter(Class<?> clazz)
	{
		IObjectStringConverter ret = null;
		if(isBuiltInType(clazz))
		{
			ret = new IObjectStringConverter()
			{
				public String convertObject(Object val, Object context)
				{
					return val==null? "null": val.toString();
				}
			};
		}
		return ret;
	}
}

/**
 *  String -> String converter. (remove?)
 */
class StringTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return val;
	}
	
}

/**
 *  String -> Integer converter.
 */
class IntegerTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Integer(val);
	}
	
}

/**
 *  String -> Long converter.
 */
class LongTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Long(val);
	}
	
}

/**
 *  String -> Float converter.
 */
class FloatTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Float(val);
	}
	
}

/**
 *  String -> Double converter.
 */
class DoubleTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Double(val);
	}
}

/**
 *  String -> Boolean converter.
 */
class BooleanTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Boolean(val);
	}
}

/**
 *  String -> Short converter.
 */
class ShortTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Short(val);
	}
}

/**
 *  String -> Byte converter.
 */
class ByteTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Byte((String)val);
	}
}

/**
 *  String -> Character converter.
 */
class CharacterTypeConverter implements IStringObjectConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertString(String val, Object context)
	{
		return new Character(((String)val).charAt(0)); //?
	}
	
}

