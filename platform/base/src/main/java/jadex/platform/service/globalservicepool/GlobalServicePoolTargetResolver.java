package jadex.platform.service.globalservicepool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.ServiceNotFoundException;

import jadex.bridge.IExternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 *  The service pool target resolver is the client side of a
 *  global pool manager. 
 *  It has the purpose the direct the call to a suitable service worker
 *  from the queue of workers.
 *  
 *  The resolver is used by 
 *  a) the RemoteMethodInvocationHandler in case of RMI calls
 *  b) the IntelligentProxyInterceptor in case of local calls
 *  
 *  The ITargetResolver.TARGETRESOLVER constant is used by both
 *  to determine the redirection target. 
 */
public class GlobalServicePoolTargetResolver implements ITargetResolver
{
	//-------- attributes --------
	
	/** The cached target services. */
	protected IndexMap<IServiceIdentifier, IService> services = new IndexMap<IServiceIdentifier, IService>();
	
	/** The reported to be broken services. */
	protected Set<IServiceIdentifier> brokens;
	
	/** The position counter. */
	protected int position;
	
	/** The search future. Only one search at the same time. */
	protected IIntermediateFuture<IService> searchfuture;
	
	/** The current usage info. */
	protected Map<IServiceIdentifier, UsageInfo> usageinfos = new HashMap<IServiceIdentifier, UsageInfo>();
	
	//-------- methods --------
	
	/**
	 *  Determine the target of a call.
	 *  @param sid The service identifier of the original call.
	 *  @param agent The external access.
	 *  @return The new service that should be called instead of the original one.
	 */
	public IFuture<IService> determineTarget(final IServiceIdentifier sid, final IExternalAccess agent, IServiceIdentifier broken)
	{
//		System.err.println("-------------+++++++++++++--------------- determineTarget ");
//		System.out.println("Called service pool resolver: "+sid+" "+(services==null? 0: services.size()));
		final Future<IService> ret = new Future<IService>();

		// Clear all services in case a failure occurred
		if(broken!=null)
		{
			if(services!=null)
				services.removeKey(broken);
			if(brokens==null)
				brokens = new HashSet<IServiceIdentifier>();
			brokens.add(broken);
		}
		// todo: update in certain intervals
		
		boolean done = false;
		
		// Case that we have services -> just pick one
		if(services!=null && services.size()>0) // min two services
		{
			if(position>=services.size())
				position = 0;
			IService ser = services.get(position++);
			
			reportUsage(ser, agent, sid);
			ret.setResult(ser);
			done = true;
		}
		
		// case we have no services and search is not running -> start search
		if(services.size()<3 && searchfuture==null) // || timeout
		{
			done = true;
			searchfuture = searchServices(sid, agent);
//			services = new IndexMap<IServiceIdentifier, IService>();
			
			searchfuture.addResultListener(new IIntermediateResultListener<IService>() 
			{
				boolean first=true;
				public void intermediateResultAvailable(IService result) 
				{
					services.put(result.getServiceIdentifier(), result);
					if(first)
					{
						reportUsage(result, agent, sid);
						if(!ret.isDone())
							ret.setResult(result);
						first = false;
					}
				}
				
				public void finished() 
				{
					if(!ret.isDone())
						ret.setException(new ServiceNotFoundException());
					searchfuture = null;
				}
				
				public void resultAvailable(Collection<IService> result) 
				{
					for(IService ser: result)
					{
						intermediateResultAvailable(ser);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception) 
				{
					if(!ret.isDone())
						ret.setException(exception);
					searchfuture=null;
				}
			});
		}
		
		// Case the search is already running so just add a listener and take the first incoming result
		if(searchfuture!=null && !done)
		{
			searchfuture.addResultListener(new IIntermediateResultListener<IService>() 
			{
				boolean first=true;
				public void intermediateResultAvailable(IService result) 
				{
					if(first)
					{
						reportUsage(result, agent, sid);
						ret.setResult(result);
						first = false;
					}
				}
				
				public void finished() 
				{
//					System.out.println("received services: "+services);
					
					if(!ret.isDone())
						ret.setException(new ServiceNotFoundException());
				}
				
				public void resultAvailable(Collection<IService> result) 
				{
					for(IService ser: result)
					{
						intermediateResultAvailable(ser);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception) 
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Search for services a call can be redirected to.
	 *  Contacts the IGlobalPoolManagementService to get services.
	 */
	protected IIntermediateFuture<IService> searchServices(final IServiceIdentifier sid, final IExternalAccess agent)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		// Fetch the global pools management service via its component id.
		SServiceProvider.searchService(agent, new ServiceQuery<>(IGlobalPoolManagementService.class).setProvider(sid.getProviderId()))
			.addResultListener(new IResultListener<IGlobalPoolManagementService>() 
		{
			public void resultAvailable(final IGlobalPoolManagementService pms) 
			{
				pms.getPoolServices(sid.getServiceType(), brokens)
					.addResultListener(new IntermediateDelegationResultListener<IService>(ret));
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Save usage info and send it in certain intervals.
	 */
	protected IFuture<Void> reportUsage(IService ser, IExternalAccess agent, final IServiceIdentifier sid)
	{
		final Future<Void> ret = new Future<Void>();
		
		UsageInfo ui = usageinfos.get(ser.getServiceIdentifier());
		if(ui==null)
		{
			ui = new UsageInfo();
			usageinfos.put(ser.getServiceIdentifier(), ui);
			ui.setServiceIdentifier(ser.getServiceIdentifier());
			ui.setStartTime(System.currentTimeMillis());
			ui.setUsages(1);
			ret.setResult(null);
		}
		else
		{
			ui.setUsages(ui.getUsages()+1);
			
			// send a report every minute
			long time = System.currentTimeMillis()-ui.getStartTime();
			if(time>1000*60)
			{
				final Map<IServiceIdentifier, UsageInfo> uclone = new HashMap<IServiceIdentifier, UsageInfo>(usageinfos);
				// Start collecting anew
				usageinfos.clear();
				
				for(IServiceIdentifier sidi: usageinfos.keySet())
				{
					UsageInfo uii = uclone.get(sidi);
					long span = System.currentTimeMillis()-ui.getStartTime();
					// normalize usage by / timeinterval
					uii.setUsages(uii.getUsages()/(span/1000));
				}
				
				// send infos to global pool
				agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( sid.getProviderId()), IGlobalPoolManagementService.class)
					.addResultListener(new ExceptionDelegationResultListener<IGlobalPoolManagementService, Void>(ret) 
				{
					public void customResultAvailable(final IGlobalPoolManagementService pms) 
					{
						pms.sendUsageInfo(uclone).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			else
			{
//				System.out.println("no reporting: "+time);
				ret.setResult(null);
			}
		}
		
		return ret;
	}
}
