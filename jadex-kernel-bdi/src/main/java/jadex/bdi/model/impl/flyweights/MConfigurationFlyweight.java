package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigBelief;
import jadex.bdi.model.IMConfigBeliefSet;
import jadex.bdi.model.IMConfigElement;
import jadex.bdi.model.IMConfiguration;
import jadex.bdi.model.IMInitialCapability;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigBelief;
import jadex.bdi.model.editable.IMEConfigBeliefSet;
import jadex.bdi.model.editable.IMEConfigElement;
import jadex.bdi.model.editable.IMEConfiguration;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for configuration model element.
 */
public class MConfigurationFlyweight extends MElementFlyweight implements IMConfiguration, IMEConfiguration
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialcapabilities);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialcapabilities);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefs);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefs);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefsets);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialbeliefsets);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialgoals);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialgoals);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endgoals);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endgoals);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialplans);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialplans);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endplans);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endplans);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialinternalevents);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialinternalevents);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endinternalevents);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endinternalevents);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialmessageevents);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_initialmessageevents);
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endmessageevents);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configuration_has_endmessageevents);
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
	 *  Create an initial capability.
	 *  @param ref The referenced capability name.
	 *  @param conf The name of configuration to use.
	 */
	public void createInitialCapability(final String ref, final String conf)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.initialcapability_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.initialcapability_has_ref, ref);
					getState().setAttributeValue(elem, OAVBDIMetaModel.initialcapability_has_configuration, conf);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialcapabilities, elem);
				}
			};
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.initialcapability_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.initialcapability_has_ref, ref);
			getState().setAttributeValue(elem, OAVBDIMetaModel.initialcapability_has_configuration, conf);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialcapabilities, elem);
		}
	}

	/**
	 *  Create an initial belief.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigBelief createInitialBelief(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configbelief_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configbelief_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialbeliefs, elem);
					object = new MConfigBeliefFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigBelief)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configbelief_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configbelief_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialbeliefs, elem);
			return new MConfigBeliefFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an initial belief set.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigBeliefSet createInitialBeliefSet(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configbeliefset_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configbeliefset_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialbeliefsets, elem);
					object = new MConfigBeliefSetFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigBeliefSet)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configbeliefset_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configbeliefset_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialbeliefsets, elem);
			return new MConfigBeliefSetFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an initial goal.
	 *  @param ref The referenced element name.
	 */
	public IMEConfigElement createInitialGoal(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialgoals, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialgoals, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an end goal.
	 *  @param ref The goal reference.
	 *  @return The end goal.
	 */
	public IMEConfigElement createEndGoal(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endgoals, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endgoals, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an initial plan.
	 *  @param ref The plan reference.
	 *  @return The initial plan.
	 */
	public IMEConfigElement createInitialPlan(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialplans, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialplans, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an end plan.
	 *  @param ref The plan reference.
	 *  @return The end plan.
	 */
	public IMEConfigElement createEndPlan(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endplans, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endplans, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an initial internal event.
	 *  @param ref The event reference.
	 *  @return The initial internal event.
	 */
	public IMEConfigElement createInitialInternalEvent(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialinternalevents, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialinternalevents, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an end internal event.
	 *  @param ref The event reference.
	 *  @return The end internal event.
	 */
	public IMEConfigElement createEndInternalEvent(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endinternalevents, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endinternalevents, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an initial message event.
	 *  @param ref The event reference.
	 *  @return The initial message event.
	 */
	public IMEConfigElement createInitialMessageEvent(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialmessageevents, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_initialmessageevents, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create an end message event.
	 *  @param ref The event reference.
	 *  @return The end message event.
	 */
	public IMEConfigElement createEndMessageEvent(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endmessageevents, elem);
					object = new MConfigElementFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigElement)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configelement_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configelement_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configuration_has_endmessageevents, elem);
			return new MConfigElementFlyweight(getState(), getScope(), elem);
		}
	}
}
