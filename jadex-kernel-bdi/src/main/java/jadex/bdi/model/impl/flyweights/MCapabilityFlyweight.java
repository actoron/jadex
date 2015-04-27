package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBeliefbase;
import jadex.bdi.model.IMCapability;
import jadex.bdi.model.IMCapabilityReference;
import jadex.bdi.model.IMConfiguration;
import jadex.bdi.model.IMEventbase;
import jadex.bdi.model.IMExpressionbase;
import jadex.bdi.model.IMGoalbase;
import jadex.bdi.model.IMPlanbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEBeliefbase;
import jadex.bdi.model.editable.IMECapability;
import jadex.bdi.model.editable.IMEConfiguration;
import jadex.bdi.model.editable.IMEEventbase;
import jadex.bdi.model.editable.IMEExpressionbase;
import jadex.bdi.model.editable.IMEGoalbase;
import jadex.bdi.model.editable.IMEPlanbase;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for capability element model.
 */
public class MCapabilityFlyweight extends MElementFlyweight implements IMCapability, IMECapability
{
	//-------- attributes --------
	
	/** The model info. */
	// Only for dynamically created models (hack!!!)
	protected ModelInfo	info;
	
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MCapabilityFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Create a new element flyweight.
	 */
	public MCapabilityFlyweight(IOAVState state, Object scope, ModelInfo info)
	{
		this(state, scope);
		this.info	= info;
	}
	
	//-------- methods --------

//	/**
//	 *  Get the package.
//	 *  @return The package.
//	 */
//	public String getPackage()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package);
//				}
//			};
//			return invoc.string;
//		}
//		else
//		{
//			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package);
//		}
//	}
//	
//	/**
//	 *  Get the imports.
//	 *  @return The imports.
//	 */
//	public String[] getImports()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_imports);
//					sarray = elems!=null? (String[])elems.toArray(new String[elems.size()]): SUtil.EMPTY_STRING_ARRAY;
//				}
//			};
//			return invoc.sarray;
//		}
//		else
//		{
//			Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_imports);
//			return elems!=null? (String[])elems.toArray(new String[elems.size()]): SUtil.EMPTY_STRING_ARRAY;
//		}
//	}
	
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
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
	
//	/**
//	 *  Get the propertybase.
//	 *  @return The propertybase.
//	 */
//	public IMPropertybase getPropertybase()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = new MPropertybaseFlyweight(getState(), getScope());
//				}
//			};
//			return (IMPropertybase)invoc.object;
//		}
//		else
//		{
//			return new MPropertybaseFlyweight(getState(), getScope());
//		}
//	}
	
//	/**
//	 *  Get the services.
//	 *  @return The services.
//	 */
//	public IMExpression[] getServices()
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_providedservices);
//					IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
//					if(elems!=null)
//					{
//						int i=0;
//						for(Iterator it=elems.iterator(); it.hasNext(); )
//						{
//							ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
//						}
//					}
//					object = ret;
//				}
//			};
//			return (IMExpression[])invoc.object;
//		}
//		else
//		{
//			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_providedservices);
//			IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
//			if(elems!=null)
//			{
//				int i=0;
//				for(Iterator it=elems.iterator(); it.hasNext(); )
//				{
//					ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
//				}
//			}
//			return ret;
//		}
//	}
	
	
	
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_capabilityrefs);
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
	
	//-------- IMECapability interface ---------
	
