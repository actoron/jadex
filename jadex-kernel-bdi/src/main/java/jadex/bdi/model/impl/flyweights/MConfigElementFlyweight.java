package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for config element model.
 */
public class MConfigElementFlyweight extends MConfigParameterElementFlyweight implements IMConfigElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConfigElementFlyweight(IOAVState state, Object scope, Object handle)
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configelement_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configelement_has_ref);
		}
	}
}
