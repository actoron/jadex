package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for trigger model element.
 */
public class MTriggerFlyweight extends MElementFlyweight implements IMTrigger
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_internalevents);
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
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_internalevents);
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_messageevents);
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
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_messageevents);
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_goalfinisheds);
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
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.trigger_has_goalfinisheds);
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factaddeds)).toArray(new String[0]);
		}
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactRemoveds()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factremoveds)).toArray(new String[0]);
		}
	}
	
	/**
	 *  Get the fact added triggers (belief set names).
	 */
	public String[]	getFactChangeds()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = ((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds)).toArray(new String[0]);
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			return (String[])((Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.trigger_has_factchangeds)).toArray(new String[0]);
		}
	}
}
