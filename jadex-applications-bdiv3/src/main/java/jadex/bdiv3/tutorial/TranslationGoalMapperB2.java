package jadex.bdiv3.tutorial;

import java.lang.reflect.Method;

import jadex.bdiv3.runtime.IServiceParameterMapper;

/**
 * 
 */
public class TranslationGoalMapperB2 implements IServiceParameterMapper<TranslationGoalB2>
{
	/**
	 *  Create service parameters.
	 */
	public Object[] createServiceParameters(TranslationGoalB2 obj, Method m)
	{
		return new Object[]{obj.getGWord()};
	}
	
	/**
	 *  Create service result.
	 */
	public void handleServiceResult(TranslationGoalB2 obj, Method m, Object result)
	{
		obj.setEword((String)result);
	}
}
