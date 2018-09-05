package jadex.bridge.service.types.cms;

import java.util.Collection;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.ComponentPersistedException;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.nonfunctional.INFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.component.ComponentFutureFunctionality;
import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.future.TupleResult;

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
		this.cid	= ia.getId();
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
	public IComponentIdentifier	getId()
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
	
	/**
	 *  @deprecated From 3.0 - just use external access.
	 *  Get the application component.
	 */
	public IExternalAccess getServiceProvider()
	{
		return this;
//		if(!valid)
//		{
//			throw terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid);
//		}
//
//		return (IServiceProvider)ia.getServiceContainer();
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with external access.
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IExternalAccess getServiceContainer()
	{
		return this;
	}
	
	protected volatile Future<Map<String, Object>> killfut;
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return killComponent((Exception)null);
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
			else if(!ia.getFeature(IExecutionFeature.class).isComponentThread())
			{
				try
				{
	//				if(cid.getParent()==null)
	//				{
	//					System.out.println("platform e: "+cid.getName());
	//					Thread.dumpStack();
	//				}
	//				System.out.println("ext kill: "+cid);
					ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
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
		
		return getDecoupledFuture(killfut);
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
	public IFuture<IComponentIdentifier[]> getChildren(final String type, IComponentIdentifier parent)
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
				ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getChildren(type, parent).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
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
			ia.getChildren(type, parent).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
		}
		
		return getDecoupledFuture(ret);
	}
	
//	/**
//	 *  Create a subcomponent.
//	 *  @param component The instance info.
//	 */
//	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		if(!valid)
//		{
//			ret.setException(terminated ? new ComponentTerminatedException(cid) : new ComponentPersistedException(cid));
//		}
//		else if(isExternalThread())
//		{
//			try
//			{
//				ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ia.getFeature(ISubcomponentsFeature.class).createChild(component)
//							.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//						return IFuture.DONE;
//					}
//				});
//			}
//			catch(final Exception e)
//			{
//				Starter.scheduleRescueStep(cid, new Runnable()
//				{
//					public void run()
//					{
//						ret.setException(e);
//					}
//				});
//			}
//		}
//		else
//		{
//			ia.getFeature(ISubcomponentsFeature.class).createChild(component)
//				.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//		}
//		
//		return ret;
//	}

	
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
				ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						String fn = ia.getFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
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
			String fn = ia.getFeature(ISubcomponentsFeature.class).getComponentFilename(ctype);
			if(fn!=null)
			{
				ret.setResult(fn);
			}
			else
			{
				ret.setException(new RuntimeException("Unknown component type: "+ctype));
			}
		}
		
		return getDecoupledFuture(ret);
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
			return ia.getFeature(ISubcomponentsFeature.class).getLocalType();
