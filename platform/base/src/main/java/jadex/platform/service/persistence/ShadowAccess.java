package jadex.platform.service.persistence;

/**
 *  A shadow access holds a reference to the external access of
 *  a component, but is also able to resurrect the component
 *  transparently, if it has been persisted.
 */
public class ShadowAccess //implements IExternalAccess
{
//	//-------- attributes --------
//	
//	/** The external access. */
//	protected IExternalAccess	access;
//	
//	/** The shadow service provider. */
//	protected IServiceProvider	provider;
//	
//	/** The swap service. */
//	protected ISwapService	swapservice;
//	
//	//-------- constructors --------
//	
//	/**
//	 *  Create a new shadow access. 
//	 */
//	public ShadowAccess(IExternalAccess access)
//	{
//		this.access	= access;
//		this.provider	= new ShadowServiceProvider();
//		
//		SServiceProvider.searchService(access, new ServiceQuery<>( ISwapService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//			.addResultListener(new IResultListener<ISwapService>()
//		{
//			public void resultAvailable(ISwapService swapservice)
//			{
//				ShadowAccess.this.swapservice	= swapservice;
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				Logger.getLogger(ShadowAccess.this.access.getComponentIdentifier().getName())
//					.severe("No swap service for "+ShadowAccess.this.access.getComponentIdentifier()+"!? "+exception);
//			}
//		});
//	}
//	
//	
//	//-------- methods --------
//	
//	/**
//	 *  Restore the component.
//	 */
//	protected IFuture<Void>	resurrect()
//	{
//		return swapservice.swapFromStorage(getComponentIdentifier());
//	}
//	
//	//-------- IExternalAccess interface --------
//	
//	public IFuture<Void> addNFProperty(final INFProperty<?, ?> nfprop)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		access.addNFProperty(nfprop).addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.addNFProperty(nfprop).addResultListener(new DelegationResultListener<Void>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component)
//	{
//		final Future<IComponentIdentifier>	ret	= new Future<IComponentIdentifier>();
//		access.createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IComponentIdentifier>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.createChild(component).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<Map<String, Object>> getArguments()
//	{
//		final Future<Map<String, Object>>	ret	= new Future<Map<String, Object>>();
//		access.getArguments().addResultListener(new DelegationResultListener<Map<String, Object>>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Map<String, Object>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getArguments().addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<IComponentIdentifier[]> getChildren(final String type)
//	{
//		final Future<IComponentIdentifier[]>	ret	= new Future<IComponentIdentifier[]>();
//		access.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IComponentIdentifier[]>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getChildren(type).addResultListener(new DelegationResultListener<IComponentIdentifier[]>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IComponentIdentifier getComponentIdentifier()
//	{
//		// Cached in access.
//		return access.getComponentIdentifier();
//	}
//	
//	public IFuture<IExtensionInstance> getExtension(final String name)
//	{
//		final Future<IExtensionInstance>	ret	= new Future<IExtensionInstance>();
//		access.getExtension(name).addResultListener(new DelegationResultListener<IExtensionInstance>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IExtensionInstance>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getExtension(name).addResultListener(new DelegationResultListener<IExtensionInstance>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<String> getFileName(final String ctype)
//	{
//		final Future<String>	ret	= new Future<String>();
//		access.getFileName(ctype).addResultListener(new DelegationResultListener<String>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getFileName(ctype).addResultListener(new DelegationResultListener<String>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public String getLocalType()
//	{
//		// Cached in access.
//		return access.getLocalType();
//	}
//	
//	public IModelInfo getModel()
//	{
//		// Cached in access.
//		return access.getModel();
//	}
//	
//	public IFuture<String[]> getNFAllPropertyNames()
//	{
//		final Future<String[]>	ret	= new Future<String[]>();
//		access.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, String[]>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getNFAllPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<INFPropertyMetaInfo> getNFPropertyMetaInfo(final String name)
//	{
//		final Future<INFPropertyMetaInfo>	ret	= new Future<INFPropertyMetaInfo>();
//		access.getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, INFPropertyMetaInfo>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getNFPropertyMetaInfo(name).addResultListener(new DelegationResultListener<INFPropertyMetaInfo>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<Map<String, INFPropertyMetaInfo>> getNFPropertyMetaInfos()
//	{
//		final Future<Map<String, INFPropertyMetaInfo>>	ret	= new Future<Map<String, INFPropertyMetaInfo>>();
//		access.getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String, INFPropertyMetaInfo>>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Map<String, INFPropertyMetaInfo>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getNFPropertyMetaInfos().addResultListener(new DelegationResultListener<Map<String, INFPropertyMetaInfo>>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<String[]> getNFPropertyNames()
//	{
//		final Future<String[]>	ret	= new Future<String[]>();
//		access.getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, String[]>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.getNFPropertyNames().addResultListener(new DelegationResultListener<String[]>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T> IFuture<T> getNFPropertyValue(final String name)
//	{
//		final Future<T>	ret	= new Future<T>();
//		IFuture<T>	fut	= access.getNFPropertyValue(name);
//		fut.addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							IFuture<T>	fut	= access.getNFPropertyValue(name);
//							fut.addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T, U> IFuture<T> getNFPropertyValue(final String name, final U unit)
//	{
//		final Future<T>	ret	= new Future<T>();
//		IFuture<T>	fut	= access.getNFPropertyValue(name, unit);
//		fut.addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							IFuture<T>	fut	= access.getNFPropertyValue(name, unit);
//							fut.addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<Map<String, Object>> getResults()
//	{
//		// Cached in access.
//		return access.getResults();
//	}
//	
//	public IServiceProvider getServiceProvider()
//	{
//		return provider;
//	}
//	
////	public boolean isValid()
////	{
////		return access.isValid();
////	}
//	
//	public IFuture<Map<String, Object>> killComponent()
//	{
//		final Future<Map<String, Object>>	ret	= new Future<Map<String, Object>>();
//		access.killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Map<String, Object>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.killComponent().addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<Void> removeNFProperty(final String name)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		access.removeNFProperty(name).addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.removeNFProperty(name).addResultListener(new DelegationResultListener<Void>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T> IFuture<T> scheduleImmediate(final IComponentStep<T> step)
//	{
//		final Future<T>	ret	= new Future<T>();
//		access.scheduleImmediate(step).addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.scheduleImmediate(step).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T> IFuture<T> scheduleImmediate(final IComponentStep<T> step, final long delay)
//	{
//		final Future<T>	ret	= new Future<T>();
//		access.scheduleImmediate(step, delay).addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.scheduleImmediate(step, delay).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T> IFuture<T> scheduleStep(final IComponentStep<T> step)
//	{
//		final Future<T>	ret	= new Future<T>();
//		access.scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.scheduleStep(step).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public <T> IFuture<T> scheduleStep(final IComponentStep<T> step, final long delay)
//	{
//		final Future<T>	ret	= new Future<T>();
//		access.scheduleStep(step, delay).addResultListener(new DelegationResultListener<T>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, T>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.scheduleStep(step, delay).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public IFuture<Void> shutdownNFPropertyProvider()
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		access.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Void>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							access.shutdownNFPropertyProvider().addResultListener(new DelegationResultListener<Void>(ret));
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		return ret;
//	}
//	
//	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToEvents(final IFilter<IMonitoringEvent> filter, final boolean initial, final PublishEventLevel elm)
//	{
//		final SubscriptionIntermediateDelegationFuture<IMonitoringEvent>	ret	= new SubscriptionIntermediateDelegationFuture<IMonitoringEvent>();
//		ISubscriptionIntermediateFuture<IMonitoringEvent> fut = access.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
//		fut.addResultListener(new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IMonitoringEvent>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							ISubscriptionIntermediateFuture<IMonitoringEvent> fut = access.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(filter, initial, elm);
//							TerminableIntermediateDelegationResultListener<IMonitoringEvent> lis = new TerminableIntermediateDelegationResultListener<IMonitoringEvent>(ret, fut);
//							fut.addResultListener(lis);
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		
//		return ret;
//	}
//	
//	public ISubscriptionIntermediateFuture<Tuple2<String, Object>> subscribeToResults()
//	{
//		final SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>>	ret	= new SubscriptionIntermediateDelegationFuture<Tuple2<String, Object>>();
//		ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = access.subscribeToResults();
//		fut.addResultListener(new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut)
//		{
//			public void exceptionOccurred(Exception exception)
//			{
//				if(exception instanceof ComponentPersistedException)
//				{
//					resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<Tuple2<String, Object>>>(ret)
//					{
//						public void customResultAvailable(Void result)
//						{
//							ISubscriptionIntermediateFuture<Tuple2<String, Object>> fut = access.subscribeToResults();
//							TerminableIntermediateDelegationResultListener<Tuple2<String, Object>> lis = new TerminableIntermediateDelegationResultListener<Tuple2<String, Object>>(ret, fut);
//							fut.addResultListener(lis);
//						}
//					});
//				}
//				else
//				{
//					super.exceptionOccurred(exception);
//				}
//			}
//		});
//		
//		return ret;
//	}
//	
//	//-------- helper classes --------
//	
//	/**
//	 *  Shadow access to service provider.
//	 */
//	public class ShadowServiceProvider implements IServiceProvider, IServiceContainer // Todo: remove service container. only included for backwards compatibility with old JCC (ComponentTreeNode.java:447)
//	{
//		//-------- attributes --------
//		
//		protected String type	= access.getServiceProvider().getType();
//		
//		//-------- IServiceProvider interface --------
//		
//		public String getType()
//		{
//			return type;
//		}
//
////		public ITerminableIntermediateFuture<IService> getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
////		{
////			final TerminableIntermediateDelegationFuture<IService>	ret	= new TerminableIntermediateDelegationFuture<IService>();
////			try
////			{
////				ITerminableIntermediateFuture<IService>	fut	= access.getServiceProvider().getServices(manager, decider, selector);
////				fut.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(ret, fut));
////			}
////			catch(ComponentPersistedException e)
////			{
////				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IService>>(ret)
////				{
////					public void customResultAvailable(Void result)
////					{
////						try
////						{
////							ITerminableIntermediateFuture<IService>	fut	= access.getServiceProvider().getServices(manager, decider, selector);
////							fut.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(ret, fut));
////						}
////						catch(Exception e)
////						{
////							ret.setException(e);
////						}
////					}
////				});
////			}
////			catch(Exception e)
////			{
////				ret.setException(e);
////			}
////			return ret;			
////		}
//		
//		/**
//		 *  Get all services of a type.
//		 *  @param type The class.
//		 *  @return The corresponding services.
//		 */
//		public ITerminableIntermediateFuture<IService> getServices(final ClassInfo type, final String scope, final IAsyncFilter<IService> filter)
//		{
//			final TerminableIntermediateDelegationFuture<IService>	ret	= new TerminableIntermediateDelegationFuture<IService>();
//			try
//			{
//				ITerminableIntermediateFuture<IService>	fut = (ITerminableIntermediateFuture<IService>)access.getServiceProvider().getServices(type, scope, filter);
//				fut.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(ret, fut));
//			}
//			catch(ComponentPersistedException e)
//			{
//				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IService>>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						try
//						{
//							ITerminableIntermediateFuture<IService>	fut = (ITerminableIntermediateFuture<IService>)access.getServiceProvider().getServices(type, scope, filter);
//							fut.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(ret, fut));
//						}
//						catch(Exception e)
//						{
//							ret.setException(e);
//						}
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//			return ret;			
//		}
//		
//		/**
//		 *  Get all services of a type.
//		 *  @param type The class.
//		 *  @return The corresponding services.
//		 */
//		public IFuture<IService> getService(final ClassInfo type, final String scope, final IAsyncFilter<IService> filter)
//		{
//			final Future<IService>	ret	= new Future<IService>();
//			try
//			{
//				IFuture<IService>	fut = (IFuture<IService>)access.getServiceProvider().getService(type, scope, filter);
//				fut.addResultListener(new DelegationResultListener<IService>(ret));
//			}
//			catch(ComponentPersistedException e)
//			{
//				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IService>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						try
//						{
//							IFuture<IService>	fut = (IFuture<IService>)access.getServiceProvider().getService(type, scope, filter);
//							fut.addResultListener(new DelegationResultListener<IService>(ret));
//						}
//						catch(Exception e)
//						{
//							ret.setException(e);
//						}
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//			return ret;	
//		}
//			
//		/**
//		 *  Get a service per id.
//		 *  @param sid The service id.
//		 *  @return The corresponding services.
//		 */
//		public IFuture<IService> getService(final IServiceIdentifier sid)
//		{
//			final Future<IService>	ret	= new Future<IService>();
//			try
//			{
//				IFuture<IService>	fut = (IFuture<IService>)access.getServiceProvider().getService(sid);
//				fut.addResultListener(new DelegationResultListener<IService>(ret));
//			}
//			catch(ComponentPersistedException e)
//			{
//				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IService>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						try
//						{
//							IFuture<IService>	fut = (IFuture<IService>)access.getServiceProvider().getService(sid);
//							fut.addResultListener(new DelegationResultListener<IService>(ret));
//						}
//						catch(Exception e)
//						{
//							ret.setException(e);
//						}
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//			return ret;	
//		}
//
//		public IFuture<Collection<IService>> getDeclaredServices()
//		{
//			final Future<Collection<IService>>	ret	= new Future<Collection<IService>>();
//			
//			try
//			{
//				IFuture<Collection<IService>> fut = (IFuture<Collection<IService>>)access.getServiceProvider().getDeclaredServices();
//				fut.addResultListener(new DelegationResultListener<Collection<IService>>(ret));
//			}
//			catch(ComponentPersistedException e)
//			{
//				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IService>>(ret)
//				{
//					public void customResultAvailable(Void result)
//					{
//						try
//						{
//							IFuture<Collection<IService>> fut = (IFuture<Collection<IService>>)access.getServiceProvider().getDeclaredServices();
//							fut.addResultListener(new DelegationResultListener<Collection<IService>>(ret));
//						}
//						catch(Exception e)
//						{
//							ret.setException(e);
//						}
//					}
//				});
//			}
//			catch(Exception e)
//			{
//				ret.setException(e);
//			}
//			return ret;			
//			
//		}
//		
////		public IFuture<IServiceProvider> getParent()
////		{
////			final Future<IServiceProvider>	ret	= new Future<IServiceProvider>();
////			try
////			{
////				access.getServiceProvider().getParent().addResultListener(new DelegationResultListener<IServiceProvider>(ret));
////			}
////			catch(ComponentPersistedException e)
////			{
////				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, IServiceProvider>(ret)
////				{
////					public void customResultAvailable(Void result)
////					{
////						try
////						{
////							access.getServiceProvider().getParent().addResultListener(new DelegationResultListener<IServiceProvider>(ret));
////						}
////						catch(Exception e)
////						{
////							ret.setException(e);
////						}
////					}
////				});
////			}
////			catch(Exception e)
////			{
////				ret.setException(e);
////			}
////			return ret;
////		}
//
//		public IComponentIdentifier getId()
//		{
//			return getComponentIdentifier();
//		}
//
////		public IFuture<Collection<IServiceProvider>> getChildren()
////		{
////			final Future<Collection<IServiceProvider>>	ret	= new Future<Collection<IServiceProvider>>();
////			try
////			{
////				access.getServiceProvider().getChildren().addResultListener(new DelegationResultListener<Collection<IServiceProvider>>(ret));
////			}
////			catch(ComponentPersistedException e)
////			{
////				resurrect().addResultListener(new ExceptionDelegationResultListener<Void, Collection<IServiceProvider>>(ret)
////				{
////					public void customResultAvailable(Void result)
////					{
////						try
////						{
////							access.getServiceProvider().getChildren().addResultListener(new DelegationResultListener<Collection<IServiceProvider>>(ret));
////						}
////						catch(Exception e)
////						{
////							ret.setException(e);
////						}
////					}
////				});
////			}
////			catch(Exception e)
////			{
////				ret.setException(e);
////			}
////			return ret;
////		}
//
//		//-------- IServiceContainer methods (todo: remove) --------
//		
//		public IFuture<Void> start()
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public IFuture<Void> shutdown()
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public ServiceContainerPersistInfo	getPersistInfo()
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void	restore(ServiceContainerPersistInfo info)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public IFuture<Void>	addService(IInternalService service, ProvidedServiceInfo info)
//		{
//			throw new UnsupportedOperationException();
//		}
//
//		public IFuture<Void>	removeService(IServiceIdentifier sid)
//		{
//			throw new UnsupportedOperationException();
//		}
//			
//		public IService getProvidedService(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public IService getProvidedService(Class<?> clazz)
//		{
//			throw new UnsupportedOperationException();
//		}
//
//		public IService[] getProvidedServices(Class<?> clazz)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public Object getProvidedServiceRawImpl(Class<?> clazz)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public RequiredServiceInfo[] getRequiredServiceInfos()
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public RequiredServiceInfo getRequiredServiceInfo(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void setRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void addRequiredServiceInfos(RequiredServiceInfo[] requiredservices)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public INFMixedPropertyProvider getRequiredServicePropertyProvider(IServiceIdentifier sid)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public boolean hasRequiredServicePropertyProvider(IServiceIdentifier sid)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> getService(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> ITerminableIntermediateFuture<T> getServices(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> getService(String name, boolean rebind)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> ITerminableIntermediateFuture<T> getServices(String name, boolean rebind)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> getService(String name, boolean rebind, IAsyncFilter<T> filter)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> ITerminableIntermediateFuture<T> getServices(String name, boolean rebind, IAsyncFilter<T> filter)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> T getLastRequiredService(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> Collection<T> getLastRequiredServices(String name)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void addInterceptor(IServiceInvocationInterceptor interceptor, Object service, int pos)
//		{
//			throw new UnsupportedOperationException();
//		}
//
//		public void removeInterceptor(IServiceInvocationInterceptor interceptor, Object service)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public IServiceInvocationInterceptor[] getInterceptors(Object service)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void addMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void removeMethodInvocationListener(IServiceIdentifier sid, MethodInfo mi, IMethodInvocationListener listener)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public void notifyMethodListeners(IServiceIdentifier sid, boolean start, Object proxy, final Method method, final Object[] args, Object callid, ServiceInvocationContext context)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public boolean hasMethodListeners(IServiceIdentifier sid, MethodInfo mi)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> getService(Class<T> type, IComponentIdentifier cid)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> searchService(Class<T> type)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IFuture<T> searchService(Class<T> type, String scope)
//		{
//			throw new UnsupportedOperationException();
//		}
//
//		public <T> IFuture<T> searchServiceUpwards(Class<T> type)
//		{
//			throw new UnsupportedOperationException();
//		}
//
//		public <T> IIntermediateFuture<T> searchServices(Class<T> type)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public <T> IIntermediateFuture<T> searchServices(Class<T> type, String scope)
//		{
//			throw new UnsupportedOperationException();
//		}
//		
//		public LocalServiceRegistry getServiceRegistry() 
//		{
//			throw new UnsupportedOperationException();
//		}
//	}
}
