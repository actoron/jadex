package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class ServiceCallTest extends TestCase
{
	/**
	 *  Test method.
	 */
	public void	testServiceCalls() throws Exception
	{
		long timeout	= 300000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "benchmarks_*",
//			"-kernels", "all",
//			"-logging_level", "java.util.logging.Level.INFO",
			"-libpath", "new String[]{\""+new File("../jadex-applications-micro/target/classes").toURI().toURL().toString()+"\"}",
			"-awareness", "false",	// otherwise influences performance measure
			"-gui", "false", "-saveonexit", "false", "-welcome", "false", //"-autoshutdown", "true", 
			"-printpass", "false"}).get(sus, timeout);
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		
		final Future<Collection<Tuple2<String, Object>>>	fut	= new Future<Collection<Tuple2<String, Object>>>();
		cms.createComponent(null, "jadex/micro/benchmarks/servicecall/ServiceCallAgent.class", null, new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<Tuple2<String, Object>>>(fut)
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
				// Agent created. Kill listener waits for result.
			}
		});
		
		fut.get(sus, timeout);
		
		try
		{
			platform.killComponent().get(sus, timeout);
		}
		catch(ComponentTerminatedException e)
		{
			// Platform autoshutdown already finished.			
		}
	}
}
