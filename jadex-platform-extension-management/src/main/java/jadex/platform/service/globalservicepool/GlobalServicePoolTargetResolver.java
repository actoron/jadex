package jadex.platform.service.globalservicepool;

import jadex.bridge.IExternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.CallMultiplexer;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.ServiceNotFoundException;

/**
 *  The service pool target resolver is the client side of a
 *  global pool manager. 
 *  It has the purpose the direct the call to a suitable service worker
 *  from the queue of workers.
 */
public class GlobalServicePoolTargetResolver implements ITargetResolver
{
	/** The cached target services. */
	protected List<IService> services;
	
	/** The position counter. */
	protected int position;
	
	/** The call multiplexer. */
	protected CallMultiplexer cpex = new CallMultiplexer();
	
	/** The search future. Only one search at the same time. */
	protected IIntermediateFuture<IService> searchfuture;
	
	/**
	 * 
	 */
	public IFuture<IService> determineTarget(IServiceIdentifier sid, IExternalAccess agent)
	{
		System.out.println("Called service pool resolver: "+sid+" "+(services==null? 0: services.size()));
		final Future<IService> ret = new Future<IService>();
		
		// todo: update in certain intervals
		
		// Case that we have services -> just pick one
		if(services!=null && services.size()>0)
		{
			if(position==services.size())
				position = 0;
			ret.setResult(services.get(position++));
		}
		// case we have no services and search is not running -> start search
		else if(services==null && searchfuture==null) // || timeout
		{
			searchfuture = searchServices(sid, agent);
			services = new ArrayList<IService>();
			
			searchfuture.addResultListener(new IIntermediateResultListener<IService>() 
			{
				boolean first=true;
				public void intermediateResultAvailable(IService result) 
				{
					services.add(result);
					if(first)
					{
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
					ret.setException(exception);
					searchfuture=null;
				}
			});
		}
		// Case the search is already running so just add a listener and take the first incoming result
		else if(searchfuture!=null)
		{
			searchfuture.addResultListener(new IIntermediateResultListener<IService>() 
			{
				boolean first=true;
				public void intermediateResultAvailable(IService result) 
				{
					if(first)
					{
						ret.setResult(result);
						first = false;
					}
				}
				
				public void finished() 
				{
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
	 * 
	 */
	protected IIntermediateFuture<IService> searchServices(final IServiceIdentifier sid, final IExternalAccess agent)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		SServiceProvider.getService(agent.getServiceProvider(), sid.getProviderId(), IGlobalPoolManagementService.class)
			.addResultListener(new IResultListener<IGlobalPoolManagementService>() 
		{
			public void resultAvailable(final IGlobalPoolManagementService pms) 
			{
				pms.getPoolServices(sid.getServiceType())
					.addResultListener(new IResultListener<Collection<IService>>() 
				{
					public void resultAvailable(Collection<IService> result) 
					{
						for(IService ser: result)
						{
							ret.addIntermediateResult(ser);
						}
						ret.setFinished();
					}
					
					public void exceptionOccurred(Exception exception) 
					{
						ret.setException(exception);
					}
				});
				
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
}
