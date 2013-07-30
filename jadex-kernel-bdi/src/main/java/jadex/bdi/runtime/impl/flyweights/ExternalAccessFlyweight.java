package jadex.bdi.runtime.impl.flyweights;

import jadex.base.Starter;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MCapabilityFlyweight;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  External access interface.
 */
public class ExternalAccessFlyweight extends ElementFlyweight implements IBDIExternalAccess
{
	//-------- attributes --------
	
	/** The service provider. */
	protected IServiceProvider provider;
	
	/** The component identifier. */
	protected IComponentIdentifier	cid;
	
	/** The parent component identifier. */
	protected IComponentIdentifier	parent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new capability flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param adapter	The adapter.
	 */
	public ExternalAccessFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
		this.provider = getInterpreter().getServiceProvider();
		this.cid = getInterpreter().getAgentAdapter().getComponentIdentifier();
		this.parent = getInterpreter().getParent().getComponentIdentifier();
	}

	//-------- methods --------
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo	getModel()
	{
		return getInterpreter().getModel();
	}

	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IComponentIdentifier getParent()
	{
		return parent;
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return cid;
	}
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(IComponentStep step)
	{
		return getInterpreter().scheduleStep(step, getHandle());
	}
	
	/**
	 *  Schedule an immediate step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleImmediate(IComponentStep step)
	{
		return getInterpreter().scheduleImmediate(step, getHandle());
	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(final IComponentStep step, final long delay)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getInterpreter().getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(getInterpreter().createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleStep(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public IFuture scheduleImmediate(final IComponentStep step, final long delay)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getInterpreter().getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(getInterpreter().createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IClockService cs = (IClockService)result;
				cs.createTimer(delay, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						scheduleImmediate(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}));
		
		return ret;
	}
	
	//-------- normal --------
		
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						getInterpreter().createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			getInterpreter().createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						Object cs = getState().getAttributeValue(getInterpreter().getAgent(), OAVBDIRuntimeModel.agent_has_state);
						if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
						{
							getInterpreter().killComponent().addResultListener(new DelegationResultListener(ret));
						}
						else
						{
							ret.setException(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
						}
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			Object cs = getState().getAttributeValue(getInterpreter().getAgent(), OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getInterpreter().startMonitorConsequences();
				getInterpreter().killComponent().addResultListener(new DelegationResultListener(ret));
				getInterpreter().endMonitorConsequences();
			}
			else
			{
				ret.setException(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get external access of subcapability.
	 *  @param name The capability name.
	 *  @return The future with external access.
	 */
	public IFuture getExternalAccess(final String name)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						StringTokenizer stok = new StringTokenizer(name, ".");
						Object handle = getHandle();
						while(stok.hasMoreTokens())
						{
							String subcapname = stok.nextToken();
							Object subcapref = getState().getAttributeValue(handle, OAVBDIRuntimeModel.capability_has_subcapabilities, subcapname);
							if(subcapref==null)
							{
								ret.setException(new RuntimeException("Capability not found: "+subcapname));
								return;
							}
							handle = getState().getAttributeValue(subcapref, OAVBDIRuntimeModel.capabilityreference_has_capability);
						}
						ret.setResult(new ExternalAccessFlyweight(getState(), handle));
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			StringTokenizer stok = new StringTokenizer(name, ".");
			Object handle = getHandle();
			while(stok.hasMoreTokens())
			{
				String subcapname = stok.nextToken();
				Object subcapref = getState().getAttributeValue(handle, OAVBDIRuntimeModel.capability_has_subcapabilities, subcapname);
				if(subcapref==null)
				{
					ret.setException(new RuntimeException("Capability not found: "+subcapname));
					return ret;
				}
				handle = getState().getAttributeValue(subcapref, OAVBDIRuntimeModel.capabilityreference_has_capability);
			}
			ret.setResult(new ExternalAccessFlyweight(getState(), handle));
		}
		
		return ret;
	}
	
	/**
	 *  Get subcapability names.
	 *  @return The future with array of subcapability names.
	 */
	public IFuture getSubcapabilityNames()
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						String[] res = SUtil.EMPTY_STRING_ARRAY;
						Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_subcapabilities);
						if(coll!=null)
						{
							res = new String[coll.size()];
							int i=0;
							for(Iterator it=coll.iterator(); it.hasNext(); i++)
							{
								Object cref = it.next();
								String name = (String)getState().getAttributeValue(cref, OAVBDIRuntimeModel.capabilityreference_has_name);
								res[i] = name;
							}
						}
						ret.setResult(res);
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			String[] res = SUtil.EMPTY_STRING_ARRAY;
			Collection coll = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.capability_has_subcapabilities);
			if(coll!=null)
			{
				res = new String[coll.size()];
				int i=0;
				for(Iterator it=coll.iterator(); it.hasNext(); i++)
				{
					Object cref = it.next();
					String name = (String)getState().getAttributeValue(cref, OAVBDIRuntimeModel.capabilityreference_has_name);
					res[i] = name;
				}
			}
			ret.setResult(res);
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					object = new MCapabilityFlyweight(getState(), me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return new MCapabilityFlyweight(getState(), me);
		}
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren(String type)
	{
		return getInterpreter().getChildren(type);
	}
	
	/**
	 *  Get the model name of a component type.
	 *  @param ctype The component type.
	 *  @return The model name of this component type.
	 */
	public IFuture getFileName(final String ctype)
	{
		final Future ret = new Future();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						String fn = getInterpreter().getComponentFilename(ctype);
						if(fn!=null)
						{
							ret.setResult(fn);
						}
						else
						{
							ret.setException(new RuntimeException("Unknown component type: "+ctype));
						}
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			String fn = getInterpreter().getComponentFilename(ctype);
			if(fn!=null)
			{
				ret.setResult(fn);
			}
			else
			{
				ret.setException(new RuntimeException("Unknown component type: "+ctype));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a space of the application.
	 *  @param name	The name of the space.
	 *  @return	The space.
	 */
	public IFuture getExtension(final String name)
	{
		final Future ret = new Future();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getExtension(name));
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(getInterpreter().getExtension(name));
		}
		
		return ret;
	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return getInterpreter().getLocalType();
	}
	
