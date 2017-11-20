package jadex.platform.service.registry;

import java.util.Iterator;
import java.util.List;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

public class ComponentSearcher 
{
	/** The global query delay, i.e. how often is polled. */
	protected long delay;
	
	/** The target cid. */
	protected IComponentIdentifier target;
	
	/** The targets. */
	protected List<IComponentIdentifier> targets;
	
	/** The superpeer search time. */
	protected long searchtime;
	
	/** The component. */
	protected IInternalAccess component;
	
	/**
	 *  Create a new service.
	 */
	public ComponentSearcher(IComponentIdentifier[] targets)
	{
		if(targets==null)
			this.targets = SUtil.arrayToList(targets);
	}
	
	/**
	 *  Get the superpeer. Triggers search in background if none available.
	 *  @return The superpeer.
	 */
	public IComponentIdentifier getTargetSync()
	{
		long ct = System.currentTimeMillis();
		if(target==null && searchtime<ct)
		{
			// Ensure that a delay is waited between searches
			searchtime = ct+delay;
			searchTarget().addResultListener(new IResultListener<IComponentIdentifier>()
			{
				public void resultAvailable(IComponentIdentifier result)
				{
//					System.out.println("Found superpeer: "+result);
					target = result;
//					addQueriesToNewSuperpeer();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("No target found");
				}
			});
		}
		else
		{
			System.out.println("No target search: "+searchtime+" "+ct);
		}
			
		return target;
	}
	
	/**
	 *  Find a supersuperpeer from a given list of superpeers.
	 */
	protected IFuture<IComponentIdentifier> getTarget(boolean force)
	{
		Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(force)
			target = null;
		
		if(target!=null)
		{
			ret.setResult(target);
		}
		else if(targets!=null)
		{
			searchTarget(targets.iterator()).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
			{
				public void customResultAvailable(IComponentIdentifier result) 
				{
					target = result;
					super.customResultAvailable(result);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Find a supersuperpeer from a given list of superpeers.
	 */
	protected IFuture<IComponentIdentifier> searchTarget(final Iterator<IComponentIdentifier> ssps)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(ssps!=null && ssps.hasNext())
		{
			final IComponentIdentifier sspcid = ssps.next();
			ISuperpeerRegistrySynchronizationService sps = SServiceProvider.getServiceProxy(component, ssps.next(), ISuperpeerRegistrySynchronizationService.class);
			isTargetOk(sspcid).addResultListener(new IResultListener<Boolean>()
			{
				public void resultAvailable(Boolean ok) 
				{
					if(ok.booleanValue())
					{
						ret.setResult(sspcid);
					}
					else
					{
						searchTarget(ssps);
					}
				}
				
				public void exceptionOccurred(Exception exception) 
				{
					searchTarget(ssps);
				}
			});
		}
		else
		{
			ret.setException(new ServiceNotFoundException("IISuperpeerRegistrySynchronizationService"));
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	public IFuture<Boolean> isTargetOk(IComponentIdentifier target)
	{
		return Future.TRUE;
	}
	
	/**
	 *  Search superpeer by sending requests to all known platforms if they host a IRegistrySynchronizationService service.
	 *  @return The cids of the superpeers.
	 */
	protected IFuture<IComponentIdentifier> searchTarget()
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		// Only search for super peer when super peer client agent is running. Otherwise the platform is itself a super peer (hack???)
//		// TODO: move super peer management to separate agent (common base agent also needed for relay and transport address super peer management).
//		if(getLocalServiceByClass(new ClassInfo(IPeerRegistrySynchronizationService.class))!=null)
//		{
////			System.out.println("ask all");
//			searchServiceAsyncByAskAll(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, cid, null))
//				.addResultListener(new ExceptionDelegationResultListener<ISuperpeerRegistrySynchronizationService, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(ISuperpeerRegistrySynchronizationService result)
//				{
////					System.out.println("found: "+result);
//					ret.setResult(((IService)result).getServiceIdentifier().getProviderId());
//				}	
//			});
//		}
//		else
//		{
//			ret.setException(new ComponentNotFoundException("No superpeer found."));
//		}
//		
		return ret;
	}
	
	/**
	 * 
	 */
	public IInternalAccess getComponent()
	{
		return component;
	}
}
