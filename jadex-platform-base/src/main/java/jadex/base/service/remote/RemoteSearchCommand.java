package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IService;
import jadex.service.IVisitDecider;
import jadex.service.SServiceProvider;
import jadex.service.ServiceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Command for performing a remote service search.
 */
public class RemoteSearchCommand implements IRemoteCommand
{
	//-------- attributes --------
	
	/** The providerid (i.e. the component to start with searching). */
	protected Object providerid;
	
	/** The serach manager. */
	protected ISearchManager manager;
	
	/** The visit decider. */
	protected IVisitDecider decider;
	
	/** The result selector. */
	protected IResultSelector selector;
	
	/** The callid. */
	protected String callid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new remote search command.
	 */
	public RemoteSearchCommand()
	{
	}

	/**
	 *  Create a new remote search command.
	 */
	public RemoteSearchCommand(Object providerid, ISearchManager manager, 
		IVisitDecider decider, IResultSelector selector, String callid)
	{
		this.providerid = providerid;
		this.manager = manager;
		this.decider = decider;
		this.selector = selector;
		this.callid = callid;
	}

	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(final IExternalAccess component, Map waitingcalls)
	{
		final Future ret = new Future();
		
		// fetch component via provider/component id
		final IComponentIdentifier compid = providerid!=null? 
			(IComponentIdentifier)providerid: component.getComponentIdentifier();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(compid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						
						// start serach on target component
//						System.out.println("rem search start: "+manager+" "+decider+" "+selector);
						exta.getServiceProvider().getServices(manager, decider, selector, new ArrayList())
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
//								System.out.println("rem search end: "+manager+" "+decider+" "+selector+" "+result);
								// Create proxy info(s) for service(s)
								Object content = null;
								if(result instanceof Collection)
								{
									try
									{
									List res = new ArrayList();
									for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
									{
										ServiceInfo tmp = (ServiceInfo)it.next();
										ProxyInfo pi = new ProxyInfo(component.getComponentIdentifier(), 
											tmp.getService().getServiceIdentifier(), tmp.getType());
										res.add(pi);
									}
									content = res;
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
								}
								else //if(result instanceof Object[])
								{
									ServiceInfo tmp = (ServiceInfo)result;
									content = new ProxyInfo(component.getComponentIdentifier(), 
										tmp.getService().getServiceIdentifier(), tmp.getType());
								}
								
								ret.setResult(new RemoteSearchResultCommand(content, null , callid));
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
							}
						});
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setResult(new RemoteSearchResultCommand(null, exception, callid));
			}
		}));
		
		return ret;
	}

	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public Object getProviderId()
	{
		return providerid;
	}

	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(Object providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the manager.
	 *  @return the manager.
	 */
	public ISearchManager getSearchManager()
	{
		return manager;
	}

	/**
	 *  Set the manager.
	 *  @param manager The manager to set.
	 */
	public void setSearchManager(ISearchManager manager)
	{
		this.manager = manager;
	}

	/**
	 *  Get the decider.
	 *  @return the decider.
	 */
	public IVisitDecider getVisitDecider()
	{
		return decider;
	}

	/**
	 *  Set the decider.
	 *  @param decider The decider to set.
	 */
	public void setVisitDecider(IVisitDecider decider)
	{
		this.decider = decider;
	}

	/**
	 *  Get the selector.
	 *  @return the selector.
	 */
	public IResultSelector getResultSelector()
	{
		return selector;
	}

	/**
	 *  Set the selector.
	 *  @param selector The selector to set.
	 */
	public void setResultSelector(IResultSelector selector)
	{
		this.selector = selector;
	}

	/**
	 *  Get the callid.
	 *  @return the callid.
	 */
	public String getCallId()
	{
		return callid;
	}

	/**
	 *  Set the callid.
	 *  @param callid The callid to set.
	 */
	public void setCallId(String callid)
	{
		this.callid = callid;
	}
	
}
