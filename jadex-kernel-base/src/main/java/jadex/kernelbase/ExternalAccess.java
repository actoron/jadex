package jadex.kernelbase;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.ComponentPersistedException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

/**
 *  External access for applications.
 */
public class ExternalAccess implements IExternalAccess
{
	//-------- attributes --------

	/** The valid flag. */
	protected boolean	valid;
	
	/** The terminated flag. */
	protected boolean	terminated;
	
	/** The component identifier. */
	protected IComponentIdentifier	cid;
	
	/** The component. */
	protected IInternalAccess ia;

	/** The toString value. */
	protected String tostring;
	
	/** The model info (cached when persisted). */
	// Todo: should not be kept in memory?
	protected IModelInfo	model;
	
	/** The local type (cached when persisted). */
	// Todo: should not be kept in memory?
//	protected String	localtype;
	
	/** The results (cached after termination). */
	protected Map<String, Object>	results;
	
	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(IInternalAccess ia)
	{
		this.valid	= true;
		this.ia = ia;
		this.cid	= ia.getComponentIdentifier();
		this.tostring = cid.getLocalName();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public IModelInfo getModel()
	{
		if(terminated)
		{
			throw new ComponentTerminatedException(cid);
		}
		else if(!valid)
		{
			return model;
		}
		else
		{
			return ia.getModel();
		}
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return cid;
	}
	
//	/**
//	 *  Get the id of the component including addresses.
//	 *  @return	The component id.
//	 */
//	public IFuture<ITransportComponentIdentifier> getTransportComponentIdentifier()
//	{
//		return SServiceProvider.getLocalService(this.getInternalAccess(), ITransportAddressService.class, 
//			RequiredServiceInfo.SCOPE_PLATFORM).getTransportComponentIdentifier(getComponentIdentifier());
//	}
	
//	/**
//	 *  Get a space of the application.
//	 *  @param name	The name of the space.
//	 *  @return	The space.
//	 */
//	public ISpace getSpace(final String name)
//	{
//		// Application getSpace() is synchronized
//		return application.getSpace(name);
//		
////		final Future ret = new Future();
////		
////		if(adapter.isExternalThread())
////		{
////			adapter.invokeLater(new Runnable() 
////			{
////				public void run() 
////				{
////					ret.setResult(application.getSpace(name));
////				}
////			});
////		}
////		else
////		{
////			ret.setResult(application.getSpace(name));
////		}
////		
////		return ret;
//	}
	
//	/**
//	 *  Get the parent access (if any).
//	 *  @return The parent access.
//	 */
//	public IExternalAccess getParentAccess()
//	{
//		if(!valid)
//		{
//			throw terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid);
//		}
//
//		return adapter.getParent();
//	}
	
//	/**
//	 *  Get the application component.
//	 */
//	public IServiceProvider getServiceProvider()
//	{
//		if(!valid)
//		{
//			throw terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid);
//		}
//
//		return (IServiceProvider)ia.getServiceContainer();
//	}
	
	protected volatile Future<Map<String, Object>> killfut;
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return killComponent(null);
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(final Exception e)
	{
//		System.out.println("exta killComp: "+getComponentIdentifier());
		
		boolean	kill	= false;
		synchronized(this)
		{
			if(killfut==null)
			{
				kill	= true;
				killfut	= new Future<Map<String, Object>>();
			}
		}
		
//		ret.addResultListener(new IResultListener<Map<String,Object>>()
//		{
//			public void resultAvailable(Map<String, Object> result)
//			{
//				System.out.println("killComp res");
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("exOcc "+exception);
//			}
//		});
		if(kill)
		{
			if(!valid)
			{
				killfut.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
			}
			else if(!ia.getComponentFeature(IExecutionFeature.class).isComponentThread())
			{
				try
				{
	//				if(cid.getParent()==null)
	//				{
	//					System.out.println("platform e: "+cid.getName());
	//					Thread.dumpStack();
	//				}
	//				System.out.println("ext kill: "+cid);
					ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
	//						System.out.println("int kill");
							if(!valid)
							{
								// Todo: consistency between external access and invokeLater()
								killfut.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
							}
							else
							{
								ia.killComponent(e).addResultListener(new DelegationResultListener<Map<String, Object>>(killfut)
								{
									public void customResultAvailable(Map<String, Object> result)
									{
										super.customResultAvailable(result);
									}
									public void exceptionOccurred(Exception exception)
									{
										super.exceptionOccurred(exception);
									}
								});
							}
							
							return IFuture.DONE;
						}
						
	//					public String toString()
	//					{
	//						return "JOOOOOOOOOOOOOOOOOOOOO";
	//					}
					}); 
				}
				catch(final Exception ex)
				{
					Starter.scheduleRescueStep(cid, new Runnable()
					{
						public void run()
						{
							killfut.setException(ex);
						}
					});
				}
			}
			else
			{
	//			if(cid.getParent()==null)
	//			{
	//				System.err.println("platform i: "+cid.getName());
	//				Thread.dumpStack();
	//			}
				ia.killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(killfut));
			}
		}
		
		return killfut;
	}
	
	
	/**
	 *  Create a result listener that will be 
	 *  executed on the component thread.
	 *  @param listener The result listener.
	 *  @return A result listener that is called on component thread.
	 * /
	public IResultListener createResultListener(IResultListener listener)
	{
		return application.createResultListener(listener);
	}*/
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(final String type)
	{
		final Future<IComponentIdentifier[]> ret = new Future<IComponentIdentifier[]>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			ia.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getComponentFeature(ISubcomponentsFeature.class).createChild(component)
							.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			ia.getComponentFeature(ISubcomponentsFeature.class).createChild(component)
				.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		}
		
		return ret;
	}

	
	/**
	 *  Get the file name of a component type.
	 *  @param ctype The component type.
	 *  @return The file name of this component type.
	 */
	public IFuture<String> getFileName(final String ctype)
	{
		final Future<String> ret = new Future<String>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						String fn = ia.getComponentFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
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
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			String fn = ia.getComponentFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
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
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType()
	{
		if(terminated)
		{
			throw new ComponentTerminatedException(cid);
		}
//		else if(!valid)
//		{
//			return localtype;
//		}
//		else
//		{
			return ia.getComponentFeature(ISubcomponentsFeature.class).getLocalType();
//		}
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(final IComponentStep<T> step)
	{
		if(step==null)
		{
			throw new NullPointerException("No step. Maybe decoding error?");
		}
		
		Method	m	= SReflect.getMethod(step.getClass(), "execute", new Class[]{IInternalAccess.class});
		final Future<T>	ret = m!=null ? (Future<T>)FutureFunctionality.getDelegationFuture(m.getReturnType(), new FutureFunctionality((Logger)null)) : new Future<T>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else
		{
			IFuture<T>	fut	= ia.getComponentFeature(IExecutionFeature.class).scheduleStep(step);
			FutureFunctionality.connectDelegationFuture(ret, fut);
		}
		
		return ret;
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(int priority, final IComponentStep<T> step)
	{
		if(step==null)
		{
			throw new NullPointerException("No step. Maybe decoding error?");
		}
		
		Method	m	= SReflect.getMethod(step.getClass(), "execute", new Class[]{IInternalAccess.class});
		final Future<T>	ret = m!=null ? (Future<T>)FutureFunctionality.getDelegationFuture(m.getReturnType(), new FutureFunctionality((Logger)null)) : new Future<T>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else
		{
			IFuture<T>	fut	= ia.getComponentFeature(IExecutionFeature.class).scheduleStep(priority, step);
			FutureFunctionality.connectDelegationFuture(ret, fut);
		}
		
		return ret;
	}
	
//	/**
//	 *  Execute some code on the component's thread.
//	 *  Unlike scheduleStep(), the action will also be executed
//	 *  while the component is suspended.
//	 *  @param action	Code to be executed on the component's thread.
//	 *  @return The result of the step.
//	 */
//	public <T>	IFuture<T> scheduleImmediate(final IComponentStep<T> step)
//	{
//		return ia.getComponentFeature(IExecutionFeature.class).scheduleImmediate(step);
//	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleStep(final IComponentStep<T> step, final long delay)
	{
		final Future<T> ret = new Future<T>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else
		{
			SServiceProvider.getService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
			{
				public void customResultAvailable(IClockService cs)
				{
					cs.createTimer(delay, new ITimedObject()
					{
						public void timeEventOccurred(long currenttime)
						{
							scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
						}
					});
				}
			}));
		}
		
		return ret;
	}
	
	/**
	 *  Schedule a step of the component.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the component.
	 *  @param delay The delay to wait before step should be done.
	 *  @return The result of the step.
	 */
	public <T>	IFuture<T> scheduleStep(final int priority, final IComponentStep<T> step, final long delay)
	{
		final Future<T> ret = new Future<T>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else
		{
			SServiceProvider.getService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
			{
				public void customResultAvailable(IClockService cs)
				{
					cs.createTimer(delay, new ITimedObject()
					{
						public void timeEventOccurred(long currenttime)
						{
							scheduleStep(step, priority).addResultListener(new DelegationResultListener<T>(ret));
						}
					});
				}
			}));
		}
		
		return ret;
	}
	
