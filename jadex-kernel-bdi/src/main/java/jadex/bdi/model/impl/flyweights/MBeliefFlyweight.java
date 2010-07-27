package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEBelief;
import jadex.bdi.model.editable.IMEExpression;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for belief model element.
 */
public class MBeliefFlyweight extends MTypedElementFlyweight implements IMBelief, IMEBelief
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MBeliefFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the fact.
	 *  @return The fact. 
	 */
	public IMExpression getFact()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Test if the belief is used as argument.
	 *  @return True if used as argument. 
	 */
	public boolean isArgument()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument)).booleanValue();
		}
	}
	
	/**
	 *  Test if the belief is used as result.
	 *  @return True if used as result. 
	 */
	public boolean isResult()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result)).booleanValue();
		}
	}
	
	/**
	 *  Create the fact.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The fact. 
	 */
	public IMEExpression createFact(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight	mfact	= MExpressionbaseFlyweight.createExpression(expression, language, getState(), getScope());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact, mfact.getHandle());
					object = mfact;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight	mfact	= MExpressionbaseFlyweight.createExpression(expression, language, getState(), getScope());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact, mfact.getHandle());
			return mfact;
		}
	}
	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setArgument(final boolean argu)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument, argu? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_argument, argu? Boolean.TRUE: Boolean.FALSE);
		}
	}
	
	/**
	 *  Set the belief is used as argument.
	 *  @param res The result flag. 
	 */
	public void setResult(final boolean res)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result, res? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_result, res? Boolean.TRUE: Boolean.FALSE);
		}
	}
}
