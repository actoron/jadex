package jadex.tools.daemon;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class DaemonService implements IDaemonService
{
	/** The started platforms. */
	protected Map platforms; 
	
	/**
	 *  Create a new daemon service.
	 */
	public DaemonService()
	{
		platforms = new HashMap();
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(String[] args)
	{
		final Future ret = new Future();
		
		Starter.createPlatform(args).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess platform = (IExternalAccess)result;
				platforms.put(platform.getComponentIdentifier(), platform);
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		IExternalAccess platform = (IExternalAccess)platforms.get(cid);
		if(platform==null)
		{
			ret.setException(new RuntimeException("No platform found: "+cid));
		}
		else
		{
			SServiceProvider.getService(platform.getServiceProvider(), IComponentManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.destroyComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get all platforms.
	 *  @param The collection of platforms.
	 */
	public IFuture getPlatforms(final IComponentIdentifier cid)
	{
		return new Future();
	}
}
