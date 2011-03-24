package jadex.bridge.service.component;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *  The default service fetcher realizes the default 
 *  strategy for fetching a service.
 *  Allows for:
 *  
 *  - binding by searching service(s)
 *  - binding by name
 *  - binding by type
 *  
 *  - dynamic or static binding
 *  - creation of components
 *  - todo: recovery of failed services
 */
public class DefaultServiceFetcher implements IRequiredServiceFetcher
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
	//-------- methods --------
	
	/**
	 *  Get a required service.
	 */
	public IFuture getService(final RequiredServiceInfo info, RequiredServiceBinding bd, 
		final IServiceProvider provider, boolean rebind)
	{
		final Future ret = new Future();
		
		final RequiredServiceBinding binding = bd!=null? bd: info.getDefaultBinding();
		
		// Test if already bound.
		if(result==null || rebind)
		{
			// Search component.
			if(binding.getComponentName()!=null)
			{
				// Search service by component name.
				getExternalAccessByName(provider, info, binding).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess ea = (IExternalAccess)result;
						SServiceProvider.getService(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
							.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
					}
				});
			}
			else if(binding.getComponentType()!=null)
			{
				// Search service by component type.
				getExternalAccessesByType(provider, info, binding).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Collection coll = (Collection)result;
						if(coll!=null && coll.size()>0)
						{
							IExternalAccess ea = (IExternalAccess)coll.iterator().next();
							SServiceProvider.getService(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
								.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
						}
						else
						{
							ret.setException(new RuntimeException("No component found."));
						}
					}
				});
			}
			else
			{
				// Search service using search specification.
				SServiceProvider.getService(provider, info.getType(), binding.getScope())
					.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic())
				{
					public void exceptionOccurred(Exception exception)
					{
						createComponent(provider, info, binding, null).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess ea = (IExternalAccess)result;
								SServiceProvider.getService(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
									.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
							}
						});
					}
				});
			}
		}
		else
		{
			ret.setResult(result);
		}
		
		return ret;
	}
	
	/**
	 *  Get a required multi service.
	 */
	public IIntermediateFuture getServices(final RequiredServiceInfo info, 
		final RequiredServiceBinding bd, final IServiceProvider provider, boolean rebind)
	{
		final IntermediateFuture ret = new IntermediateFuture();
		
		final RequiredServiceBinding binding = bd!=null? bd: info.getDefaultBinding();
		
		// Test if already bound.
		// Test if already bound.
		if(result==null || rebind)
		{
			// Search component.
			if(binding.getComponentName()!=null)
			{
				// Search service by component name.
				getExternalAccessByName(provider, info, binding).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess ea = (IExternalAccess)result;
						SServiceProvider.getServices(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
							.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
					}
				});
			}
			else if(binding.getComponentType()!=null)
			{
				// Search service by component type.
				
				getExternalAccessesByType(provider, info, binding).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						Collection coll = (Collection)result;
						if(coll!=null && coll.size()>0)
						{
							CollectionResultListener lis = new CollectionResultListener(coll.size(), true, new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									Collection coll = (Collection)result;
									Collection ret = Collections.EMPTY_LIST;
									if(coll!=null && coll.size()>0)
									{
										Iterator it = coll.iterator();
										ret = (Collection)it.next();
										while(it.hasNext())
										{
											ret.addAll((Collection)it.next());
										}
									}
									super.customResultAvailable(ret);
								}
							});
							for(Iterator it=coll.iterator(); it.hasNext(); )
							{
								IExternalAccess ea = (IExternalAccess)it.next();
								SServiceProvider.getService(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
									.addResultListener(lis);
							}
						}
						else
						{
							ret.setException(new RuntimeException("No component found."));
						}
					}
				});
			}
			else
			{
				// Search service using search specification.
				SServiceProvider.getServices(provider, info.getType(), binding.getScope())
					.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic())
				{
					public void exceptionOccurred(Exception exception)
					{
						createComponent(provider, info, binding, null).addResultListener(new DelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess ea = (IExternalAccess)result;
								SServiceProvider.getServices(ea.getServiceProvider(), info.getType(), RequiredServiceInfo.SCOPE_LOCAL)
									.addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
							}
						});
					}
				});
			}
		}
		else
		{
			ret.setResult(result);
		}
		
		return ret;
	}
	
	/**
	 *  Get the external access of a component by its name.
	 */
	protected IFuture getExternalAccessByName(final IServiceProvider provider, final RequiredServiceInfo info, 
		final RequiredServiceBinding binding)
	{
		final Future ret = new Future();
		getExternalAccess(provider, binding.getComponentName()).addResultListener(new DelegationResultListener(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				// No component found with cid -> create.
				createComponent(provider, info, binding, null).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Get the external access of a component by type.
	 */
	protected IFuture getExternalAccessesByType(final IServiceProvider provider, final RequiredServiceInfo info, 
		final RequiredServiceBinding binding)
	{
		final Future ret = new Future();
		
		if(RequiredServiceInfo.SCOPE_PARENT.equals(binding.getScope()))
		{
			SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService cms = (IComponentManagementService)result;
					cms.getParent((IComponentIdentifier)provider.getId()).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							final IComponentIdentifier cid = (IComponentIdentifier)result;
							getChildExternalAccesses(cid, provider, info, binding).addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
						}
					});
				}
			});
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(binding.getScope()))
		{
			getChildExternalAccesses((IComponentIdentifier)provider.getId(), provider, info, binding).addResultListener(new StoreDelegationResultListener(ret, binding.isDynamic()));
		}
		else
		{
			ret.setException(new RuntimeException("Only parent or local scopes allowed."));
		}
		
		return ret;
	}
	
	/**
	 *  Get a fitting (of given type) child component.
	 */
	public IFuture getChildExternalAccesses(final IComponentIdentifier cid, final IServiceProvider provider, 
		final RequiredServiceInfo info, final RequiredServiceBinding binding)
	{
		final Future ret = new Future();
		SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						exta.getChildren(binding.getComponentType()).addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								Collection coll = (Collection)result;
								if(coll!=null && coll.size()>0)
								{
//									Iterator it = coll.iterator();
//									IExternalAccess child = (IExternalAccess)it.next();
									ret.setResult(coll);
								}
								else
								{
									createComponent(provider, info, binding, cid).addResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object result)
										{
											List ret = new ArrayList();
											ret.add(result);
											super.customResultAvailable(ret);
										}
									});
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								createComponent(provider, info, binding, cid).addResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										List ret = new ArrayList();
										ret.add(result);
										super.customResultAvailable(ret);
									}
								});
							}
						});
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Create component identifier from name.
	 */
	protected IFuture createComponentIdentifier(final IServiceProvider provider, final String name)
	{
		final Future ret = new Future();
		
		// todo: use parent names?!
		
		SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				ret.setResult(cms.createComponentIdentifier(name, name.indexOf("@")==-1));
			}
		});
			
		return ret;
	}
	
	/**
	 *  Get external access for component identifier.
	 */
	protected IFuture getExternalAccess(final IServiceProvider provider, final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get external access for component name.
	 */
	protected IFuture getExternalAccess(final IServiceProvider provider, final String name)
	{
		final Future ret = new Future();
		
		createComponentIdentifier(provider, name)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentIdentifier cid = (IComponentIdentifier)result;
				getExternalAccess(provider, cid).addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Create component and get external access for component.
	 */
	protected IFuture createComponent(final IServiceProvider provider, final RequiredServiceInfo info, 
		final RequiredServiceBinding binding, final IComponentIdentifier pa)
	{
		final Future ret = new Future();
		final IComponentIdentifier parent = pa!=null? pa: (IComponentIdentifier)provider.getId();
		if(binding.isCreate() && binding.getComponentType()!=null)
		{
			SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					final IComponentManagementService cms = (IComponentManagementService)result;
					cms.getExternalAccess(parent).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess exta = (IExternalAccess)result;
							exta.getFileName(binding.getComponentType()).addResultListener(new DelegationResultListener(ret)
							{
								public void customResultAvailable(Object result)
								{
									final String filename = (String)result;
									CreationInfo ci = new CreationInfo(parent);
									cms.createComponent(binding.getComponentName(), filename, ci, null)
										.addResultListener(new DelegationResultListener(ret)
									{
										public void customResultAvailable(Object result)
										{
											IComponentIdentifier cid = (IComponentIdentifier)result;
											getExternalAccess(provider, cid).addResultListener(new DelegationResultListener(ret));
										}
										public void exceptionOccurred(Exception exception)
										{
											exception.printStackTrace();
											super.exceptionOccurred(exception);
										}
									});
								}
							});
						}
					});
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("No creation possible"));
		}
		return ret;
	}
	
	/**
	 *  Simple listener that can store the result in a member variable.
	 */
	public class StoreDelegationResultListener extends DelegationResultListener
	{
		/** Flag if binding is dynamic. */
		protected boolean dynamic;
		
		/**
		 *  Create a new listener.
		 */
		public StoreDelegationResultListener(Future ret, boolean dynamic)
		{
			super(ret);
			this.dynamic = dynamic;
		}
		
		/**
		 *  Called when result is available.
		 */
		public void customResultAvailable(Object result)
		{
			if(dynamic)
				DefaultServiceFetcher.this.result = result;
			super.customResultAvailable(result);
		}	
	}
}


