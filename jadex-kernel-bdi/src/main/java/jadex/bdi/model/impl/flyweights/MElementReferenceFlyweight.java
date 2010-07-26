package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMElementReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for element reference model.
 */
public class MElementReferenceFlyweight extends MReferenceableElementFlyweight implements IMElementReference
{
	//-------- constructors --------
	
	/**
	 *  Create a new element reference flyweight.
	 */
	public MElementReferenceFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get concrete element name.
	 *  @return The concrete element name. 
	 */
	public String getConcrete()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.elementreference_has_concrete);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.elementreference_has_concrete);
		}
	}
}
