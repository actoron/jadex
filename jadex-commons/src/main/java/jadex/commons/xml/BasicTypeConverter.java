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
	
	/** String -> String converter. (remove?) */
	public static ITypeConverter STRING_CONVERTER = new StringTypeConverter();
	
	/** String -> Integer converter.  */
	public static ITypeConverter INTEGER_CONVERTER = new IntegerTypeConverter();
	
	/** String -> Long converter. */
	public static ITypeConverter LONG_CONVERTER = new LongTypeConverter();
	
	/** String -> Float converter. */
	public static ITypeConverter FLOAT_CONVERTER = new FloatTypeConverter();
	
	/** String -> Double converter. */
	public static ITypeConverter DOUBLE_CONVERTER = new DoubleTypeConverter();
	
	/** String -> Boolean converter. */
	public static ITypeConverter BOOLEAN_CONVERTER = new BooleanTypeConverter();
	
	/** String -> Short converter. */
	public static ITypeConverter SHORT_CONVERTER = new ShortTypeConverter();
	
	/** String -> Byte converter. */
	public static ITypeConverter BYTE_CONVERTER = new ByteTypeConverter();
	
	/** String -> Character converter. (remove?) */
	public static ITypeConverter CHARACTER_CONVERTER = new CharacterTypeConverter();
	
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
	public static ITypeConverter getBasicConverter(Class clazz)
	{
		return (ITypeConverter)basicconverters.get(clazz);
	}
}

/**
 *  String -> String converter. (remove?)
 */
class StringTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		return val;
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Integer converter.
 */
class IntegerTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Integer((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Long converter.
 */
class LongTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Long((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Float converter.
 */
class FloatTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Float((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Double converter.
 */
class DoubleTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Double((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Boolean converter.
 */
class BooleanTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Boolean((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Short converter.
 */
class ShortTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Short((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Byte converter.
 */
class ByteTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Byte((String)val);
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

/**
 *  String -> Character converter.
 */
class CharacterTypeConverter implements ITypeConverter
{
	/**
	 *  Convert a string value to another type.
	 *  @param val The string value to convert.
	 */
	public Object convertObject(Object val, Object root, ClassLoader classloader)
	{
		if(!(val instanceof String))
			throw new RuntimeException("Source value must be string: "+val);
		return new Character(((String)val).charAt(0)); //?
	}
	
	/**
	 *  Test if a converter accepts a specific input type.
	 *  @param inputtype The input type.
	 *  @return True, if accepted.
	 */
	public boolean acceptsInputType(Class inputtype)
	{
		return String.class.isAssignableFrom(inputtype);
	}
}

