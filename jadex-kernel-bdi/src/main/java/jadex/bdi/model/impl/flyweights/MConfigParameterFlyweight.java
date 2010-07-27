package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigParameter;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigParameter;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for config parameter model.
 */
public class MConfigParameterFlyweight extends MParameterFlyweight implements IMConfigParameter, IMEConfigParameter
{
	//-------- constructors --------
	
	/**
	 *  Create a new config parameter flyweight.
	 */
	public MConfigParameterFlyweight(IOAVState state, Object scope, Object handle)
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configparameter_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configparameter_has_ref);
		}
	}
}
