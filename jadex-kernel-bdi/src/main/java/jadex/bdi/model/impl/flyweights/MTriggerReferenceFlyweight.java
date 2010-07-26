package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class MTriggerReferenceFlyweight extends MElementFlyweight implements IMTriggerReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MTriggerReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the reference.
	 */
	public String	getReference()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_ref);
		}
	}
	
	/**
	 *  Get the match expression.
	 */
	public IMExpression	getMatchExpression()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_match);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_match);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
