package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.TypeResultSelector;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Remote service container for searching at a remote platform
 *  in the same way as on a the local one.
 */
public class RemoteServiceContainer extends ComponentServiceContainer
{
	//-------- attributes --------
	
	/** The remote component id. */
	protected IComponentIdentifier componentid;
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The local rms service. */
	protected IRemoteServiceManagementService rms;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public RemoteServiceContainer(IComponentIdentifier remotecid, IComponentAdapter adapter, IInternalAccess instance)
	{
		super(adapter, "remote", instance, true);
		this.adapter = adapter;
		this.componentid = remotecid;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture<IService>	getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
//		if(selector instanceof TypeResultSelector && ((TypeResultSelector)selector).getType().getName().indexOf("IProxy")!=-1)
//		{
//			System.out.println(((TypeResultSelector)selector).getType().getName());
//			System.out.println("search: "+componentid);
//		}
		
		super.getServices(manager, decider, selector).addResultListener(new IResultListener<Collection<IService>>()
		{
			public void resultAvailable(Collection<IService> result)
			{
				// If should not search remotely or not inited (no rms)
				
				// Problem that the container calls itself the decider, could already
				// be done in search manager when this call is part of a search
				// But could also be called directly :-(
				if(!decider.searchNode(RemoteServiceContainer.this.getId(), RemoteServiceContainer.this.getId(), componentid, false, result)
					|| rms==null || componentid==null)// || selector instanceof ComponentFactorySelector)
				{
					ret.setResult(result);
				}
				else
				{
					if(result!=null)
					{
						for(IService ser: result)
						{
							ret.addIntermediateResult(ser);
						}
					}
					
					// Hack! Use user search manager.
					rms.getServiceProxies(componentid, SServiceProvider.sequentialmanager, decider, selector)
						.addResultListener(new IResultListener<Object>()
					{
						public void resultAvailable(Object res)
						{
//							System.out.println("remote search finished: "+componentid);
							if(res instanceof Collection)
							{
								for(Iterator<?> it=((Collection<?>)res).iterator(); it.hasNext(); )
								{
									Object next = it.next();
//									System.out.println("add rem: "+next);
									if(!ret.getIntermediateResults().contains(next))
										ret.addIntermediateResult((IService)next);
								}
							}
							else if(res!=null)
							{
//								System.out.println("add rem: "+res);
								if(!ret.getIntermediateResults().contains(res))
									ret.addIntermediateResult((IService)res);
							}
							
							if(res==null || res instanceof Collection && ((Collection)res).isEmpty())
							{
//								if(selector instanceof TypeResultSelector && ((TypeResultSelector) selector).getType().toString().indexOf("ILocalService")!=-1)
//								{
//									System.out.println("remote search has no result: "+componentid+", "+res);
//								}
							}

//							ret.setResult(selector.getResult(results));
//							ret.setResult(results);
							ret.setFinished();
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							if(selector instanceof TypeResultSelector && ((TypeResultSelector) selector).getType().toString().indexOf("ILocalService")!=-1)
//							{
//								System.out.println("remote search failed: "+componentid+", "+exception);
//							}
//							ret.setFinished();
							// todo: notify exception?
							ret.setException(exception);
						}
					});
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				if(selector instanceof TypeResultSelector && ((TypeResultSelector) selector).getType().toString().indexOf("ILocalService")!=-1)
//				{
//					System.out.println("remote search failed locally: "+componentid+", "+exception);
//				}
				ret.setException(exception);
			}
		});
			
		return ret;
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		
		super.start().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
//				System.out.println("Searching rms: "+getId());
				SServiceProvider.getService(RemoteServiceContainer.this, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new IResultListener<IRemoteServiceManagementService>()
				{
					public void resultAvailable(IRemoteServiceManagementService result)
					{
//						System.out.println("Found rms: "+getId()+result);
						rms = result;	
						ret.setResult(null);
					}
					public void exceptionOccurred(Exception exception) 
					{
						ret.setException(exception);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteServiceProvider(id="+getId()+")";
	}
	
	//-------- methods --------
	
	/**
	 *  Get the remote cid.
	 */
	public IComponentIdentifier	getRemoteComponentIdentifier()
	{
		return componentid;
	}
	
	/**
	 *  Set the remote cid.
	 */
	public void	setRemoteComponentIdentifier(IComponentIdentifier cid)
	{
		this.componentid	= cid;
	}
}

