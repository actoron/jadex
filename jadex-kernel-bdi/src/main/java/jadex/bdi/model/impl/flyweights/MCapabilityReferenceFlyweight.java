package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCapability;
import jadex.bdi.model.IMCapabilityReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Get the capability reference model.
 */
public class MCapabilityReferenceFlyweight extends MElementFlyweight implements IMCapabilityReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MCapabilityReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------

	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capabilityref_has_file);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capabilityref_has_file);
		}
	}
	
	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	public IMCapability getCapability()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capabilityref_has_capability);
					object = new MCapabilityFlyweight(getState(), handle);
				}
			};
			return (IMCapability)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capabilityref_has_capability);
			return new MCapabilityFlyweight(getState(), handle);
		}
	}
}