//	/**
//	 *  Set the package.
//	 *  @param The package.
//	 */
//	public void setPackage(final String name)
//	{
//		if(isExternalThread())
//		{
//			new AgentInvocation()
//			{
//				public void run()
//				{
//					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package, name);
//				}
//			};
//		}
//		else
//		{
//			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_package, name);
//		}
//	}
//	
//	/**
//	 *  Set the imports.
//	 *  @param The imports.
//	 */
//	public void setImports(final String[] imports)
//	{
//		if(isExternalThread())
//		{
//			new AgentInvocation()
//			{
//				public void run()
//				{
//					Collection	old	= getState().getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_imports);
//					if(old!=null)
//					{
//						for(Iterator it=old.iterator(); it.hasNext(); )
//						{
//							getState().removeAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports, it.next());
//						}
//					}
//					if(imports!=null)
//					{
//						for(int i=0; i<imports.length; i++)
//						{
//							getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports, imports[i]);
//						}
//					}
//				}
//			};
//		}
//		else
//		{
//			Collection	old	= getState().getAttributeValues(getHandle(), OAVBDIMetaModel.capability_has_imports);
//			if(old!=null)
//			{
//				for(Iterator it=old.iterator(); it.hasNext(); )
//				{
//					getState().removeAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports, it.next());
//				}
//			}
//			if(imports!=null)
//			{
//				for(int i=0; i<imports.length; i++)
//				{
//					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_imports, imports[i]);
//				}
//			}
//		}
//	}
	
	/**
	 *  Set if is abstract.
	 *  @param abs True, if is abstract.
	 */
	public void setAbstract(final boolean abs)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_abstract, abs ? Boolean.TRUE : Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_abstract, abs ? Boolean.TRUE : Boolean.FALSE);
		}
	}
	
	/**
	 *  Get the capability references.
	 */
	public void createCapabilityReference(final String name, final String file)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object	caparef	= getState().createObject(OAVBDIMetaModel.capabilityref_type);
					getState().setAttributeValue(caparef, OAVBDIMetaModel.modelelement_has_name, name);
					getState().setAttributeValue(caparef, OAVBDIMetaModel.capabilityref_has_file, file);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_capabilityrefs, caparef);
				}
			};
		}
		else
		{
			Object	caparef	= getState().createObject(OAVBDIMetaModel.capabilityref_type);
			getState().setAttributeValue(caparef, OAVBDIMetaModel.modelelement_has_name, name);
			getState().setAttributeValue(caparef, OAVBDIMetaModel.capabilityref_has_file, file);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_capabilityrefs, caparef);
		}
	}
	
	/**
	 *  Create or get the beliefbase.
	 *  @return The belief base.
	 */
	public IMEBeliefbase createBeliefbase()
	{
		return new MBeliefbaseFlyweight(getState(), getHandle());
	}
	
	/**
	 *  Create or get the beliefbase.
	 *  @return The goalbase.
	 */
	public IMEGoalbase createGoalbase()
	{
		return new MGoalbaseFlyweight(getState(), getHandle());
	}
	
	/**
	 *  Create or get the planbase.
	 *  @return The planbase.
	 */
	public IMEPlanbase createPlanbase()
	{
		return new MPlanbaseFlyweight(getState(), getHandle());
	}
	
	/**
	 *  Create or get the eventbase.
	 *  @return The eventbase.
	 */
	public IMEEventbase createEventbase()
	{
		return new MEventbaseFlyweight(getState(), getHandle());
	}
	
	/**
	 *  Get the expressionbase.
	 *  @return The expressionbase.
	 */
	public IMEExpressionbase createExpressionbase()
	{
		return new MExpressionbaseFlyweight(getState(), getHandle());
	}
	
//	/**
//	 *  Get the propertybase.
//	 *  @return The propertybase.
//	 */
//	public IMEPropertybase createPropertybase()
//	{
//		return new MPropertybaseFlyweight(getState(), getHandle());
//	}
	
//	/**
//	 *  Add a service.
//	 *  @param name	The service name.
//	 *  @param clazz	The service type (for lookups).
//	 *  @param expression	The creation expression for the service object.
//	 *  @param language	The expression language (or null for default java-like language).
//	 *  @return The service expression object.
//	 */
//	public IMEExpression createService(final String name, final Class cls, final String expression, final String language)
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation invoc = new AgentInvocation()
//			{
//				public void run()
//				{
//					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
//					getState().setAttributeValue(mexp.getHandle(), OAVBDIMetaModel.modelelement_has_name, name);
//					getState().setAttributeValue(mexp, OAVBDIMetaModel.expression_has_class, cls);
//					
//					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_providedservices, mexp.getHandle());
//					object	= mexp;
//				}
//			};
//			return (IMEExpression)invoc.object;
//		}
//		else
//		{
//			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
//			getState().setAttributeValue(mexp.getHandle(), OAVBDIMetaModel.modelelement_has_name, name);
//			getState().setAttributeValue(mexp, OAVBDIMetaModel.expression_has_class, cls);
//
//			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_providedservices, mexp.getHandle());			
//			return mexp;
//		}
//	}
	
	/**
	 *  Create a configuration.
	 *  @return The configuration.
	 */
	public IMEConfiguration createConfiguration(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	conf	= getState().createObject(OAVBDIMetaModel.configuration_type);
					getState().setAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_configurations, conf);
					object	= new MConfigurationFlyweight(getState(), getHandle(), conf);
				}
			};
			return (IMEConfiguration)invoc.object;
		}
		else
		{
			Object	conf	= getState().createObject(OAVBDIMetaModel.configuration_type);
			getState().setAttributeValue(conf, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_configurations, conf);
			return new MConfigurationFlyweight(getState(), getHandle(), conf);
		}
	}
	
	
	/**
	 *  Get the model info for editing component level settings.
	 */
	public ModelInfo	getModelInfo()
	{
		return getBDIFeature()!=null ? (ModelInfo)getBDIFeature().getModel(getHandle()) : info;
	}
}
