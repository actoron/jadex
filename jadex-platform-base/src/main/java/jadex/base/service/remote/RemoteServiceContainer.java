package jadex.base.service.remote;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicServiceContainer;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.IVisitDecider;
import jadex.service.SServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *  Remote service container for searching at a remote platform
 *  in the same way as on a the local one.
 */
public class RemoteServiceContainer extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The rms. */
	protected IRemoteServiceManagementService rms;
	
	//-------- constructors --------

	/**
	 *  Create a new service container.
	 */
	public RemoteServiceContainer(IComponentIdentifier id, IComponentAdapter adapter)
	{
		super(id);
		this.adapter = adapter;
	}
	
	//-------- interface methods --------
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture	getServices(ISearchManager manager, final IVisitDecider decider, final IResultSelector selector, final Collection results)
	{
		final Future ret = new Future();
		
		super.getServices(manager, decider, selector, results).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(rms==null)
				{
					ret.setResult(selector.getResult(results));
				}
				else
				{
					// Hack! Use user search manager.
					IComponentIdentifier cid = (IComponentIdentifier)getId();
					rms.getServiceProxies(cid, cid, SServiceProvider.sequentialmanager, decider, selector)
						.addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object res)
						{
							if(res instanceof Collection)
							{
								results.addAll((Collection)res);
							}
							else
							{
								results.add(res);
							}
							ret.setResult(selector.getResult(results));
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
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
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture start()
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(this, IRemoteServiceManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				System.out.println("Found rms: "+result);
				rms = (IRemoteServiceManagementService)result;	
				ret.setResult(null);
			}
			public void exceptionOccurred(Object source, Exception exception) 
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
		return new Future(null);
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
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RemoteServiceProvider (id="+getId()+")";
	}
}


//
//
//package jadex.base.service.remote;
///**
// *  Remote service container for searching at a remote platform
// *  in the same way as on a the local one.
// */
//public class RemoteServiceContainer implements IServiceContainer
//{
//	//-------- attributes --------
//	
//	/** The container id. */
//	protected IComponentIdentifier id;
//	
//	/** The component adapter. */
//	protected IComponentAdapter adapter;
//	
//	/** The rms. */
//	protected IRemoteServiceManagementService rms;
//	
//	//-------- constructors --------
//
//	/**
//	 *  Create a new service container.
//	 */
//	public RemoteServiceContainer(IComponentIdentifier id, IComponentAdapter adapter)
//	{
//		this.id = id;
//		this.adapter = adapter;
//	}
//	
//	//-------- interface methods --------
//	
//	/**
//	 *  Get all services of a type.
//	 *  @param type The class.
//	 *  @return The corresponding services.
//	 */
//	public IFuture	getServices(ISearchManager manager, final IVisitDecider decider, final IResultSelector selector, final Collection result)
//	{
//		final Future ret = new Future();
//		
//		if(rms==null)
//		{
//			ret.setResult(selector.getResult(result));
//		}
//		else
//		{
//			// Hack! Use user search manager.
//			IComponentIdentifier cid = (IComponentIdentifier)getId();
//			rms.getServiceProxies(cid, cid, SServiceProvider.sequentialmanager, decider, selector)
//				.addResultListener(new IResultListener()
//			{
//				public void resultAvailable(Object source, Object res)
//				{
//					if(res instanceof Collection)
//					{
//						result.addAll((Collection)res);
//					}
//					else
//					{
//						result.add(res);
//					}
//					ret.setResult(selector.getResult(result));
//				}
//				
//				public void exceptionOccurred(Object source, Exception exception)
//				{
//					ret.setException(exception);
//				}
//			});
//		}
//			
//		return ret;
//	}
//	
//	/**
//	 *  Get the parent service container.
//	 *  @return The parent container.
//	 */
//	public IFuture	getParent()
//	{
//		final Future ret = new Future();
//		
//		ret.setResult(adapter.getParent()!=null ? adapter.getParent().getServiceProvider() : null);
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the children service containers.
//	 *  @return The children containers.
//	 */
//	public IFuture	getChildren()
//	{
//		// no children to ensure that search stops here
//		final Future ret = new Future(null);
//		
////		adapter.getChildren().addResultListener(new IResultListener()
////		{
////			public void resultAvailable(Object source, Object result)
////			{
////				Collection	children	= null;
////				if(result!=null)
////				{
////					children	= new ArrayList();
////					for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
////					{
////						children.add(((IExternalAccess)it.next()).getServiceProvider());
////					}
////				}
////				ret.setResult(children);
////			}
////			public void exceptionOccurred(Object source, Exception exception)
////			{
////				ret.setException(exception);
////			}
////		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get the globally unique id of the provider.
//	 *  @return The id of this provider.
//	 */
//	public Object	getId()
//	{
//		return id;
//	}
//	
//	/**
//	 *  Start the service.
//	 *  @return A future that is done when the service has completed starting.  
//	 */
//	public IFuture start()
//	{
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(this, IRemoteServiceManagementService.class)
//			.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				System.out.println("Found rms: "+result);
//				rms = (IRemoteServiceManagementService)result;	
//				ret.setResult(null);
//			}
//			public void exceptionOccurred(Object source, Exception exception) 
//			{
//				ret.setException(exception);
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Shutdown the service.
//	 *  @return A future that is done when the service has completed its shutdown.  
//	 */
//	public IFuture shutdown()
//	{
//		return new Future(null);
//	}
//	
//	/**
//	 *  Add a service to the platform.
//	 *  If under the same name and type a service was contained,
//	 *  the old one is removed and shutdowned.
//	 *  @param name The name.
//	 *  @param service The service.
//	 */
//	public void addService(Class type, Object service)
//	{
//		throw new UnsupportedOperationException("Unsupported operation on remote container.");
//	}
//
//	/**
//	 *  Removes a service from the platform (shutdowns also the service).
//	 *  @param name The name.
//	 *  @param service The service.
//	 */
//	public void removeService(Class type, Object service)
//	{
//		throw new UnsupportedOperationException("Unsupported operation on remote container.");
//	}
//		
//	/**
//	 *  Get the string representation.
//	 *  @return The string representation.
//	 */
//	public String toString()
//	{
//		return "RemoteServiceProvider (id="+getId()+")";
//	}
//}