//		}
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return getDecoupledFuture(ia.getFeature(IExecutionFeature.class).scheduleStep(step));
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleStep(int priority, IComponentStep<T> step)
	{
		return getDecoupledFuture(ia.getFeature(IExecutionFeature.class).scheduleStep(priority, step));
	}
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime)
	{
		return getDecoupledFuture(ia.getFeature(IExecutionFeature.class).waitForDelay(delay, step, realtime));

	}

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
	{
		return getDecoupledFuture(ia.getFeature(IExecutionFeature.class).waitForDelay(delay, step));
	}
	
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
				ia.getFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<IMonitoringEvent> fut = ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
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
			ISubscriptionIntermediateFuture<IMonitoringEvent> fut = ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
			TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return getDecoupledSubscriptionFuture(ret);
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
				ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = ia.getFeature(IArgumentsResultsFeature.class).subscribeToResults(); 
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
			ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = ia.getFeature(IArgumentsResultsFeature.class).subscribeToResults(); 
			TerminableIntermediateDelegationResultListener<Tuple2<String, Object>> lis = new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut);
			fut.addResultListener(lis);
		}
		
		return getDecoupledSubscriptionFuture(ret);
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
				ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(ia.getFeature(IArgumentsResultsFeature.class).getArguments());
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
			ret.setResult(ia.getFeature(IArgumentsResultsFeature.class).getArguments());
		}
		
		return getDecoupledFuture(ret);
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
			ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ret.setResult(ia.getFeature(IArgumentsResultsFeature.class).getResults());
					return IFuture.DONE;
				}
			}).addResultListener(null,	exception ->
			{
				Starter.scheduleRescueStep(cid, new Runnable()
				{
					public void run()
					{
						// Should be possible to get the results even if component is already terminated?!
						ret.setResult(ia.getFeature(IArgumentsResultsFeature.class).getResults());
					}
				});
			});
		}
		else
		{
			ret.setResult(ia.getFeature(IArgumentsResultsFeature.class).getResults());
		}
		
		return getDecoupledFuture(ret);
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
//		final Future<Void> ret = new Future<Void>();
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

		return !ia.getFeature(IExecutionFeature.class).isComponentThread();
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
		return ia.getFeature(IExecutionFeature.class).createResultListener(listener);
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
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return ia;
	}
	
	
	/**
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return cid == null? 0 : cid.hashCode();
	}

	/**
	 *  Test if an object equals this object.
	 *  @param obj The object to test.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof IExternalAccess)
			ret = ((IExternalAccess)obj).getId().equals(cid);
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}
	
	//-------- methods for searching --------
	
	/**
	 *  Search for matching services and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		return scheduleStep(new IComponentStep<T>()
		{
			@Override
			public IFuture<T> execute(IInternalAccess ia)
			{
				return ((IInternalRequiredServicesFeature)ia.getFeature(IRequiredServicesFeature.class)).resolveService(query, null);	// null for no proxy
			}
		});
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services or ServiceNotFoundException when not found.
	 */
	public <T>  ITerminableIntermediateFuture<T> searchServices(ServiceQuery<T> query)
	{
		return (ITerminableIntermediateFuture<T>)scheduleStep(new IComponentStep<Collection<T>>()
		{
			@Override
			public ITerminableIntermediateFuture<T> execute(IInternalAccess ia)
			{
				return ((IInternalRequiredServicesFeature)ia.getFeature(IRequiredServicesFeature.class)).resolveServices(query, null);	// null for no proxy
			}
		});
	}
	
	//-------- query methods --------

	/**
	 *  Add a service query.
	 *  Continuously searches for matching services.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		return (ISubscriptionIntermediateFuture<T>)scheduleStep(new IComponentStep<Collection<T>>()
		{
			@Override
			public ISubscriptionIntermediateFuture<T> execute(IInternalAccess ia)
			{
				// TODO no proxy
				return ia.getFeature(IRequiredServicesFeature.class).addQuery(query);
			}
		});
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		return (IFuture<IExternalAccess>)scheduleStep(new IComponentStep<IExternalAccess>()
		{
			@Override
			public IFuture<IExternalAccess> execute(IInternalAccess ia)
			{
				return ia.createComponent(component, info, resultlistener);
			}
		});
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info)
	{
		return (ISubscriptionIntermediateFuture<CMSStatusEvent>)scheduleStep(new IComponentStep<Collection<CMSStatusEvent>>()
		{
			// must declare ISubscriptionIntermediateFuture<CMSStatusEvent> as return value of method
			// because it is internally used to determine the future type
			@Override
			public ISubscriptionIntermediateFuture<CMSStatusEvent> execute(IInternalAccess ia)
			{
				return ia.createComponentWithResults(component, info);
			}
		});
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info)
	{
		@SuppressWarnings("unchecked")
		ITuple2Future<IComponentIdentifier, Map<String, Object>> ret = (ITuple2Future<IComponentIdentifier, Map<String, Object>>)scheduleStep(new IComponentStep<Collection<TupleResult>>()
		{
			@Override
			public ITuple2Future<IComponentIdentifier, Map<String, Object>> execute(IInternalAccess ia)
			{
				return ia.createComponent(component, info);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid)
	{
		return (IFuture<IExternalAccess>)scheduleStep(new IComponentStep<IExternalAccess>()
		{
			@Override
			public IFuture<IExternalAccess> execute(IInternalAccess ia)
			{
				return SComponentManagementService.getExternalAccess(cid, ia);
			}
		});
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid)
	{
		return (IFuture<Map<String, Object>>)scheduleStep(new IComponentStep<Map<String, Object>>()
		{
			@Override
			public IFuture<Map<String, Object>> execute(IInternalAccess ia)
			{
				return ia.killComponent(cid);
			}
		});
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription> getDescription()
	{
		return (IFuture<IComponentDescription>)scheduleStep(new IComponentStep<IComponentDescription>()
		{
			@Override
			public IFuture<IComponentDescription> execute(IInternalAccess ia)
			{
				return new Future<>(ia.getDescription());
			}
		});
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription> getDescription(IComponentIdentifier cid)
	{
		return (IFuture<IComponentDescription>)scheduleStep(new IComponentStep<IComponentDescription>()
		{
			@Override
			public IFuture<IComponentDescription> execute(IInternalAccess ia)
			{
				return ia.getDescription(cid);
			}
		});
	}
	
	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent(IComponentIdentifier componentid)
	{
		return (IFuture<Void>)scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return ia.suspendComponent(componentid);
			}
		});
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier componentid)
	{
		return (IFuture<Void>)scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return ia.resumeComponent(componentid);
			}
		});
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(IComponentIdentifier componentid, String stepinfo)
	{
		return (IFuture<Void>)scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return ia.stepComponent(componentid, stepinfo);
			}
		});
	}
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints)
	{
		return (IFuture<Void>)scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				return ia.setComponentBreakpoints(componentid, breakpoints);
			}
		});
	}
	
	/**
	 *  Add a component listener for a specific component.
	 *  The listener is registered for component changes.
	 *  @param cid	The component to be listened.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(IComponentIdentifier cid)
	{
		return (ISubscriptionIntermediateFuture<CMSStatusEvent>)scheduleStep(new IComponentStep<Collection<CMSStatusEvent>>()
		{
			@Override
			public  ISubscriptionIntermediateFuture<CMSStatusEvent> execute(IInternalAccess ia)
			{
				return ia.listenToComponent(cid);
			}
		});
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)//, boolean remote)
	{
		return (IFuture<IComponentDescription[]>)scheduleStep(new IComponentStep<IComponentDescription[]>()
		{
			@Override
			public IFuture<IComponentDescription[]> execute(IInternalAccess ia)
			{
				return ia.searchComponents(adesc, con);
			}
		});
	}

	/**
	 *  Returns a future that schedules back to calling component if necessary..
	 *  @param infut Input future.
	 *  @return 
	 */
	protected <T> IFuture<T> getDecoupledFuture(IFuture<T> infut)
	{
		IFuture<T> ret = infut;
		IInternalAccess caller = IInternalExecutionFeature.LOCAL.get();
		if (caller != null && !ia.equals(caller))
		{
			IComponentIdentifier callerplat = caller.getId().getRoot();
			Boolean issim = (Boolean) Starter.getPlatformValue(callerplat, IClockService.SIMULATION_CLOCK_FLAG);
			if (Boolean.TRUE.equals(issim) && !callerplat.equals(ia.getId().getRoot()))
				((IInternalExecutionFeature) caller.getFeature(IExecutionFeature.class)).addSimulationBlocker(infut);
			
			IFuture<T> newret = FutureFunctionality.getDelegationFuture(infut, new ComponentFutureFunctionality(caller)
			{
				public void scheduleBackward(ICommand<Void> command)
				{
					if(!ia.getFeature(IExecutionFeature.class).isComponentThread())
					{
						ia.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess intaccess)
							{
								command.execute(null);
								return IFuture.DONE;
							}
						}).addResultListener(new IResultListener<Void>()
						{
							public void exceptionOccurred(Exception exception)
							{
								System.err.println("Unexpected Exception: "+command);
								exception.printStackTrace();
							}
							
							public void resultAvailable(Void result)
							{
							}
						});
					}
					else
					{
						command.execute(null);
					}
				}
			});
			ret = newret;
		}
		
		
		
		return ret;
	}
	
	/**
	 *  Returns a future that schedules back to calling component if necessary..
	 *  @param infut Input future.
	 *  @return 
	 */
	protected <T> ISubscriptionIntermediateFuture<T> getDecoupledSubscriptionFuture(final ISubscriptionIntermediateFuture<T> infut)
	{
		return (ISubscriptionIntermediateFuture<T>) getDecoupledFuture(infut);
	}
	
	
}