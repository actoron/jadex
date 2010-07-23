package jadex.bdi.model.impl.flyweights;

import java.util.Collection;
import java.util.Iterator;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMEventbase;
import jadex.bdi.model.IMInternalEvent;
import jadex.bdi.model.IMMessageEvent;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MElementFlyweight.AgentInvocation;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the event base model.
 */
public class MEventbaseFlyweight extends MElementFlyweight implements IMEventbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private MEventbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods --------
	
	/**
	 *  Get an internal event for a name.
	 *  @param name	The event name.
	 */
	public IMInternalEvent getInternalEvent(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents, name);
					if(handle==null)
						throw new RuntimeException("Event not found: "+name);
					object = new MInternalEventFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMInternalEvent)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents, name);
			if(handle==null)
				throw new RuntimeException("Event not found: "+name);
			return new MInternalEventFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Get a message event for a name.
	 *  @param name	The event set name.
	 */
	public IMMessageEvent getMessageEvent(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents, name);
					if(handle==null)
						throw new RuntimeException("Event not found: "+name);
					object = new MInternalEventFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMMessageEvent)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents, name);
			if(handle==null)
				throw new RuntimeException("Event not found: "+name);
			return new MMessageEventFlyweight(getState(), getScope(), handle);
		}
	}
	
	/**
	 *  Returns all internal events.
	 *  @return All internal events.
	 */
	public IMInternalEvent[] getInternalEvents()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents);
					IMInternalEvent[] ret = new IMInternalEvent[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MInternalEventFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMInternalEvent[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_internalevents);
			IMInternalEvent[] ret = new IMInternalEvent[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MInternalEventFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}

	/**
	 *  Return all message events.
	 *  @return All message events.
	 */
	public IMMessageEvent[] getEventSets()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents);
					IMMessageEvent[] ret = new IMMessageEvent[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MMessageEventFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMMessageEvent[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_messageevents);
			IMMessageEvent[] ret = new IMMessageEvent[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MMessageEventFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
}
