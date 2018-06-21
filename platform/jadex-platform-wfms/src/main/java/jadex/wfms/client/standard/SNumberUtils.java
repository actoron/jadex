package jadex.wfms.client.standard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class SNumberUtils
{
	public static final Number parseNumber(Class numberClass, String input)
	{
		return ((NumberParser) NUMBER_CONVERTERS.get(numberClass)).parseString(input);
	}
	
	private interface NumberParser
	{
		public Number parseString(String input);
	}
	
	private static final Map NUMBER_CONVERTERS = new HashMap();
	static
	{
		NUMBER_CONVERTERS.put(Byte.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new Byte(Byte.parseByte(input));
			}
		});
		
		NUMBER_CONVERTERS.put(Short.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new Short(Short.parseShort(input));
			}
		});
		
		NUMBER_CONVERTERS.put(Integer.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return Integer.valueOf(Integer.parseInt(input));
			}
		});
		
		NUMBER_CONVERTERS.put(Long.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new Long(Long.parseLong(input));
			}
		});
		
		NUMBER_CONVERTERS.put(Float.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new Float(Float.parseFloat(input));
			}
		});
		
		NUMBER_CONVERTERS.put(Double.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new Double(Double.parseDouble(input));
			}
		});
		
		NUMBER_CONVERTERS.put(BigDecimal.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new BigDecimal(input);
			}
		});
		
		NUMBER_CONVERTERS.put(BigInteger.class, new NumberParser()
		{
			public Number parseString(String input)
			{
				return new BigInteger(input);
			}
		});
	}
}
