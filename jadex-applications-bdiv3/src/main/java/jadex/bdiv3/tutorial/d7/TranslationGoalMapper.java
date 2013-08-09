package jadex.bdiv3.tutorial.d7;

import java.lang.reflect.Method;

import jadex.bdiv3.runtime.impl.IServiceParameterMapper;

/**
 * 
 */
public class TranslationGoalMapper implements IServiceParameterMapper<TranslationGoal>
{
	/**
	 *  Create service parameters.
	 */
	public Object[] createServiceParameters(TranslationGoal obj, Method m)
	{
		return new Object[]{obj.getEWord()};
	}
	
	/**
	 *  Create service result.
	 */
	public void handleServiceResult(TranslationGoal obj, Method m, Object result)
	{
		obj.setGWord((String)result);
	}
}
