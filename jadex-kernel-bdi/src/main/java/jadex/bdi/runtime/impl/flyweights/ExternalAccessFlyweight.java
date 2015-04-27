package jadex.bdi.runtime.impl.flyweights;

import jadex.base.Starter;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MCapabilityFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
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
public class ExternalAccessFlyweight extends ElementFlyweight implements IExternalAccess
{
	//-------- attributes --------
	
	/** The valid flag. */
	protected boolean	valid;	
	
	/** The service provider. */
//	protected IInternalAccess provider;
	
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
//		this.provider = getInterpreter().getServiceProvider();
		this.cid = getInterpreter().getComponentIdentifier();
		this.parent = getInterpreter().getComponentIdentifier().getParent();
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
	
//	/**
//	 *  Get the service provider.
//	 *  @return The service provider.
//	 */
//	public IServiceProvider getServiceProvider()
//	{
//		return provider;
//	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(IComponentStep step)
	{
		return getBDIFeature().scheduleStep(step, getHandle());
	}
	
	/**
	 *  Schedule an immediate step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleImmediate(IComponentStep step)
	{
		return getBDIFeature().scheduleImmediate(step, getHandle());
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
		
		SServiceProvider.getService(getInterpreter(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(getInterpreter().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
		
		SServiceProvider.getService(getInterpreter(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(getInterpreter().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						getInterpreter().getComponentFeature(ISubcomponentsFeature.class).createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
						return IFuture.DONE;
					}
				}); 
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getComponentIdentifier(), new Runnable()
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
			getInterpreter().getComponentFeature(ISubcomponentsFeature.class).createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						Object cs = getState().getAttributeValue(getBDIFeature().getAgent(), OAVBDIRuntimeModel.agent_has_state);
						if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
						{
							getInterpreter().killComponent().addResultListener(new DelegationResultListener(ret));
						}
						else
						{
							ret.setException(new RuntimeException("Component not running: "+getComponentIdentifier().getName()));
						}
						return IFuture.DONE;
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
			Object cs = getState().getAttributeValue(getBDIFeature().getAgent(), OAVBDIRuntimeModel.agent_has_state);
			if(OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_ALIVE.equals(cs))
			{
				//	System.out.println("set to terminating");
				getBDIFeature().startMonitorConsequences();
				getInterpreter().killComponent().addResultListener(new DelegationResultListener(ret));
				getBDIFeature().endMonitorConsequences();
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
		
		if(!getBDIFeature().isPlanThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
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
						return IFuture.DONE;
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
		
		if(!getBDIFeature().isPlanThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
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
						return IFuture.DONE;
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
		if(isExternalThread())
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
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						String fn = getInterpreter().getComponentFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
						if(fn!=null)
						{
							ret.setResult(fn);
						}
						else
						{
							ret.setException(new RuntimeException("Unknown component type: "+ctype));
						}
						return IFuture.DONE;
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
			String fn = getInterpreter().getComponentFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
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
	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public IFuture getExtension(final String name)
//	{
//		final Future ret = new Future();
//		
//		if(isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ret.setResult(getInterpreter().getExtension(name));
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
//			ret.setResult(getInterpreter().getExtension(name));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		return getInterpreter().getComponentFeature(ISubcomponentsFeature.class).getLocalType();
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
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(final IFilter<IMonitoringEvent> filter, final boolean initial, final PublishEventLevel elm)
	{
		// No NoTimeoutFuture needed as is already created internally.
		final SubscriptionIntermediateDelegationFuture<IMonitoringEvent> ret = new SubscriptionIntermediateDelegationFuture<IMonitoringEvent>();
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
						TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
						fut.addResultListener(lis);
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getComponentIdentifier(), new Runnable()
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
			ISubscriptionIntermediateFuture<IMonitoringEvent> fut = getInterpreter().getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
			TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return ret;
	}
	
	/**
	 *  Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
	{
		// No NoTimeoutFuture needed as is already created internally.
		final SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>> ret = new SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>>();
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).subscribeToResults();
						TerminableIntermediateDelegationResultListener<Tuple2<String, Object>> lis = new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut);
						fut.addResultListener(lis);
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getInterpreter().getComponentIdentifier(), new Runnable()
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
			ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).subscribeToResults();
			TerminableIntermediateDelegationResultListener<Tuple2<String, Object>> lis = new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut);
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
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).getArguments());
						return IFuture.DONE;
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
			ret.setResult(getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).getArguments());
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
		
		if(isExternalThread())
		{
			try
			{
				getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).getResults());
						return IFuture.DONE;
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
			ret.setResult(getInterpreter().getComponentFeature(IArgumentsResultsFeature.class).getResults());
		}
		
		return ret;
	}
	
	/**
	 *  Test if current thread is external thread.
	 *  @return True if the current thread is not the component thread.
	 */
	public boolean isExternalThread()
	{
		return !getInterpreter().getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
	
//	/**
//	 *  Returns the names of all non-functional properties of this service.
//	 *  @return The names of the non-functional properties of this service.
//	 */
//	public IFuture<String[]> getNFPropertyNames()
//	{
//		final Future<String[]> ret = new Future<String[]>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Returns the names of all non-functional properties of this service.
//	 *  @return The names of the non-functional properties of this service.
//	 */
//	public IFuture<String[]> getNFAllPropertyNames()
//	{
//		final Future<String[]> ret = new Future<String[]>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//		}
//		
//		return ret;
//	}
//	
//	
//	/**
//	 *  Returns the meta information about a non-functional property of this service.
//	 *  @param name Name of the property.
//	 *  @return The meta information about a non-functional property of this service.
//	 */
//	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
//	{
//		final Future<Map<String, INFPropertyMetaInfo>> ret = new Future<Map<String, INFPropertyMetaInfo>>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String,INFPropertyMetaInfo>>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String,INFPropertyMetaInfo>>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String,INFPropertyMetaInfo>>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Returns the meta information about a non-functional property of this service.
//	 *  @param name Name of the property.
//	 *  @return The meta information about a non-functional property of this service.
//	 */
//	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(final String name)
//	{
//		final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						getInterpreter().getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Returns the current value of a non-functional property of this service.
//	 *  @param name Name of the property.
//	 *  @return The current value of a non-functional property of this service.
//	 */
//	public <T> IFuture<T> getNFPropertyValue(final String name)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						IFuture<T> fut = getInterpreter().getNFPropertyValue(name);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						IFuture<T> fut = getInterpreter().getNFPropertyValue(name);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			IFuture<T> fut = getInterpreter().getNFPropertyValue(name);
//			fut.addResultListener(new DelegationResultListener<T>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
//	 *  @param name Name of the property.
//	 *  @param type Type of the property value.
//	 *  @return The current value of a non-functional property of this service.
//	 */
////	public <T, U> IFuture<T> getNFPropertyValue(final String name, final Class<U> unit)
//	public <T, U> IFuture<T> getNFPropertyValue(final String name, final U unit)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		if(getInterpreter().getAgentAdapter().isExternalThread())
//		{
//			try
//			{
//				getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						IFuture<T> fut = getInterpreter().getNFPropertyValue(name, unit);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						IFuture<T> fut = getInterpreter().getNFPropertyValue(name, unit);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
////						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			IFuture<T> fut = getInterpreter().getNFPropertyValue(name, unit);
//			fut.addResultListener(new DelegationResultListener<T>(ret));
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Add a non-functional property.
//	 *  @param metainfo The metainfo.
//	 */
//	public IFuture<Void> addNFProperty(final INFProperty<?, ?> nfprop)
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
//						getInterpreter().addNFProperty(nfprop);
//						ret.setResult(null);
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().addNFProperty(nfprop);
//						ret.setResult(null);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().addNFProperty(nfprop);
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Add a non-functional property.
//	 *  @param metainfo The metainfo.
//	 */
//	public IFuture<Void> removeNFProperty(final String name)
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
//						getInterpreter().removeNFProperty(name);
//						ret.setResult(null);
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
//						getInterpreter().removeNFProperty(name);
//						ret.setResult(null);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().removeNFProperty(name);
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Shutdown the provider.
//	 */
//	public IFuture<Void> shutdownNFPropertyProvider()
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
//						getInterpreter().shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(getInterpreter().getAgentAdapter().getComponentIdentifier(), new Runnable()
//				{
//					public void run()
//					{
////						interpreter.removeNFProperty(name);
////						ret.setResult(null);
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			getInterpreter().shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Check if the component is directly available.
	 *  An external access becomes invalid, when a component
	 *  is persisted or terminated.
	 */
	public boolean	isValid()
	{
		return valid;
	}
	
	/**
	 *  Invalidate the external access.
	 */
	public void	invalidate()
	{
		this.valid	= false;
//		this.provider	= null;
		cleanup();	// ???
	}
}
