package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMInhibited;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for inhibit element.
 */
public class MInhibitedFlyweight extends MConditionFlyweight implements IMInhibited
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MInhibitedFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the referenced goal name.
	 *  @return The name of the inhibited goal.
	 */
	public String getReference()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.inhibits_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.inhibits_has_ref);
		}
	}
	
	/**
	 *  Get the inhibition mode (OAVBDIMetaModel.INHIBITS_WHEN_ACTIVE/INHIBITS_WHEN_IN_PROCESS).
	 *  @return The mode.
	 */
	public String getMode()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.inhibits_has_inhibit);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.inhibits_has_inhibit);
		}
	}
}
