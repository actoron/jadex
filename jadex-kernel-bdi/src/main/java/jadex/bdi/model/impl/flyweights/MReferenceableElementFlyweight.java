package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMReferenceableElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.List;

/**
 *  Referenceable element model flyweight.
 */
public class MReferenceableElementFlyweight extends MElementFlyweight implements IMReferenceableElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MReferenceableElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String isExported()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported);
		}
	}
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String[] getAssigntos()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
				}
			};
			return invoc.object==null? SUtil.EMPTY_STRING_ARRAY: (String[])((List)invoc.object).toArray(new String[((List)invoc.object).size()]);
		}
		else
		{
			Collection atos = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
			return atos==null? SUtil.EMPTY_STRING_ARRAY: (String[])(atos.toArray(new String[atos.size()]));
		}
	}
}
