package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;

import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class MarsworldEnvironmentTest extends TestCase
{
	/**
	 *  Test method.
	 */
	public void	testCreationDeletion() throws Exception
	{
		long timeout	= 300000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "benchmarks_*",
//			"-kernels", "all",
//			"-logging_level", "java.util.logging.Level.INFO",
			"-libpath", "new String[]{\""+new File("../jadex-applications-bdi/target/classes").toURI().toURL().toString()+"\"}",
			"-awareness", "false",	// otherwise influences performance measure
			"-gui", "false", "-saveonexit", "false", "-welcome", "false", //"-autoshutdown", "true", 
			"-printpass", "false"}).get(sus, timeout);

		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		IFuture<IComponentIdentifier>	fut	= cms.createComponent(null, "jadex/bdi/examples/marsworld_classic/environment/Environment.agent.xml", null, null);
		IComponentIdentifier	cid	= fut.get(sus, timeout);
		Thread.sleep(35);
		cms.destroyComponent(cid).get(sus, timeout);
		
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
