package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMProcessableElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEProcessableElement;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for processable element model.
 */
public class MProcessableElementFlyweight extends MParameterElementFlyweight implements IMProcessableElement, IMEProcessableElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MProcessableElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Test if is posttoall.
	 *  @return True, if posttoaall.
	 */
	public boolean isPostToAll()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_posttoall)).booleanValue();
		}
	}
	
	/**
	 *  Test if is random selection.
	 *  @return True, if is random selection.
	 */
	public boolean isRandomSelection()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_randomselection)).booleanValue();
		}
	}
	
	/**
	 *  Test if is posttoall.
	 */
	public void setPostToAll(final boolean posttoall)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_posttoall, posttoall? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_posttoall, posttoall? Boolean.TRUE: Boolean.FALSE);
		}
	}
	
	/**
	 *  Test if is random selection.
	 */
	public void setRandomSelection(final boolean random)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_randomselection, random? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().getAttributeValue(getHandle(), OAVBDIMetaModel.processableelement_has_randomselection, random? Boolean.TRUE: Boolean.FALSE);
		}
	}
}
