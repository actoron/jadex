package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMETrigger;
import jadex.bdi.model.editable.IMETriggerReference;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for trigger model element.
 */
public class MTriggerFlyweight extends MElementFlyweight implements IMTrigger, IMETrigger
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MTriggerFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the internal events.
	 */
	public IMTriggerReference[]	getInternalEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_internalevents);
					IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMTriggerReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_internalevents);
			IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the message events.
	 */
	public IMTriggerReference[]	getMessageEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_messageevents);
					IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMTriggerReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_messageevents);
			IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the goal finished events.
	 */
	public IMTriggerReference[]	getGoalFinisheds()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_goalfinisheds);
					IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMTriggerReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.trigger_has_goalfinisheds);
			IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactAddeds()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds)).toArray(new String[0]);
		}
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactRemoveds()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds)).toArray(new String[0]);
		}
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactChangeds()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds)).toArray(new String[0]);
		}
	}

	/**
	 *  Create an internal event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createInternalEvent(final String reference)
	{
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
					getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_internalevents, mtr);
					object	= new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
				}
			};
			return (IMETriggerReference)invoc.object;
		}
		else
		{
			Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
			getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_internalevents, mtr);
			return new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
		}
	}
	
	/**
	 *  Create a message event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createMessageEvent(final String reference)
	{
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
					getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_messageevents, mtr);
					object	= new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
				}
			};
			return (IMETriggerReference)invoc.object;
		}
		else
		{
			Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
			getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_messageevents, mtr);
			return new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
		}
	}
	
	/**
	 *  Create a goal finished event reference.
	 *  @param reference	The name of the referenced element.
	 */
	public IMETriggerReference	createGoalFinishedEvent(final String reference)
	{
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
					getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_goalfinisheds, mtr);
					object	= new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
				}
			};
			return (IMETriggerReference)invoc.object;
		}
		else
		{
			Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
			getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_goalfinisheds, mtr);
			return new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
		}		
	}
	
	/**
	 *  Create a fact added trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactAdded(final String reference)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds, reference);
				}
			};
		}
		else
		{
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds, reference);
		}
	}
	
	/**
	 *  Create a fact removed trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactRemoved(final String reference)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds, reference);
				}
			};
		}
		else
		{
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds, reference);
		}
	}
	
	/**
	 *  Create a fact changed trigger.
	 *  @param reference	The name of the referenced element.
	 */
	public void	createFactChanged(final String reference)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds, reference);
				}
			};
		}
		else
		{
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds, reference);
		}
	}
}
