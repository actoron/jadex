package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.rules.state.IOAVState;

/**
 *  Model element flyweight.
 */
public class MElementFlyweight extends ElementFlyweight implements IMElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 *  @param rplan	The calling plan (if called from plan)
	 */
	public MElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name. 
	 */
	public String getName()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_name);
		}
	}
	
	/**
	 *  Get the description.
	 *  @return The description. 
	 */
	public String getDescription()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.modelelement_has_description);
		}
	}
}
