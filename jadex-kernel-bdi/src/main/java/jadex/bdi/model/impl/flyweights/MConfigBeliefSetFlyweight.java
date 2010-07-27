package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigBeliefSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigBeliefSet;
import jadex.rules.state.IOAVState;

/**
 * 
 */
public class MConfigBeliefSetFlyweight extends MBeliefSetFlyweight implements IMConfigBeliefSet, IMEConfigBeliefSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConfigBeliefSetFlyweight(IOAVState state, Object scope, Object handle)
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configbeliefset_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configbeliefset_has_ref);
		}
	}
}