//	/**
//	 *  Add an component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture<Void> addComponentListener(final IComponentListener listener)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().addComponentListener(listener).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//		}
//		else
//		{
//			getInterpreter().addComponentListener(listener).addResultListener(new DelegationResultListener<Void>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Remove a component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture<Void> removeComponentListener(final IComponentListener listener)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().removeComponentListener(listener).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//		}
//		else
//		{
//			getInterpreter().removeComponentListener(listener).addResultListener(new DelegationResultListener<Void>(ret));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(final IFilter<IMonitoringEvent> filter, final boolean initial)
	{
		final SubscriptionIntermediateDelegationFuture<IMonitoringEvent> ret = new SubscriptionIntermediateDelegationFuture<IMonitoringEvent>();
		
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getComponentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().subscribeToEvents(filter, initial);
						TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
						fut.addResultListener(lis);
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getComponentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().subscribeToEvents(filter, initial);
			TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IFuture<Map<String, Object>> getArguments()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getArguments());
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(getInterpreter().getArguments());
		}
		
		return ret;
	}
	
	/**
	 *  Get the component results.
	 *  @return The results.
	 */
	public IFuture<Map<String, Object>> getResults()
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getResults());
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(getInterpreter().getResults());
		}
		
		return ret;
	}
	
	/**
	 *  Test if current thread is external thread.
	 *  @return True if the current thread is not the component thread.
	 */
	public boolean isExternalThread()
	{
		return getInterpreter().getAgentAdapter().isExternalThread();
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNonFunctionalPropertyNames()
	{
		final Future<String[]> ret = new Future<String[]>();
		
		// todo
		ret.setException(new UnsupportedOperationException());
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyNames());
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyNames());
//						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(getInterpreter().getNonFunctionalPropertyNames());
		}
		
		return ret;
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNfPropertyMetaInfo(final String name)
	{
		final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getNfPropertyMetaInfo(name));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setResult(getInterpreter().getNfPropertyMetaInfo(name));
//						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(getInterpreter().getNfPropertyMetaInfo(name));
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNonFunctionalPropertyValue(final String name, final Class<T> type)
	{
		final Future<T> ret = new Future<T>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type));
//						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type));
		}
		
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T, U> IFuture<T> getNonFunctionalPropertyValue(final String name, final Class<T> type, final Class<U> unit)
	{
		final Future<T> ret = new Future<T>();
		
		if(getInterpreter().getAgentAdapter().isExternalThread())
		{
			try
			{
				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
				{
					public void run() 
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type, unit));
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type, unit));
//						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(getInterpreter().getNonFunctionalPropertyValue(name, type, unit));
		}
		
		return ret;
	}
}
