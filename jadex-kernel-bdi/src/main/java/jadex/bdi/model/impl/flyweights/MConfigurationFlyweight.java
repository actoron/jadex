package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigBelief;
import jadex.bdi.model.IMConfigBeliefSet;
import jadex.bdi.model.IMConfigElement;
import jadex.bdi.model.IMConfiguration;
import jadex.bdi.model.IMInitialCapability;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for configuration model element.
 */
public class MConfigurationFlyweight extends MElementFlyweight implements IMConfiguration
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MConfigurationFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the initial capabilities.
	 *  @return The initial capabilities.
	 */
	public IMInitialCapability[] getInitialCapabilities()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialcapabilities);
					IMInitialCapability[] ret = new IMInitialCapability[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MInitialCapabilityFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMInitialCapability[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialcapabilities);
			IMInitialCapability[] ret = new IMInitialCapability[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MInitialCapabilityFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}

	/**
	 *  Get the initial beliefs.
	 *  @return The initial beliefs.
	 */
	public IMConfigBelief[] getInitialBeliefs()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefs);
					IMConfigBelief[] ret = new IMConfigBelief[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigBeliefFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigBelief[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefs);
			IMConfigBelief[] ret = new IMConfigBelief[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigBeliefFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the initial belief sets.
	 *  @return The initial belief sets.
	 */
	public IMConfigBeliefSet[] getInitialBeliefSets()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefsets);
					IMConfigBeliefSet[] ret = new IMConfigBeliefSet[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigBeliefSetFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigBeliefSet[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefsets);
			IMConfigBeliefSet[] ret = new IMConfigBeliefSet[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigBeliefSetFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the initial goals.
	 *  @return The initial goals.
	 */
	public IMConfigElement[] getInitialGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialgoals);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialgoals);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the end goals.
	 *  @return The end goals.
	 */
	public IMConfigElement[] getEndGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endgoals);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endgoals);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the initial plans.
	 *  @return The initial plans.
	 */
	public IMConfigElement[] getInitialPlans()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialplans);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialplans);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the end plans.
	 *  @return The end plans.
	 */
	public IMConfigElement[] getEndPlans()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endplans);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endplans);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the initial internal events.
	 *  @return The initial internal events.
	 */
	public IMConfigElement[] getInitialInternalEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialinternalevents);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialinternalevents);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the end internal events.
	 *  @return The end internal events.
	 */
	public IMConfigElement[] getEndInternalEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endinternalevents);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endinternalevents);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the initial message events.
	 *  @return The initial message events.
	 */
	public IMConfigElement[] getInitialMessageEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialmessageevents);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_initialmessageevents);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the end message events.
	 *  @return The end message events.
	 */
	public IMConfigElement[] getEndMessageEvents()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endmessageevents);
					IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigElement[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configuration_has_endmessageevents);
			IMConfigElement[] ret = new IMConfigElement[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
}
