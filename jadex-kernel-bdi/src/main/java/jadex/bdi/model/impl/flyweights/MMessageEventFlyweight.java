package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMMessageEvent;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for message events.
 */
public class MMessageEventFlyweight extends MProcessableElementFlyweight implements IMMessageEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new message event flyweight.
	 */
	public MMessageEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the parameter direction.
	 *  @return The direction.
	 */
	public String getDirection()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_direction);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_direction);
		}
	}
	
	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public String getType()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_type);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_type);
		}
	}
	
	/**
	 *  Get the match expression.
	 *  @return The match expression.
	 */
	public IMExpression getMatchExpression()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_match);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_match);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
}
