package jadex.bdi.model.impl.flyweights;


import jadex.bdi.model.IMConfigParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigParameterSet;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for config parameter model.
 */
public class MConfigParameterSetFlyweight extends MParameterSetFlyweight implements IMConfigParameterSet, IMEConfigParameterSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new config parameterset flyweight.
	 */
	public MConfigParameterSetFlyweight(IOAVState state, Object scope, Object handle)
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configparameterset_has_ref);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.configparameterset_has_ref);
		}
	}
}
