package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMETriggerReference;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class MTriggerReferenceFlyweight extends MElementFlyweight implements IMTriggerReference, IMETriggerReference
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
		if(isExternalThread())
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
		if(isExternalThread())
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

	/**
	 *  Set the reference.
	 *  @param reference	The name of the referenced element.
	 */
	public void	setReference(final String reference)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_ref, reference);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_ref, reference);
		}
	}
	
	/**
	 *  Create a match expression.
	 *  @param expression	The match expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return the expression.
	 */
	public IMEExpression	createMatchExpression(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_match, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.triggerreference_has_match, mexp.getHandle());			
			return mexp;
		}
	}
}
