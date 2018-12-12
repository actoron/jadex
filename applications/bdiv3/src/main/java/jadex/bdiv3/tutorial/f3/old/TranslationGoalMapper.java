package jadex.bdiv3.tutorial.f3.old;

import java.lang.reflect.Method;

import jadex.bdiv3.runtime.impl.IServiceParameterMapper;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.tutorial.f3.TranslationGoal;

/**
 *  Custom goal service mapper.
 */
public class TranslationGoalMapper implements IServiceParameterMapper<TranslationGoal>
{
	/**
	 *  Create service parameters.
	 */
	public Object[] createServiceParameters(TranslationGoal obj, Method m, RPlan plan)
	{
		return new Object[]{obj.getEWord()};
	}
	
	/**
	 *  Create service result.
	 */
	public void handleServiceResult(TranslationGoal obj, Method m, Object result, RPlan plan)
	{
		obj.setGWord((String)result);
	}
}
