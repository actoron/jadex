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
	 *  @return The fact. 
	 */
	public IMEExpression createFact()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact, elem);
					object = new MExpressionFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.belief_has_fact, elem);
			return new MExpressionFlyweight(getState(), getScope(), elem);
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
			AgentInvocation invoc = new AgentInvocation()
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
			AgentInvocation invoc = new AgentInvocation()
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
