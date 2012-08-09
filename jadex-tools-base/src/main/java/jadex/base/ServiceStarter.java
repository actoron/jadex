package jadex.base;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.Collection;

/**
 *  Wrapper for starting Jadex as OS service.
 */
public class ServiceStarter
{
	protected static IExternalAccess	platform;
	
	/**
	 *  Start Jadex.
	 *  @param args Arguments to be passed to starter. 
	 */
	public static void main(String[] args)
	{
		// Start platform.
		platform	= Starter.createPlatform(args).get(new ThreadSuspendable());
		
		// Wait for platform to exit.
		final Future<Void>	exit	= new Future<Void>(); 
		IComponentManagementService	cms	= SServiceProvider.getService(platform.getServiceProvider(),
			IComponentManagementService.class).get(new ThreadSuspendable());
		cms.addComponentResultListener(new IResultListener<Collection<Tuple2<String,Object>>>()
		{
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
				exit.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exit.setException(exception);
			}
		}, platform.getComponentIdentifier());
		exit.get(new ThreadSuspendable());
	}
	
	/**
	 *  Method that can be called to shutdown the platform.
	 */
	public static void	shutdown()
	{
		platform.killComponent().get(new ThreadSuspendable());
	}
}
