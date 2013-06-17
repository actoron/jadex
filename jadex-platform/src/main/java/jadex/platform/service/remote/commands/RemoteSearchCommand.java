package jadex.platform.service.remote.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.TypeResultSelector;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Alias;
import jadex.micro.IMicroExternalAccess;
import jadex.platform.service.remote.RemoteReferenceModule;
import jadex.platform.service.remote.RemoteServiceManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Command for performing a remote service search.
 */
@Alias("jadex.base.service.remote.commands.RemoteSearchCommand")
public class RemoteSearchCommand extends AbstractRemoteCommand
{
	//-------- attributes --------

	/** The providerid (i.e. the component to start with searching). */
	protected IComponentIdentifier providerid;
	
	/** The serach manager. */
	protected ISearchManager manager;
	
	/** The visit decider. */
	protected IVisitDecider decider;
	
	/** The result selector. */
	protected IResultSelector selector;
	
	/** The callid. */
	protected String callid;
	
	/** The security level (set by postprocessing). */
	protected String securitylevel;

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
	public RemoteSearchCommand(IComponentIdentifier providerid, ISearchManager manager, 
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
	 *  Return security level determined by post-process.
	 */
	public String getSecurityLevel()
	{
		return securitylevel;
	}
	
	/**
	 *  Post-process a received command before execution
	 *  for e.g. setting security level.
	 */
	public IFuture<Void>	postprocessCommand(IInternalAccess component, RemoteReferenceModule rrm, final IComponentIdentifier target)
	{
		try
		{
			Security	sec	= null;
			// Try to find security level.
			// Todo: support other result selectors!?
			if(selector instanceof TypeResultSelector)
			{
				List<Class<?>>	classes	= new ArrayList<Class<?>>();
				classes.add(((TypeResultSelector)selector).getType());
				for(int i=0; sec==null && i<classes.size(); i++)
				{
					Class<?>	clazz	= classes.get(i);
					sec	= clazz.getAnnotation(Security.class);
					if(sec==null)
					{
						classes.addAll(Arrays.asList((Class<?>[])clazz.getInterfaces()));
						if(clazz.getSuperclass()!=null)
						{
							classes.add(clazz.getSuperclass());
						}
					}
				}
			}
			
			// Default to max security if not found.
			securitylevel	= sec!=null ? sec.value() : Security.PASSWORD;
			
			return IFuture.DONE;
		}
		catch(Exception e)
		{
			return new Future<Void>(e);
		}
	}

	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IIntermediateFuture execute(final IMicroExternalAccess component, RemoteServiceManagementService rsms)
	{
		final IntermediateFuture ret = new IntermediateFuture();
			
		SServiceProvider.getServiceUpwards(component.getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new IResultListener()
//			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)providerid).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						
						// start search on target component
//						System.out.println("rem search start: "+manager+" "+decider+" "+selector);
						exta.getServiceProvider().getServices(manager, decider, selector)
							.addResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
//								System.out.println("rem search end: "+manager+" "+decider+" "+selector+" "+result);
								// Create proxy info(s) for service(s)
								Object content = null;
								if(result instanceof Collection)
								{
									List res = new ArrayList();
									for(Iterator it=((Collection)result).iterator(); it.hasNext(); )
									{
										IService service = (IService)it.next();
//										RemoteServiceManagementService.getProxyInfo(component.getComponentIdentifier(), tmp, 
//											tmp.getServiceIdentifier(), tmp.getServiceIdentifier().getServiceType());
//										ProxyInfo pi = getProxyInfo(component.getComponentIdentifier(), tmp);
//										res.add(pi);
										res.add(service);
									}
									content = res;
								}
								else //if(result instanceof Object[])
								{
									IService service = (IService)result;
//									content = getProxyInfo(component.getComponentIdentifier(), tmp);
									content = service;
								}
								
//								ret.setResult(new RemoteResultCommand(content, null , callid, false));
								ret.addIntermediateResult(new RemoteResultCommand(null, content, null, callid, 
									false, null, getNonFunctionalProperties()));
								ret.setFinished();
							}
							
							public void exceptionOccurred(Exception exception)
							{
//								ret.setResult(new RemoteResultCommand(null, exception, callid, false));
								ret.addIntermediateResult(new RemoteResultCommand(null, null, exception, callid, 
									false, null, getNonFunctionalProperties()));
								ret.setFinished();
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						ret.setResult(new RemoteResultCommand(null, exception, callid, false));
						ret.addIntermediateResult(new RemoteResultCommand(null, null, exception, callid, 
							false, null, getNonFunctionalProperties()));
						ret.setFinished();
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				ret.setResult(new RemoteResultCommand(null, exception, callid, false));
				ret.addIntermediateResult(new RemoteResultCommand(null, null, exception, callid, 
					false, null, getNonFunctionalProperties()));
				ret.setFinished();
			}
		});
		
		return ret;
	}

	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public IComponentIdentifier getProviderId()
	{
		return providerid;
	}

	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderId(IComponentIdentifier providerid)
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
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RemoteSearchCommand(providerid=" + providerid + ", manager="
			+ manager + ", decider=" + decider + ", selector=" + selector
			+ ", callid=" + callid + ")";
	}
}
