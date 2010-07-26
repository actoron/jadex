package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMInitialCapability;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for initial capability model.
 */
public class MInitialCapabilityFlyweight extends MElementFlyweight implements IMInitialCapability
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MInitialCapabilityFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the referenced element.
	 *  @return The referenced element name.
	 */
	public String getReference()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.initialcapability_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.initialcapability_has_ref);
		}
	}
	
	/**
	 *  Get the configuration name.
	 *  @return The configuration name.
	 */
	public String getConfiguration()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.initialcapability_has_configuration);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.initialcapability_has_configuration);
		}
	}
}
