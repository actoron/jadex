package jadex.base.service.remote;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.IResultSelector;
import jadex.bridge.service.ISearchManager;
import jadex.bridge.service.IVisitDecider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 *  Remote service container for searching at a remote platform
 *  in the same way as on a the local one.
 */
public class RemoteServiceContainer extends BasicServiceContainer
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
	public RemoteServiceContainer(IComponentIdentifier componentid, IComponentAdapter adapter)
	{
		super(adapter.getComponentIdentifier());
		this.adapter = adapter;
		this.componentid = componentid;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture	getServices(final ISearchManager manager, final IVisitDecider decider, final IResultSelector selector)
	{
		final IntermediateFuture ret = new IntermediateFuture();
		
		super.getServices(manager, decider, selector).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				// If should not search remotely or not inited (no rms)
				
				// Problem that the container calls itself the decider, could already
				// be done in search manager when this call is part of a search
				// But could also be called directly :-(
				if(!decider.searchNode(RemoteServiceContainer.this, null, RemoteServiceContainer.this, false, (Collection)result)
					|| rms==null || componentid==null)// || selector instanceof ComponentFactorySelector)
				{
					ret.setResult(result);
				}
				else
				{
					// Hack! Use user search manager.
					rms.getServiceProxies(componentid, SServiceProvider.sequentialmanager, decider, selector)
						.addResultListener(new IResultListener()
					{
						public void resultAvailable(Object res)
						{
							if(res instanceof Collection)
							{
								for(Iterator it=((Collection)res).iterator(); it.hasNext(); )
								{
									Object next = it.next();
//									System.out.println("add rem: "+next);
									if(!ret.getIntermediateResults().contains(next))
										ret.addIntermediateResult(next);
								}
							}
							else if(res!=null)
							{
//								System.out.println("add rem: "+res);
								if(!ret.getIntermediateResults().contains(res))
									ret.addIntermediateResult(res);
							}
//							ret.setResult(selector.getResult(results));
//							ret.setResult(results);
							ret.setFinished();
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							ret.setFinished();
							// todo: notify exception?
							ret.setException(exception);
						}
					});
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
			
		return ret;
	}
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public IFuture	getParent()
	{
		final Future ret = new Future();
		
		ret.setResult(adapter.getParent()!=null ? adapter.getParent().getServiceProvider() : null);
		
		return ret;
	}
	
	/**
	 *  Get the children service containers.
	 *  @return The children containers.
	 */
	public IFuture	getChildren()
	{
		// no children to ensure that search stops here
		final Future ret = new Future(null);
		
//		adapter.getChildren().addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				Collection	children	= null;
//				if(result!=null)
//				{
//					children	= new ArrayList();
//					for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
//					{
//						children.add(((IExternalAccess)it.next()).getServiceProvider());
//					}
//				}
//				ret.setResult(children);
//			}
//			public void exceptionOccurred(Object source, Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Create a service fetcher.
	 */
	public IRequiredServiceFetcher createServiceFetcher(String name)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get the type of the service provider (e.g. enclosing component type).
	 *  @return The type of this provider.
	 */
	public String	getType()
	{
		return "remote"; 
	}
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
//		System.out.println("Searching rms: "+getId());
		SServiceProvider.getService(this, IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println("Found rms: "+getId()+result);
				rms = (IRemoteServiceManagementService)result;	
				ret.setResult(null);
			}
			public void exceptionOccurred(Exception exception) 
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture shutdown()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void addService(Class type, Object service)
	{
		throw new UnsupportedOperationException("Unsupported operation on remote container.");
	}

	/**
	 *  Removes a service from the platform (shutdowns also the service).
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, Object service)
	{
		throw new UnsupportedOperationException("Unsupported operation on remote container.");
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchService(Class type, String scope)
	{
		throw new UnsupportedOperationException();
	}
	
	// todo: remove
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public IFuture searchServiceUpwards(Class type)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IIntermediateFuture searchServices(Class type, String scope)
	{
		throw new UnsupportedOperationException();
	}
		
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteServiceProvider(id="+getId()+")";
	}
	
	/**
	 *  Get the logger.
	 */
	protected Logger getLogger()
	{
		return adapter.getLogger();
	}
}

