package jadex.bdiv3.runtime.impl;

import jadex.commons.IParameterGuesser;
import jadex.commons.SReflect;
import jadex.commons.SimpleParameterGuesser;
import jadex.micro.IPojoMicroAgent;

/**
 *  Guesser supporting the pojo agent.
 */
public class AgentParameterGuesser extends SimpleParameterGuesser
{
	/** The pojo. */
	protected Object pojoagent;

	/**
	 * 
	 */
	public AgentParameterGuesser(IParameterGuesser parent, BDIAgentInterpreter agent)
	{
		super(parent);
		this.pojoagent = agent instanceof IPojoMicroAgent? ((IPojoMicroAgent)agent).getPojoAgent(): null;
	}
	
	/**
	 *  Guess a parameter.
	 *  @param type The type.
	 *  @return The mapped value. 
	 *  (Throws exception if no value could be found to support null value).
	 */
	public Object guessParameter(Class<?> type)
	{
		if(pojoagent!=null && SReflect.isSupertype(pojoagent.getClass(), type))
		{
			return pojoagent;
		}
		else
		{
			return super.guessParameter(type);
		}
	}
}
