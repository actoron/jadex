package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBeliefbase;
import jadex.bdi.model.IMCapability;
import jadex.bdi.model.IMCapabilityReference;
import jadex.bdi.model.IMConfiguration;
import jadex.bdi.model.IMEventbase;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMExpressionbase;
import jadex.bdi.model.IMGoalbase;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.model.IMPropertybase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for capability element model.
 */
public class MCapabilityFlyweight extends MElementFlyweight implements IMCapability
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MCapabilityFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods --------

	/**
	 *  Get the package.
	 *  @return The package.
	 */
	public String getPackage()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package);
		}
	}
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getImports()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					sarray = (String[])getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports);
				}
			};
			return invoc.sarray;
		}
		else
		{
			return (String[])getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports);
		}
	}
	
	/**
	 *  Test if is abstract.
	 *  @return True, if is abstract.
	 */
	public boolean isAbstract()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_abstract)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_abstract)).booleanValue();
		}
	}
	
	/**
	 *  Get the capability references.
	 *  @return The capability references.
	 */
	public IMCapabilityReference[] getCapabilityReferences()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
					IMCapabilityReference[] ret = new IMCapabilityReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MCapabilityReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMCapabilityReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
			IMCapabilityReference[] ret = new IMCapabilityReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MCapabilityReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The belief base.
	 */
	public IMBeliefbase getBeliefbase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MBeliefbaseFlyweight(getState(), getScope());
				}
			};
			return (IMBeliefbase)invoc.object;
		}
		else
		{
			return new MBeliefbaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The goalbase.
	 */
	public IMGoalbase getGoalbase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MGoalbaseFlyweight(getState(), getScope());
				}
			};
			return (IMGoalbase)invoc.object;
		}
		else
		{
			return new MGoalbaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the planbase.
	 *  @return The planbase.
	 */
	public IMPlanbase getPlanbase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MPlanbaseFlyweight(getState(), getScope());
				}
			};
			return (IMPlanbase)invoc.object;
		}
		else
		{
			return new MPlanbaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the eventbase.
	 *  @return The eventbase.
	 */
	public IMEventbase getEventbase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MEventbaseFlyweight(getState(), getScope());
				}
			};
			return (IMEventbase)invoc.object;
		}
		else
		{
			return new MEventbaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the expressionbase.
	 *  @return The expressionbase.
	 */
	public IMExpressionbase getExpressionbase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MExpressionbaseFlyweight(getState(), getScope());
				}
			};
			return (IMExpressionbase)invoc.object;
		}
		else
		{
			return new MExpressionbaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the propertybase.
	 *  @return The propertybase.
	 */
	public IMPropertybase getPropertybase()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = new MPropertybaseFlyweight(getState(), getScope());
				}
			};
			return (IMPropertybase)invoc.object;
		}
		else
		{
			return new MPropertybaseFlyweight(getState(), getScope());
		}
	}
	
	/**
	 *  Get the services.
	 *  @return The services.
	 */
	public IMExpression[] getServices()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_services);
					IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMExpression[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_services);
			IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public IMConfiguration[] getConfigurations()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
					IMConfiguration[] ret = new IMConfiguration[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigurationFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (MConfigurationFlyweight[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
			MConfigurationFlyweight[] ret = new MConfigurationFlyweight[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigurationFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the default configuration.
	 *  @return The default configuration.
	 */
	public String getDefaultConfiguration()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_defaultconfiguration);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_defaultconfiguration);
		}
	}
	
}
