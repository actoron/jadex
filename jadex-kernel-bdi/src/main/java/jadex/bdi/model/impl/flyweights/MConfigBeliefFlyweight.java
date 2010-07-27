package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigBelief;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigBelief;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class MConfigBeliefFlyweight extends MBeliefFlyweight implements IMConfigBelief, IMEConfigBelief
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConfigBeliefFlyweight(IOAVState state, Object scope, Object handle)
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configbelief_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configbelief_has_ref);
		}
	}
}