//	/**
//	 *  Execute some code on the component's thread.
//	 *  Unlike scheduleStep(), the action will also be executed
//	 *  while the component is suspended.
//	 *  @param action	Code to be executed on the component's thread.
//	 *  @param delay The delay to wait before step should be done.
//	 *  @return The result of the step.
//	 */
//	public <T> IFuture<T> scheduleImmediate(final IComponentStep<T> step, final long delay)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else
//		{
//			SServiceProvider.getService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(createResultListener(new ExceptionDelegationResultListener<IClockService, T>(ret)
//			{
//				public void customResultAvailable(IClockService cs)
//				{
//					cs.createTimer(delay, new ITimedObject()
//					{
//						public void timeEventOccurred(long currenttime)
//						{
//							scheduleImmediate(step).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//			}));
//		}
//		
//		return ret;
//	}
	
	// todo: move to external feature!?
	/**
	 *  Subscribe to component events.
	 *  @param filter An optional filter.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(final IFilter<IMonitoringEvent> filter, final boolean initial, final PublishEventLevel elm)
	{
		// No NoTimeoutFuture needed as is already created internally.
		final SubscriptionIntermediateDelegationFuture<IMonitoringEvent> ret = new SubscriptionIntermediateDelegationFuture<IMonitoringEvent>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<IMonitoringEvent> fut = ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
						TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
						fut.addResultListener(lis);
						return IFuture.DONE;
					}
				}).addResultListener(new ExceptionDelegationResultListener<Void, Collection<IMonitoringEvent>>(ret)
				{
					public void customResultAvailable(Void result)
					{
						// Nop. only forward exception.
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			ISubscriptionIntermediateFuture<IMonitoringEvent> fut = ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
			TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return ret;
	}
	
	// todo: move to external feature!?
	/**
	 *  Subscribe to receive results.
	 */
	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
	{
		// No NoTimeoutFuture needed as is already created internally.
		final SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>> ret = new SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>>();
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = ia.getComponentFeature(IArgumentsResultsFeature.class).subscribeToResults(); 
						TerminableIntermediateDelegationResultListener<Tuple2<String, Object>> lis = new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut);
						fut.addResultListener(lis);
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = ia.getComponentFeature(IArgumentsResultsFeature.class).subscribeToResults(); 
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
		
		if(!valid)
		{
			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(ia.getComponentFeature(IArgumentsResultsFeature.class).getArguments());
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
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
			ret.setResult(ia.getComponentFeature(IArgumentsResultsFeature.class).getArguments());
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
		
		if(!valid)
		{
			ret.setResult(results);
		}
		else if(isExternalThread())
		{
			try
			{
				ia.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(ia.getComponentFeature(IArgumentsResultsFeature.class).getResults());
						return IFuture.DONE;
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(cid, new Runnable()
				{
					public void run()
					{
						// Should be possible to get the results even if component is already terminated?!
						ret.setResult(ia.getComponentFeature(IArgumentsResultsFeature.class).getResults());
//						ret.setException(e);
					}
				});
			}
		}
		else
		{
			ret.setResult(ia.getComponentFeature(IArgumentsResultsFeature.class).getResults());
		}
		
		return ret;
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
	{
		final Future<Map<String, INFPropertyMetaInfo>> ret = new Future<Map<String, INFPropertyMetaInfo>>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String,INFPropertyMetaInfo>>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						ret.setResult(interpreter.getNFPropertyNames());
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String,INFPropertyMetaInfo>>(ret));
//		}
		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFPropertyNames()
	{
		final Future<String[]> ret = new Future<String[]>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						if(!valid)
//						{
//							ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//						}
//						else
//						{
//							ia.getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//						}
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						ret.setResult(interpreter.getNFPropertyNames());
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//		}
		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNFAllPropertyNames()
	{
		final Future<String[]> ret = new Future<String[]>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						ret.setResult(interpreter.getNFPropertyNames());
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//		}
		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(final String name)
	{
		final Future<INFPropertyMetaInfo> ret = new Future<INFPropertyMetaInfo>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						ret.setResult(interpreter.getNFPropertyMetaInfo(name));
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
//		}
		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  @param name Name of the property.
	 *  @return The current value of a non-functional property of this service.
	 */
	public <T> IFuture<T> getNFPropertyValue(final String name)
	{
		final Future<T> ret = new Future<T>();
//		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						IFuture<T> fut = ia.getNFPropertyValue(name);
//						fut.addResultListener(new IResultListener<T>()
//						{
//							public void resultAvailable(T result)
//							{
//								ret.setResult(result);
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								IExternalAccess parent = adapter.getParent();
//								if (parent != null)
//								{
//									IFuture<T> fut = parent.getNFPropertyValue(name);
//									fut.addResultListener(new DelegationResultListener<T>(ret));
//								}
//								else
//								{
//									ret.setResult(null);
//								}
//							}
//						});
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						IFuture<T> fut = interpreter.getNFPropertyValue(name);
////						fut.addResultListener(new DelegationResultListener<T>(ret));
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			IFuture<T> fut = ia.getNFPropertyValue(name);
//			fut.addResultListener(new IResultListener<T>()
//			{
//				public void resultAvailable(T result)
//				{
//					ret.setResult(result);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					IExternalAccess parent = adapter.getParent();
//					if (parent != null)
//					{
//						IFuture<T> fut = parent.getNFPropertyValue(name);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
//					}
//					else
//					{
//						ret.setResult(null);
//					}
//				}
//			});
//		}
//		
//		return ret;
		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
//	public <T, U> IFuture<T> getNFPropertyValue(final String name, final Class<U> unit)
	public <T, U> IFuture<T> getNFPropertyValue(final String name, final U unit)
	{
		final Future<T> ret = new Future<T>();
//		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						IFuture<T> fut = ia.getNFPropertyValue(name, unit);
//						fut.addResultListener(new IResultListener<T>()
//						{
//							public void resultAvailable(T result)
//							{
//								ret.setResult(result);
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								IExternalAccess parent = adapter.getParent();
//								if (parent != null)
//								{
//									IFuture<T> fut = parent.getNFPropertyValue(name, unit);
//									fut.addResultListener(new DelegationResultListener<T>(ret));
//								}
//								else
//								{
//									ret.setResult(null);
//								}
//							}
//						});
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						IFuture<T> fut = interpreter.getNFPropertyValue(name, unit);
////						fut.addResultListener(new DelegationResultListener<T>(ret));
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			IFuture<T> fut = ia.getNFPropertyValue(name, unit);
//			fut.addResultListener(new IResultListener<T>()
//			{
//				public void resultAvailable(T result)
//				{
//					ret.setResult(result);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					IExternalAccess parent = adapter.getParent();
//					if (parent != null)
//					{
//						IFuture<T> fut = parent.getNFPropertyValue(name, unit);
//						fut.addResultListener(new DelegationResultListener<T>(ret));
//					}
//					else
//					{
//						ret.setResult(null);
//					}
//				}
//			});
//		}
//		
		ret.setException(new UnsupportedOperationException());
		return ret;
	}
	
	/**
	 *  Add a non-functional property.
	 *  @param nfprop The property.
	 */
	public IFuture<Void> addNFProperty(final INFProperty<?, ?> nfprop)
	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(adapter.isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.addNFProperty(nfprop);
//						ret.setResult(null);
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
////						interpreter.addNFProperty(nfprop);
////						ret.setResult(null);
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.addNFProperty(nfprop);
//			ret.setResult(null);
//		}
		
//		return ret;
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a non-functional property.
	 *  @param The name.
	 */
	public IFuture<Void> removeNFProperty(final String name)
	{
//		final Future<Void> ret = new Future<Void>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(adapter.isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.removeNFProperty(name).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
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
//			ia.removeNFProperty(name).addResultListener(new DelegationResultListener<Void>(ret));
//		}
//		
//		return ret;
		
		return IFuture.DONE;
	}
	
	/**
	 *  Shutdown the provider.
	 */
	public IFuture<Void> shutdownNFPropertyProvider()
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				adapter.invokeLater(new Runnable() 
//				{
//					public void run() 
//					{
//						ia.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
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
//			ia.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret));
//		}
		
//		return ret;
		return IFuture.DONE;
	}
	
//	/**
//	 *  Get the interpreter.
//	 *  @return the interpreter.
//	 */
//	public StatelessAbstractInterpreter getInterpreter()
//	{
//		if(!valid)
//		{
//			throw terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid);
//		}
//
//		return ia;
//	}
	
	/**
	 *  Test if current thread is external thread.
	 *  @return True if the current thread is not the component thread.
	 */
	public boolean isExternalThread()
	{
		if(!valid)
		{
			throw terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid);
		}

		return !ia.getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
	
	/**
	 *  Check if the component is directly available.
	 *  An external access becomes invalid, when a component
	 *  is persisted or terminated.
	 */
	public boolean	isValid()
	{
		return valid;
	}
	
//	/**
//	 *  Invalidate the external access.
//	 *  @param terminated	Invalidated due to termination?
//	 */
//	public void	invalidate(boolean terminated)
//	{
//		this.terminated	= terminated;
//		this.valid	= false;
//		this.results	= ia.getResults();
//		
//		if(terminated)
//		{
//			model	= null;
//			localtype	= null;
//		}
//		else
//		{
//			model	= ia.getModel();
//			localtype	= ia.getLocalType();
//		}
//		
//		this.ia	= null;
//		this.adapter	= null;
//		this.provider	= null;
//	}
	
	/**
	 *  Create a result listener that is executed on the
	 *  component thread.
	 */
	public <T> IResultListener<T> createResultListener(IResultListener<T> listener)
	{
		return ia.getComponentFeature(IExecutionFeature.class).createResultListener(listener);
	}
	
//	/**
//	 * 
//	 */
//	public Class<?> getFeatureClass(Class<?> type)
//	{
//		IComponentFeature feat = (IComponentFeature)ia.getComponentFeature(type);
//		return feat.getExternalFacadeType(this);
//	}
	
	/**
	 * 
	 */
	public IInternalAccess getInternalAccess()
	{
		return ia;
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}
}
