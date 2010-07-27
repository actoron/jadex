package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMMessageEvent;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEMessageEvent;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for message events.
 */
public class MMessageEventFlyweight extends MProcessableElementFlyweight implements IMMessageEvent, IMEMessageEvent
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
	
	/**
	 *  Set the parameter direction.
	 *  @param dir The direction.
	 */
	public void setDirection(final String dir)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_direction, dir);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_direction, dir);
		}
	}
	
	/**
	 *  Set the message type.
	 *  @param type The message type name.
	 */
	public void setType(final String type)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_type, type);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.messageevent_has_type, type);
		}
	}
	
	/**
	 *  Create a match expression.
	 *  @param content The content.
	 *  @param lang The language.
	 */
	public IMEExpression createMatchExpression(final String content, final String lang)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = MExpressionbaseFlyweight.createExpression(content, lang, getState(), getScope());
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			return MExpressionbaseFlyweight.createExpression(content, lang, getState(), getScope());
		}
	}
}
